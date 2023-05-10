import sys
import os

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import unittest
from unittest.mock import Mock, patch
from surprise import SVDpp
import tempfile
import os

from model_training import create_model, fit_model, save_model, train_model


class TestModelFunctions(unittest.TestCase):

# ---------------------------------------------------------------------------- #

    def test_create_model(self):
        model = create_model()
        self.assertIsInstance(model, SVDpp)
        # Additional assertions can be added depending on the desired behavior

# ---------------------------------------------------------------------------- #

    def test_fit_model(self):
        model = Mock(spec=SVDpp)  # Create a MagicMock with the same interface as SVDpp
        trainset = Mock()

        fit_model(model, trainset)
        model.fit.assert_called_once_with(trainset)
        # Additional assertions can be added depending on the desired behavior

# ---------------------------------------------------------------------------- #

    @patch('model_training.pickle.dump')
    def test_save_model(self, pickle_dump_mock):
        model = SVDpp()

        with tempfile.NamedTemporaryFile(delete=False) as temp:
            save_model(model, temp.name)

        with open(temp.name, 'rb') as file:
            self.assertEqual(pickle_dump_mock.call_args[0][0], model)

        os.unlink(temp.name)

        # Additional assertions can be added depending on the desired behavior

# ---------------------------------------------------------------------------- #

    @patch('model_training.create_model')
    @patch('model_training.fit_model')
    @patch('model_training.save_model')
    def test_train_model(self, save_model_mock, fit_model_mock, create_model_mock):
        trainset = Mock()
        model = SVDpp()

        create_model_mock.return_value = model
        train_model(trainset)

        create_model_mock.assert_called_once()
        fit_model_mock.assert_called_once_with(model, trainset)
        save_model_mock.assert_called_once_with(model, 'movie_recommender_model_final.pkl')

        # Additional assertions can be added depending on the desired behavior

# ---------------------------------------------------------------------------- #

if __name__ == '__main__':
    unittest.main()
