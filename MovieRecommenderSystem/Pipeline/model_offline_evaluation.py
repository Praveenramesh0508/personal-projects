import pickle
from surprise import accuracy

#_______________________________________________________________________________________________________________________

def load_model(model_path='movie_recommender_model_final.pkl'):
    '''
    Loads a model from a pickle file.

    Args:
        model_path (str, optional): Path and name of the pickle file. Defaults to 'movie_recommender_model_final.pkl'.

    Returns:
        Surprise.AlgoBase: The loaded model.
    '''
    with open(model_path, 'rb') as file:
        model = pickle.load(file)
    return model


#_______________________________________________________________________________________________________________________

def make_predictions(model, dataset):
    '''
    Makes inferences on a dataset using a model.

    Args:
        model (Surprise.AlgoBase): The model to use for inferences.
        dataset (Surprise.Dataset): The dataset to make inferences on.

    Returns:
        list: List of predictions.
    '''
    predictions = model.test(dataset)
    return predictions


#_______________________________________________________________________________________________________________________

def compute_rmse(predictions):
    '''
    Computes the RMSE of a model.

    Args:
        predictions (list): List of predictions.

    Returns:
        float: The RMSE of the model.
    '''
    performance = accuracy.rmse(predictions)
    return performance


#_______________________________________________________________________________________________________________________


def evaluate_model_pipeline(testset, model_path='movie_recommender_model_final.pkl'):
    '''
    Complete pipeline for evaluating a model.

    Args:
        testset (Surprise.Dataset): The test set to use for evaluation.
        model_path (str, optional): Path and name of the pickle file. Defaults to 'movie_recommender_model_final.pkl'.

    Returns:
        float: The RMSE of the model.
    '''
    model = load_model(model_path)
    predictions = make_predictions(model, testset)
    performance = compute_rmse(predictions)
    return performance

