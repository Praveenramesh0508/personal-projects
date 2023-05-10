import sys
import os

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import pickle
import unittest
import tempfile
import pandas as pd

from data_schema import MyDataModel, read_data_from_csv, get_data_statistics, detect_data_drift

class TestModelDataSchema(unittest.TestCase):
    
        def test_read_csv(self):
            with tempfile.NamedTemporaryFile(mode='w', delete=False) as temp:
                temp.write('2023-03-15T17:50:40,209021,the+godfather+1972,4\n')
                temp.write('2023-03-15T17:50:41,530878,the+lord+of+the+rings+the+two+towers+2002,4\n')
                temp.flush()

            df = read_data_from_csv(temp.name)
            os.unlink(temp.name)

            self.assertEqual(len(df), 2)
            self.assertEqual(df['userid'].iloc[0], 209021)
            self.assertEqual(df['movieid'].iloc[1], "the+lord+of+the+rings+the+two+towers+2002")
        
        def test_get_statistics(self):
            with tempfile.NamedTemporaryFile(mode='w', delete=False) as temp:
                temp.write('2023-03-15T17:50:40,209021,the+godfather+1972,4\n')
                temp.write('2023-03-15T17:50:41,530878,the+lord+of+the+rings+the+two+towers+2002,4\n')
                temp.flush()
            df = read_data_from_csv(temp.name)
            os.unlink(temp.name)
            stat = get_data_statistics(df)
            self.assertAlmostEqual(stat['rating_mean'], 4.0)
            self.assertAlmostEqual(stat['rating_std'], 0.0)
            self.assertAlmostEqual(stat['rating_min'], 4.0)
            self.assertAlmostEqual(stat['rating_max'], 4.0)
            self.assertEqual(stat['n_users'], 2)
            self.assertEqual(stat['n_movies'], 2)
            self.assertEqual(stat['n_ratings'], 2)
        
        def test_detect_data_drift(self):
            with tempfile.NamedTemporaryFile(mode='w', delete=False) as temp:
                temp.write('2023-03-15T17:50:40,209021,the+godfather+1972,4\n')
                temp.write('2023-03-15T17:50:41,530878,the+lord+of+the+rings+the+two+towers+2002,4\n')
                temp.flush()
            df = read_data_from_csv(temp.name)
            os.unlink(temp.name)
            stat = {'n_users': 2, 'n_movies': 2, 'n_ratings': 2, 'rating_mean': 4.0, 'rating_std': 0.0, 'rating_min': 4.0, 'rating_max': 4.0}
            self.assertFalse(detect_data_drift(df, stat))
        
        def test_detect_data_drift(self):
            with tempfile.NamedTemporaryFile(mode='w', delete=False) as temp:
                temp.write('2023-03-15T17:50:40,209021,the+godfather+1972,2\n')
                temp.write('2023-03-15T17:50:41,530878,the+lord+of+the+rings+the+two+towers+2002,4\n')
                temp.flush()
            df = read_data_from_csv(temp.name)
            os.unlink(temp.name)
            stat = {'n_users': 2, 'n_movies': 2, 'n_ratings': 2, 'rating_mean': 4.0, 'rating_std': 0.0, 'rating_min': 4.0, 'rating_max': 4.0}
            self.assertTrue(detect_data_drift(df, stat))
