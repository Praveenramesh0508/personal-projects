import sys
import os

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import pickle
import unittest
import tempfile
import pandas as pd

from data_processing import read_data_from_csv, save_top_movies, get_train_test_split, data_processing_pipeline

class TestDataProcessing(unittest.TestCase):

# ---------------------------------------------------------------------------- #

    def test_read_data_from_csv(self):
        with tempfile.NamedTemporaryFile(mode='w', delete=False) as temp:
            temp.write('1613708204,1,101,4\n')
            temp.write('1613708304,2,102,3\n')
            temp.flush()

            df = read_data_from_csv(temp.name)
            os.unlink(temp.name)

        self.assertEqual(len(df), 2)
        self.assertEqual(df['userid'].iloc[0], 1)
        self.assertEqual(df['movieid'].iloc[1], 102)
        # Additional assertions can be added depending on the desired behavior

# ---------------------------------------------------------------------------- #

    def test_save_top_movies(self):
        df = pd.DataFrame({'userid': [1, 2], 'movieid': [101, 102], 'ratings': [4, 3]})
        with tempfile.NamedTemporaryFile(delete=False) as temp:
            save_top_movies(df, n_movies=1, output_file=temp.name)

            with open(temp.name, 'rb') as file:
                top_movies = pickle.load(file)
            os.unlink(temp.name)

        self.assertEqual(len(top_movies), 1)
        self.assertEqual(top_movies[0], 101)
        # Additional assertions can be added depending on the desired behavior

# ---------------------------------------------------------------------------- #

    def test_get_train_test_split(self):
        df = pd.DataFrame({
            'userid': [1, 2, 3, 4], 
            'movieid': [101, 102, 103, 104], 
            'ratings': [4, 3, 5, 6]
            })
        trainset, testset = get_train_test_split(df, test_size=0.5)

        self.assertIsNotNone(trainset)
        self.assertIsNotNone(testset)
        self.assertEqual(len(testset), 2)
        # Additional assertions can be added depending on the desired behavior
    

# ---------------------------------------------------------------------------- #
    
    def test_data_processing_pipeline(self):
        with tempfile.NamedTemporaryFile(mode='w', delete=False) as temp:
            temp.write('1613708204,1,101,4\n')
            temp.write('1613708304,2,102,3\n')
            temp.flush()

            trainset, testset = data_processing_pipeline(csv_data_path=temp.name, test_size=0.5)
            os.unlink(temp.name)

        self.assertIsNotNone(trainset)
        self.assertIsNotNone(testset)
        self.assertEqual(len(testset), 1)
        # Additional assertions can be added depending on the desired behavior


# ---------------------------------------------------------------------------- #

if __name__ == '__main__':
    unittest.main()


