from surprise import SVD
from surprise import Dataset
from surprise.model_selection import cross_validate

def generate_predictions(model, user_id, list_of_movies):
    '''Generate predictions for a given user and list of movies
    
    Args:
        model (surprise.prediction_algorithms.algo_base.AlgoBase): Trained model
        user_id (int): User ID
        list_of_movies (list): List of movie IDs
        
    Returns:
        predictions (list): List of tuples containing movie ID and predicted rating
    '''
    predictions = []

    for movie_id in list_of_movies:
        predicted_rating = model.predict(int(user_id), movie_id).est
        predictions.append((movie_id, predicted_rating))

    return predictions

# ---------------------------------------------------------------------------- #

def sort_predictions(predictions):
    '''
    Sort predictions by predicted rating in descending order

    Args:
        predictions (list): List of tuples containing movie ID and predicted rating

    Returns:
        predictions (list): List of tuples containing movie ID and predicted rating
    '''
    return sorted(predictions, key=lambda x: x[1], reverse=True)

# ---------------------------------------------------------------------------- #

def format_output(predictions, top_n=20):
    '''
    Format output for model serving

    Args:
        predictions (list): List of tuples containing movie ID and predicted rating
        top_n (int): Number of top predictions to return

    Returns:
        output (str): Formatted output string
    '''
    output = ''

    for idx, ids_preds in enumerate(predictions[:top_n]):
        ids, preds = ids_preds
        if idx != 0: output += ','
        output += str(ids)

    return output

# ---------------------------------------------------------------------------- #

def model_serve_pipeline(model, user_id, list_of_movies):
    '''
    Combine all model serving functions into a single pipeline

    Args:
        model (surprise.prediction_algorithms.algo_base.AlgoBase): Trained model
        user_id (int): User ID
        list_of_movies (list): List of movie IDs

    Returns:
        output (str): Formatted output string
    '''
    predictions = generate_predictions(model, user_id, list_of_movies)
    sorted_predictions = sort_predictions(predictions)
    output = format_output(sorted_predictions)

    return output

