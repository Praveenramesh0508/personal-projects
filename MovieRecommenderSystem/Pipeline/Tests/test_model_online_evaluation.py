import sys
import os

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import tempfile
import shutil
import unittest
from unittest.mock import MagicMock, patch
from model_online_evaluation import *

class TestModelOnlineEvaluation(unittest.TestCase):

# ---------------------------------------------------------------------------- #

    @classmethod
    def setUpClass(cls):
        cls.temp_dir = tempfile.mkdtemp()
        cls.test_predictions_csv = os.path.join(cls.temp_dir, 'test_predictions.csv')
        cls.test_telemetry_csv = os.path.join(cls.temp_dir, 'test_telemetry.csv')
        
        # Create test CSV files with sample data
        with open(cls.test_predictions_csv, 'w') as f:
            f.write(
                    "2023-03-24T01:01:38.157643,719481,recommendation request 17645-team07.isri.cmu.edu:8082, status 200, result: the+shawshank+redemption+1994, the+godfather+1972, star+wars+1977, one+flew+over+the+cuckoos+nest+1975, the+lord+of+the+rings+the+two+towers+2002, the+secret+in+their+eyes+2009, a+grand+day+out+1990, the+kings+speech+2010, the+lord+of+the+rings+the+fellowship+of+the+ring+2001, amlie+2001, a+man+escaped+1956, the+philadelphia+story+1940, wait+until+dark+1967, the+thin+man+1934, the+dark+knight+2008, pulp+fiction+1994, the+sea+inside+2004, the+lives+of+others+2006, das+boot+1981, the+bridge+on+the+river+kwai+1957, 145 ms\n" \
                    "2023-03-24T01:01:38.203734,66598,recommendation request 17645-team07.isri.cmu.edu:8082, status 200, result: the+lord+of+the+rings+the+two+towers+2002, the+shawshank+redemption+1994, a+grand+day+out+1990, the+lord+of+the+rings+the+fellowship+of+the+ring+2001, harakiri+1962, the+matrix+1999, das+boot+1981, life+itself+2014, city+of+god+2002, the+secret+in+their+eyes+2009, the+dark+knight+2008, the+princess+bride+1987, andrei+rublev+1966, the+celebration+1998, saving+private+ryan+1998, the+empire+strikes+back+1980, toy+story+3+2010, the+great+escape+1963, the+sea+inside+2004, to+kill+a+mockingbird+1962, 169 ms\n" \
                    "2023-03-24T01:01:38.324305,103599,recommendation request 17645-team07.isri.cmu.edu:8082, status 200, result: the+lord+of+the+rings+the+two+towers+2002, a+grand+day+out+1990, the+godfather+1972, rebecca+1940, the+shawshank+redemption+1994, the+matrix+1999, saving+private+ryan+1998, the+sixth+sense+1999, wait+until+dark+1967, to+kill+a+mockingbird+1962, ivans+childhood+1962, the+lord+of+the+rings+the+fellowship+of+the+ring+2001, on+the+waterfront+1954, los+olvidados+1950, amlie+2001, one+flew+over+the+cuckoos+nest+1975, the+good_+the+bad+and+the+ugly+1966, monty+python+and+the+holy+grail+1975, spirited+away+2001, the+lives+of+others+2006, 295 ms\n"   
                    )
        
        with open(cls.test_telemetry_csv, 'w') as f:
            f.write(
                    "719481,movie1, 30\n" \
                    "66598,movie2, 60\n" \
                    "103599,movie2, 20\n" \
                    )

    
# ---------------------------------------------------------------------------- #
    @classmethod
    def tearDownClass(cls):
        shutil.rmtree(cls.temp_dir)

# ---------------------------------------------------------------------------- #

    def test_read_prediction_data(self):
        predictions = read_prediction_data(self.test_predictions_csv)
        TOP_20 = "the+shawshank+redemption+1994, the+godfather+1972, star+wars+1977, one+flew+over+the+cuckoos+nest+1975, the+lord+of+the+rings+the+two+towers+2002, the+secret+in+their+eyes+2009, a+grand+day+out+1990, the+kings+speech+2010, the+lord+of+the+rings+the+fellowship+of+the+ring+2001, amlie+2001, a+man+escaped+1956, the+philadelphia+story+1940, wait+until+dark+1967, the+thin+man+1934, the+dark+knight+2008, pulp+fiction+1994, the+sea+inside+2004, the+lives+of+others+2006, das+boot+1981, the+bridge+on+the+river+kwai+1957"
        self.assertEqual(predictions.iloc[0]['user_id'], 719481)
        self.assertEqual(predictions.iloc[0]['top_20'], TOP_20)

# ---------------------------------------------------------------------------- #

    def test_read_telemetry_data(self):
        telemetry = read_telemetry_data(self.test_telemetry_csv)
        self.assertEqual(telemetry.iloc[0]['user_id'], 66598)
        self.assertEqual(telemetry.iloc[0]['movie_id'], 'movie2')
        self.assertEqual(telemetry.iloc[0]['mins'], 60)

# ---------------------------------------------------------------------------- #

    def test_merge_data(self):
        predictions = read_prediction_data(self.test_predictions_csv)
        telemetry = read_telemetry_data(self.test_telemetry_csv)
        merged_df = merge_data(predictions, telemetry)

        self.assertEqual(merged_df.iloc[0]['user_id'], 719481)
        self.assertEqual(merged_df.iloc[0]['movie_id'], 'movie1')
        self.assertEqual(merged_df.iloc[0]['watched'], False)

# ---------------------------------------------------------------------------- #

    def test_model_evaluation_pipeline(self):
        metric = model_evaluation_pipeline(self.test_predictions_csv, self.test_telemetry_csv)
        self.assertEqual(metric, 0)
        self.assertTrue(isinstance(metric, float))


    
# ---------------------------------------------------------------------------- #
    @patch('model_online_evaluation.KafkaConsumer')
    def test_data_collection_pipeline(self, mock_kafka_consumer):
        sample_messages = [MagicMock(value=b'recommendation: message 1'), MagicMock(value=b'recommendation: message 2')]

        mock_consumer_instance = mock_kafka_consumer.return_value
        mock_consumer_instance.__iter__.return_value = iter(sample_messages)

        with tempfile.NamedTemporaryFile(delete=False) as temp_file:
            try:
                data_collection_pipeline(csv_file_path=temp_file.name, topic='movielog7')

                with open(temp_file.name, 'r') as f:
                    lines = f.readlines()
                    self.assertEqual(len(lines), 2)
                    self.assertEqual(lines[0].strip(), 'recommendation: message 1')
                    self.assertEqual(lines[1].strip(), 'recommendation: message 2')
            finally:
                os.unlink(temp_file.name)

if __name__ == '__main__':
    unittest.main()
