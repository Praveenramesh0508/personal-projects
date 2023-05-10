import sys
import os
import re

from data_collection import data_collection_pipeline
from data_processing import data_processing_pipeline
from model_training import train_model
from model_offline_evaluation import evaluate_model_pipeline


def get_dataset_name():
    # Find latest dataset number
    dataset_regex = r'^dataset_(\d+)\.csv$'
    dataset_numbers = []
    for filename in os.listdir(DATA_DIR):
        match = re.match(dataset_regex, filename)
        if match:
            dataset_numbers.append(int(match.group(1)))
    latest_dataset_number = max(dataset_numbers) if dataset_numbers else 0

    # Create new dataset name
    new_dataset_number = latest_dataset_number + 1
    new_csv_name = f"dataset_{new_dataset_number}.csv"

    return new_csv_name

def get_model_name():
    # Find latest model number
    model_regex = r'^model_v(\d+)\.pkl$'
    model_numbers = []
    for filename in os.listdir(MODEL_DIR):
        match = re.match(model_regex, filename)
        if match:
            model_numbers.append(int(match.group(1)))
    latest_model_number = max(model_numbers) if model_numbers else 0

    # Create new model name
    new_model_number = latest_model_number + 1
    new_model_name = f"model_v{new_model_number}.pkl"
    return new_model_name


if __name__ == '__main__':
    # Parse command-line arguments
    if len(sys.argv) < 2:
        print('Usage: python complete_pipeline.py <data_collection_duation in minutes>')
        sys.exit(1)

    MODEL_DIR = "/home/team07/Milestone3/group-project-s23-The-hangover-Part-ML/Models"
    DATA_DIR = "/home/team07/Milestone3/group-project-s23-The-hangover-Part-ML/Data"

    data_collection_duation = int(sys.argv[1])
    
    csv_name = get_dataset_name()
    model_name = get_model_name()

    # Create CSV file
    csv_path = os.path.join(DATA_DIR, csv_name)
    open(csv_path, 'a').close()

    # Collect data from Kafka
    print("Collecting data...")
    data_collection_pipeline(csv_file_path=csv_path, duration_min=data_collection_duation, verbose=False)
    print("Done collecting data")

    # Process the data and split into train and test sets
    print("Training model...")
    trainset, testset = data_processing_pipeline(csv_data_path=csv_path, test_size=0.25)

    # Train the model and save it to a file
    model_path = os.path.join(MODEL_DIR, model_name)
    train_model(trainset, saved_model_path=model_path)
    print("Saving model...")

    # Evaluate the model on the test set and print the RMSE
    rmse = evaluate_model_pipeline(testset, model_path=model_path)
    print('RMSE:', rmse)

    print("##################################################")
    print(f"Model saved at 'Models/{model_name}")
    print(f"Dataset saved at 'Data/{csv_name}")
