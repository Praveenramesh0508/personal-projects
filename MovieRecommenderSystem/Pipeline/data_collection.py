import os
import time
from kafka import KafkaConsumer, KafkaProducer


def init_kafka_consumer(topic='movielog7'):
    """
    Initialize a Kafka consumer with the given topic.
    
    Args:
        topic (str, optional): Topic name for the Kafka stream. Defaults to 'movielog7'.
    
    Returns:
        KafkaConsumer: Initialized Kafka consumer.
    """
    consumer = KafkaConsumer(
        topic,
        bootstrap_servers=['localhost:9092'],
        auto_offset_reset='earliest',
        enable_auto_commit=True,
        auto_commit_interval_ms=1000
    )
    return consumer

#_______________________________________________________________________________________________________________________

def process_kafka_message(message: str):
    """
    Process a Kafka message by extracting the relevant information.
    
    Args:
        message (str): Raw Kafka message.
    
    Returns:
        str: Processed message containing movie ID and rating, or None if the message is not valid.
    """
    search_str = "/rate/"
    if "/rate/" in message:
        rating = message[-1:]
        index_ = message.index(search_str)
        movie_id = message[index_ + len(search_str):-2]
        return message[:message.index("GET")] + movie_id + "," + rating
    return None

#_______________________________________________________________________________________________________________________

def write_message_to_csv(message: str, csv_file_path):
    '''
    Saves message to csv file.

    Args:
        message (str): Message to be saved.

    Returns:
        None
    '''
    os.system(f"echo {message} >> {csv_file_path}")

#_______________________________________________________________________________________________________________________


def data_collection_pipeline(csv_file_path='kafka_log_sample.csv', topic='movielog7', duration_min=10, verbose=False):
    '''
    Gets data from Kafka and saves it to a csv file.

    Args:
        csv_file_path (str, optional): Path to csv file. Defaults to 'kafka_log_sample.csv'.
        topic (str, optional): Topic name for the Kafka stream. Defaults to 'movielog7'.
        duration_min (int, optional): Duration in minutes for which to read data from Kafka. Defaults to 1.
        verbose (bool, optional): Print messages to console. Defaults to False.

    Returns:
        None
    '''
    consumer = init_kafka_consumer(topic)

    if verbose:
        print('Reading Kafka Broker')

    start_time = time.time()

    for message in consumer:
        try:
            message = message.value.decode()
            if verbose:
                print(message)
            processed_message = process_kafka_message(message)
            if processed_message:
                write_message_to_csv(processed_message, csv_file_path)

            if (time.time() - start_time) > (duration_min * 60):
                break
        except:
            pass
