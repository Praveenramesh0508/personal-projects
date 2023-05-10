import sys
import os

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import unittest
from unittest.mock import MagicMock, patch
from telemetry_collection import create_kafka_consumer, process_rating_message, process_time_message, append_to_file, telemetry_data_collection_pipeline

class TestTelemetryCollection(unittest.TestCase):

# ---------------------------------------------------------------------------- #
    
    def setUp(self):
        self.rating_csv_path = 'test_rating.csv'
        self.time_csv_path = 'test_time.csv'

        if os.path.exists(self.rating_csv_path):
            os.remove(self.rating_csv_path)

        if os.path.exists(self.time_csv_path):
            os.remove(self.time_csv_path)

# ---------------------------------------------------------------------------- #
    
    def test_append_to_file(self):
        test_content = "test content"
        append_to_file(self.rating_csv_path, test_content)

        with open(self.rating_csv_path, 'r') as file:
            content = file.read().strip()

        self.assertEqual(content, test_content)

# ---------------------------------------------------------------------------- #

    #TODO: @Praveen: Add tests
    def test_process_rating_message(self):
        message = "2023-03-21T20:34:49,770723,GET /rate/the+adventures+of+buckaroo+banzai+across+the+8th+dimension+1984=4"
        process_rating_message(message, self.rating_csv_path)

        with open(self.rating_csv_path, 'r') as file:
            content = file.read().strip()

        self.assertEqual(content, "770723,the+adventures+of+buckaroo+banzai+across+the+8th+dimension+1984,4")

# ---------------------------------------------------------------------------- #

    #TODO: @Praveen: Add tests
    def test_process_time_message(self):
        message = "2023-03-21T20:22:10,34665,GET /data/m/hans+christian+andersen+1952/39.mpg"
        process_time_message(message, self.time_csv_path)

        with open(self.time_csv_path, 'r') as file:
            content = file.read().strip()

        self.assertEqual(content, "34665,hans+christian+andersen+1952,39")


# ---------------------------------------------------------------------------- #
    #TODO: @Praveen: Add tests
    @patch('telemetry_collection.create_kafka_consumer')
    def test_telemetry_data_collection_pipeline(self, create_kafka_consumer_mock):

        mock_consumer = MagicMock()
        mock_consumer.__iter__.return_value = [
            MagicMock(value=b"2023-03-21T20:34:49,770723,GET /rate/the+adventures+of+buckaroo+banzai+across+the+8th+dimension+1984=4"),
            MagicMock(value=b"2023-03-21T20:22:10,34665,GET /data/m/hans+christian+andersen+1952/39.mpg")
        ]
        create_kafka_consumer_mock.return_value = mock_consumer

        telemetry_data_collection_pipeline(csv_file_path_rating=self.rating_csv_path, csv_file_path_time=self.time_csv_path, verbose=False)

        with open(self.rating_csv_path, 'r') as file:
            content = file.read().strip()
        self.assertEqual(content, "770723,the+adventures+of+buckaroo+banzai+across+the+8th+dimension+1984,4")

        with open(self.time_csv_path, 'r') as file:
            content = file.read().strip()
        self.assertEqual(content, "34665,hans+christian+andersen+1952,39")

# ---------------------------------------------------------------------------- #

if __name__ == '__main__':
    unittest.main()
