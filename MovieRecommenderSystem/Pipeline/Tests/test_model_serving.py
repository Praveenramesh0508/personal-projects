import sys
import os

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import unittest
from unittest.mock import MagicMock, patch
from surprise import SVD
from model_serving import generate_predictions, sort_predictions, format_output, model_serve_pipeline

class TestModelServe(unittest.TestCase):

# ---------------------------------------------------------------------------- #

    def setUp(self):
        self.model = SVD()
        self.user_id = 1
        self.list_of_movies = [101, 102, 103, 104, 105]
        self.predictions = [(101, 4.0), (102, 3.5), (103, 2.0), (104, 4.5), (105, 1.0)]

# ---------------------------------------------------------------------------- #

    def test_generate_predictions(self):
        with patch.object(self.model, 'predict', return_value=MagicMock(est=4.0)) as mock_predict:
            predictions = generate_predictions(self.model, self.user_id, self.list_of_movies)

            self.assertEqual(len(predictions), len(self.list_of_movies))

            for prediction in predictions:
                self.assertIsInstance(prediction, tuple)
                self.assertIn(prediction[0], self.list_of_movies)
                
            self.assertEqual(mock_predict.call_count, len(self.list_of_movies))

# ---------------------------------------------------------------------------- #

    def test_sort_predictions(self):
        sorted_predictions = sort_predictions(self.predictions)
        self.assertEqual(len(sorted_predictions), len(self.predictions))
        self.assertEqual(sorted_predictions, [(104, 4.5), (101, 4.0), (102, 3.5), (103, 2.0), (105, 1.0)])

# ---------------------------------------------------------------------------- #

    def test_format_output(self):
        output = format_output(self.predictions)
        expected_output = '101,102,103,104,105'
        self.assertEqual(output, expected_output)

# ---------------------------------------------------------------------------- #

    def test_model_serve_pipeline(self):
        with patch('model_serving.generate_predictions', return_value=self.predictions) as mock_generate_predictions, \
             patch('model_serving.sort_predictions', return_value=self.predictions) as mock_sort_predictions, \
             patch('model_serving.format_output', return_value='101,102,103,104,105') as mock_format_output:

            output = model_serve_pipeline(self.model, self.user_id, self.list_of_movies)
            expected_output = '101,102,103,104,105'
            self.assertEqual(output, expected_output)

            mock_generate_predictions.assert_called_once_with(self.model, self.user_id, self.list_of_movies)
            mock_sort_predictions.assert_called_once_with(self.predictions)
            mock_format_output.assert_called_once_with(self.predictions)

# ---------------------------------------------------------------------------- #

if __name__ == '__main__':
    unittest.main()
