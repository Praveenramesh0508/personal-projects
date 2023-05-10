import pandas as pd
import pickle
from surprise import Reader, Dataset
from surprise.model_selection import train_test_split
from splitting_rules import get_user_details, split_by_gender

#_______________________________________________________________________________________________________________________

def read_data_from_csv(data_path='kafka_log_sample.csv',user_details_path='user_details.csv'):
    """
    Read data from a CSV file and return a DataFrame.

    Args:
        csv_data_path (str, optional): Path and name of the CSV file. Defaults to 'kafka_log_sample.csv'.

    Returns:
        pd.DataFrame: DataFrame containing the data from the CSV file.
    """
    colnames = ['time', 'userid', 'movieid', 'ratings']
    data = pd.read_csv(data_path, names=colnames, header=None)
    user_details = pd.read_csv(user_details_path, names=['userid','gender'])
    df = pd.merge(data, user_details, on='userid', how='inner')
    return df

#_______________________________________________________________________________________________________________________

def save_top_movies(df, n_movies=1000, output_file='top_1000_movie_list_final.pkl'):
    '''
    Saves the top n movies to a pickle file.

    Args:
        df (pd.DataFrame): DataFrame containing the data.
        n_movies (int, optional): Number of movies to save. Defaults to 1000.

    Returns:
        None
    '''
    top_movies = df.groupby('movieid').size().sort_values(ascending=False)[:n_movies].index.tolist()
    with open(output_file, 'wb') as file:
        pickle.dump(top_movies, file)

#_______________________________________________________________________________________________________________________

def get_train_test_split(df, test_size):
    '''
    Splits a DataFrame into a train and test set.

    Args:
        df (pd.DataFrame): DataFrame containing the data.
        test_size (float): Size of the test set.

    Returns:
        tuple (Surprise.Dataset): Tuple containing the train and test sets.
    '''
    reader = Reader(rating_scale=(1, 5))
    dataset = Dataset.load_from_df(df[["userid", "movieid", "ratings"]], reader)

    trainset, testset = train_test_split(dataset, test_size=test_size)
    return trainset, testset

#_______________________________________________________________________________________________________________________

def split_by_gender(row):
    # divide testset into male and female
    return row['gender'] == 'M'

#_______________________________________________________________________________________________________________________

def split_users(df, criterium: function=split_by_gender):
    '''
    Splits a DataFrame into two DataFrames based on a given criterium.

    Args:
        df (pd.DataFrame): DataFrame containing the data.
        criterium (function): Function that returns a boolean value.

    Returns:
        tuple (pd.DataFrame): Tuple containing the two DataFrames.
    '''
    mask = df.apply(criterium, axis=1)
    return df[mask], df[~mask]
    

#_______________________________________________________________________________________________________________________

def data_processing_pipeline(data_path='kafka_log_sample.csv',user_details_path='user_details.csv', test_size=0.25, criterium: function = split_by_gender):
    '''
    Reads data from a CSV file, prepares the dataset and splits it into a train and test set.

    Args:
        csv_data_path (str, optional): Path and name of the CSV file. Defaults to 'kafka_log_sample.csv'.
        test_size (float, optional): Size of the test set. Defaults to 0.25.

    Returns:
        tuple (Surprise.Dataset): Tuple containing the train and test sets.
    
    '''
    df = read_data_from_csv(data_path, user_details_path)
    save_top_movies(df)
    df1, df2 = split_users(df, criterium)
    trainset1, testset1 = get_train_test_split(df1, test_size=test_size)
    trainset2, testset2 = get_train_test_split(df2, test_size=test_size)
    return trainset1, testset1, trainset2, testset2
