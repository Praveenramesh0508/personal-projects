import pandas as pd
import pickle
from surprise import Reader, Dataset
from surprise.model_selection import train_test_split

#_______________________________________________________________________________________________________________________

def read_data_from_csv(csv_data_path='kafka_log_sample.csv'):
    """
    Read data from a CSV file and return a DataFrame.

    Args:
        csv_data_path (str, optional): Path and name of the CSV file. Defaults to 'kafka_log_sample.csv'.

    Returns:
        pd.DataFrame: DataFrame containing the data from the CSV file.
    """
    colnames = ['time', 'userid', 'movieid', 'ratings']
    df = pd.read_csv(csv_data_path, names=colnames, header=None)

    df['userid'] = pd.to_numeric(df['userid'], errors='coerce')
    df['ratings'] = pd.to_numeric(df['ratings'], errors='coerce')

    df = df.dropna()
    
    return df

#_______________________________________________________________________________________________________________________

def save_top_movies(df, n_movies=1000, output_file='../top_1000_movie_list_final.pkl'):
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

def data_processing_pipeline(csv_data_path='kafka_log_sample.csv', test_size=0.25):
    '''
    Reads data from a CSV file, prepares the dataset and splits it into a train and test set.

    Args:
        csv_data_path (str, optional): Path and name of the CSV file. Defaults to 'kafka_log_sample.csv'.
        test_size (float, optional): Size of the test set. Defaults to 0.25.

    Returns:
        tuple (Surprise.Dataset): Tuple containing the train and test sets.
    
    '''
    df = read_data_from_csv(csv_data_path)
    save_top_movies(df)
    trainset, testset = get_train_test_split(df, test_size=test_size)
    return trainset, testset
