import sys
import os

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import unittest
from unittest.mock import Mock, patch, mock_open
from surprise import SVDpp, Dataset
from model_offline_evaluation import *


class TestEvaluation(unittest.TestCase):

# ---------------------------------------------------------------------------- #

    @patch('builtins.open', mock_open(read_data=b'mock_model_data'))
    @patch('pickle.load')
    def test_load_model(self, pickle_load_mock):
        model = SVDpp()
        pickle_load_mock.return_value = model

        loaded_model = load_model('test_model.pkl')
        self.assertEqual(loaded_model, model)

# ---------------------------------------------------------------------------- #

    def test_make_predictions(self):
        model = Mock()
        dataset = [1, 2, 3]
        expected_predictions = [4, 5, 6]

        model.test.return_value = expected_predictions
        predictions = make_predictions(model, dataset)

        self.assertEqual(predictions, expected_predictions)

# ---------------------------------------------------------------------------- #

    def test_compute_rmse(self):
        predictions = Mock()
        accuracy_mock = Mock(return_value=0.95)

        with patch('model_offline_evaluation.accuracy.rmse', accuracy_mock):
            performance = compute_rmse(predictions)

        accuracy_mock.assert_called_once_with(predictions)
        self.assertEqual(performance, 0.95)

# ---------------------------------------------------------------------------- #

    @patch('model_offline_evaluation.load_model')
    @patch('model_offline_evaluation.make_predictions')
    @patch('model_offline_evaluation.compute_rmse')
    def test_evaluate_model_pipeline(self, compute_rmse_mock, make_predictions_mock, load_model_mock):
        testset = Mock()
        model = SVDpp()

        load_model_mock.return_value = model
        make_predictions_mock.return_value = 'predictions'
        compute_rmse_mock.return_value = 0.95

        performance = evaluate_model_pipeline(testset)

        load_model_mock.assert_called_once_with('movie_recommender_model_final.pkl')
        make_predictions_mock.assert_called_once_with(model, testset)
        compute_rmse_mock.assert_called_once_with('predictions')

        self.assertEqual(performance, 0.95)

# ---------------------------------------------------------------------------- #

if __name__ == '__main__':
    unittest.main()
