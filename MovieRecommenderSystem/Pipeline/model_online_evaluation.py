import os
import pandas as pd
from kafka import KafkaConsumer

# ---------------------------------------------------------------------------- #

def create_kafka_consumer(topic='movielog7'):
    '''
    Creates a Kafka Consumer to consume messages from the Kafka Topic
    
    Args:
        topic (str, optional): Kafka Topic Name

    Returns:
        consumer (KafkaConsumer): Kafka Consumer
    '''
    consumer = KafkaConsumer(
        topic,
        bootstrap_servers=['localhost:9092'],
        auto_offset_reset='latest',
        enable_auto_commit=True,
        auto_commit_interval_ms=1000
    )
    return consumer

# ---------------------------------------------------------------------------- #

def collect_data(consumer, csv_file_path='kafka_Service_Predictions.csv'):
    '''
    Collects the messages from the Kafka Topic and writes it to a CSV file

    Args:
        consumer (KafkaConsumer): Kafka Consumer
        csv_file_path (str, optional): Path to the CSV file where the messages will be written

    Returns:
        None
    '''
    for message in consumer:
        message = message.value.decode()
        if "recommendation" in message:
            os.system(f"echo {message} >> {csv_file_path}")

# ---------------------------------------------------------------------------- #

def data_collection_pipeline(csv_file_path='kafka_Service_Predictions.csv', topic='movielog7'):
    '''
    
    '''
    consumer = create_kafka_consumer(topic)
    collect_data(consumer, csv_file_path)

# ---------------------------------------------------------------------------- #

def read_prediction_data(predictions_csv_path):
    '''
    Reads the CSV file containing the predictions and returns a DataFrame with the user_id and top_20 movies

    Args:
        predictions_csv_path (str): Path to the CSV file containing the predictions

    Returns:
        predictions (DataFrame): DataFrame with the user_id and top_20 movies
    '''
    df_prediction = pd.read_csv(predictions_csv_path, header=None)
    user_id = df_prediction.iloc[:, 1]
    top_20 = df_prediction.iloc[:, 4:24].astype(str).apply(','.join, axis=1)
    top_20 = top_20.str.replace('result: ', '').str.strip()
    predictions = pd.DataFrame({'user_id': user_id, 'top_20': top_20})
    return predictions

# ---------------------------------------------------------------------------- #

def read_telemetry_data(telemetry_csv_path):
    '''
    Reads the CSV file containing the telemetry data and returns a DataFrame with the user_id, movie_id and the maximum minutes watched

    Args:
        telemetry_csv_path (str): Path to the CSV file containing the telemetry data

    Returns:
        telemetry (DataFrame): DataFrame with the user_id, movie_id and the maximum minutes watched
    '''
    df_telemetry = pd.read_csv(telemetry_csv_path, header=None, names=['user_id', 'movie_id', 'mins'])
    telemetry = df_telemetry.groupby(['user_id', 'movie_id'])['mins'].max().reset_index()
    return telemetry

# ---------------------------------------------------------------------------- #

def merge_data(predictions, telemetry):
    '''
    Merges the predictions and telemetry dataframes and returns a DataFrame with the user_id, movie_id, top_20 movies and a boolean column indicating if the movie was watched or not

    Args:
        predictions (DataFrame): DataFrame with the user_id and top_20 movies
        telemetry (DataFrame): DataFrame with the user_id, movie_id and the maximum minutes watched

    Returns:
        merged_df (DataFrame): DataFrame with the user_id, movie_id, top_20 movies and a boolean column indicating if the movie was watched or not
    '''
    merged_df = pd.merge(predictions, telemetry, on='user_id', how='inner')
    merged_df['watched'] = merged_df.apply(lambda row: row['movie_id'] in row['top_20'], axis=1)
    return merged_df

# ---------------------------------------------------------------------------- #

def model_evaluation_pipeline(predictions_csv_path, telemetry_csv_path):
    '''
    Evaluates the model by calculating the mean of the boolean column indicating if the movie was watched or not

    Args:
        predictions_csv_path (str): Path to the CSV file containing the predictions
        telemetry_csv_path (str): Path to the CSV file containing the telemetry data

    Returns:
        metric (float): Mean of the boolean column indicating if the movie was watched or not
    '''
    predictions = read_prediction_data(predictions_csv_path)
    telemetry = read_telemetry_data(telemetry_csv_path)
    merged_df = merge_data(predictions, telemetry)
    return merged_df["watched"].mean()

