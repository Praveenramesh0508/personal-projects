from surprise import SVDpp
import pickle

#_______________________________________________________________________________________________________________________

def create_model():
    '''
    Creates a model.

    Returns:
        Surprise.AlgoBase: The created model.
    '''
    model = SVDpp()
    return model

#_______________________________________________________________________________________________________________________

def fit_model(model, trainset):
    '''
    Fits a model to a dataset.

    Args:
        model (Surprise.AlgoBase): The model to fit.
        trainset (Surprise.Dataset): The dataset to fit the model to.

    Returns:
        None
    '''
    model.fit(trainset)

#_______________________________________________________________________________________________________________________

def save_model(model, saved_model_path):
    '''
    Saves a model to a pickle file.
    
    Args:
        model (Surprise.AlgoBase): The model to save.
        saved_model_path (str): Path and name of the pickle file. Defaults to 'movie_recommender_model_final.pkl'.
        
    Returns:
        None
    '''
    with open(saved_model_path, 'wb') as file:
        pickle.dump(model, file)

#_______________________________________________________________________________________________________________________

def train_model(trainset, saved_model_path='movie_recommender_model_final.pkl'):
    '''
    Complete pipeline for training a model.

    Args:
        trainset (Surprise.Dataset): The dataset to fit the model to.
        saved_model_path (str, optional): Path and name of the pickle file. Defaults to 'movie_recommender_model_final.pkl'.

    Returns:
        Surprise.AlgoBase: The trained model.
    '''

    model = create_model()
    fit_model(model, trainset)
    save_model(model, saved_model_path)

    return model
