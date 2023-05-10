import sys
import os

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import unittest
from unittest.mock import MagicMock
import tempfile
from kafka import KafkaConsumer


from data_collection import init_kafka_consumer, process_kafka_message, write_message_to_csv, data_collection_pipeline

class TestDataCollection(unittest.TestCase):

# ---------------------------------------------------------------------------- #

    @unittest.mock.patch('data_collection.KafkaConsumer')
    def test_init_kafka_consumer(self, KafkaConsumerMock):
        consumer_mock = MagicMock(spec=KafkaConsumer)
        KafkaConsumerMock.return_value = consumer_mock

        consumer = init_kafka_consumer('test_topic')

        self.assertIsNotNone(consumer)
        self.assertEqual(consumer, consumer_mock)

        KafkaConsumerMock.assert_called_once_with(
            'test_topic',
            bootstrap_servers=['localhost:9092'],
            auto_offset_reset='earliest',
            enable_auto_commit=True,
            auto_commit_interval_ms=1000
        )

        # Additional assertions can be added depending on the desired behavior

# ---------------------------------------------------------------------------- #
    
    def test_process_kafka_message(self):
        #TODO: Confirm with @Praveen the message format
        message = "2023-03-21T20:34:49,770723,GET /rate/the+adventures+of+buckaroo+banzai+across+the+8th+dimension+1984=4"
        result = process_kafka_message(message)
        self.assertEqual(result, "2023-03-21T20:34:49,770723,the+adventures+of+buckaroo+banzai+across+the+8th+dimension+1984,4")

        message = "2023-03-21T20:34:49,770723,GET /invalid/the+adventures+of+buckaroo+banzai+across+the+8th+dimension+1984=4"
        result = process_kafka_message(message)
        self.assertIsNone(result)

        # Additional assertions can be added depending on the desired behavior

# ---------------------------------------------------------------------------- #

    def test_write_message_to_csv(self):
        # It's better to use a temporary file for testing file writing
        with tempfile.NamedTemporaryFile(delete=False) as temp:
            write_message_to_csv("12345,4", temp.name)
            temp.seek(0)
            content = temp.read().decode()
            self.assertEqual(content.strip(), "12345,4")
        # Additional assertions can be added depending on the desired behavior

# ---------------------------------------------------------------------------- #
if __name__ == '__main__':
    unittest.main()
