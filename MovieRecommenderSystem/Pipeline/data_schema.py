from pydantic import BaseModel
import pandas as pd

class MyDataModel(BaseModel):
    userid: str
    movieid: str
    ratings: float

def read_data_from_csv(csv_data_path='kafka_log_sample.csv'):
    """
    Read data from a CSV file and return a DataFrame.

    Args:
        csv_data_path (str, optional): Path and name of the CSV file. Defaults to 'kafka_log_sample.csv'.

    Returns:
        pd.DataFrame: DataFrame containing the data from the CSV file.
    """
    df = pd.read_csv(csv_data_path, header=None, names=['time', 'userid', 'movieid', 'ratings'])
    df = df.astype({'time': 'datetime64', 'userid': 'int', 'movieid': 'str', 'ratings': 'float'})
    return df

# def validate_model_data(data_file, model):
#     # read csv file without column names
#     df = read_data_from_csv(data_file)

#     # attach column names
#     data = df[['userid', 'movieid', 'ratings']]
#     print(data.head())
#     try:
#         model_data = model(**data)
#     except ValueError as e:
#         print(e)

def detect_data_drift(df, statistics):
    """
    Detects data drift by comparing the data statistics of the current data to the data statistics of the training data.

    Args:
        df (pd.DataFrame): DataFrame containing the data.
        statistics (dict): Dictionary containing the data statistics of the training data.

    Returns:
        bool: True if data drift was detected, False otherwise.
    """
    # get data statistics of current data
    current_statistics = get_data_statistics(df)

    # compare data statistics of current data to data statistics of training data
    for key in statistics.keys():
        error = abs(statistics[key] - current_statistics[key]) / statistics[key]
        if error > 0.1:
            print("Data drift detected!")
            return True
    return False

def get_data_statistics(df):
    """
    Returns the data statistics of a DataFrame.

    Args:
        df (pd.DataFrame): DataFrame containing the data.

    Returns:
        dict: Dictionary containing the data statistics.
    """
    rating_mean = df['ratings'].mean()
    rating_std = df['ratings'].std()
    rating_min = df['ratings'].min()
    rating_max = df['ratings'].max()
    
    return {
        'n_users': df['userid'].nunique(),
        'n_movies': df['movieid'].nunique(),
        'n_ratings': df.shape[0],
        'rating_mean': rating_mean,
        'rating_std': rating_std,
        'rating_min': rating_min,
        'rating_max': rating_max
    }