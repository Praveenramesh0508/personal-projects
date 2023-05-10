def validate_userid(userid):
    if not userid.isdigit():
        return "Error: userid should only contain digits"

# ---------------------------------------------------------------------------- #

import unittest

class TestValidateUserId(unittest.TestCase):
    def test_valid_user_ids(self):
        self.assertIsNone(validate_userid("1234"))
        self.assertIsNone(validate_userid("4567"))
        self.assertIsNone(validate_userid("7890"))
    
    def test_invalid_user_ids(self):
        self.assertEqual(validate_userid("abcd"), "Error: userid should only contain digits")
        self.assertEqual(validate_userid("a4cd"), "Error: userid should only contain digits")
        self.assertEqual(validate_userid("1l112"), "Error: userid should only contain digits")
        self.assertEqual(validate_userid("22-44"), "Error: userid should only contain digits")
        self.assertEqual(validate_userid(""), "Error: userid should only contain digits")
        self.assertEqual(validate_userid(" "), "Error: userid should only contain digits")

if __name__ == '__main__':
    unittest.main()
