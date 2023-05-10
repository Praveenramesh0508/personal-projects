import os
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import unittest
from unittest import mock
from app import app

class TestRecommendMoviesEndpoint(unittest.TestCase):
    def setUp(self):
        self.client = app.test_client()
        self.valid_user_id = '1234'
        self.invalid_user_id = 'abcd'
        
        # Create a mock model object
        self.mock_model = mock.MagicMock()
        
        # Patch the `pickle.load` function to return the mock model object
        self.patcher = mock.patch('pickle.load', return_value=self.mock_model)
        self.patcher.start()
        
    def tearDown(self):
        # Stop patching the `pickle.load` function
        self.patcher.stop()

    def test_valid_user_id(self):
        # Test a valid user ID
        response = self.client.get(f'/recommend/{self.valid_user_id}')
        
        self.assertEqual(response.status_code, 200)
    
    def test_invalid_user_id(self):
        # Test an invalid user ID
        response = self.client.get(f'/recommend/{self.invalid_user_id}')
        
        self.assertEqual(response.status_code, 400)

if __name__ == '__main__':
    unittest.main()
