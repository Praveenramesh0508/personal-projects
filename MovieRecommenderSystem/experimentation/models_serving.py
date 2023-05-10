import pandas as pd

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

def choose_model(user_id, user_details):
    '''
    Choose model based on user details

    Args:
        user_id (int): User ID
        user_details (dict): Dictionary containing user details

    Returns:
        model (surprise.prediction_algorithms.algo_base.AlgoBase): Trained model
    '''
    return user_details[user_id]['gender'] == 'M'

# ---------------------------------------------------------------------------- #

def get_user_details(user_details_path='user_details.csv'):
    user_details = pd.read_csv(user_details_path)
    return user_details

# ---------------------------------------------------------------------------- #

def model_serve_pipeline(model1, model2, user_id, list_of_movies, criterium: function = choose_model, user_details_path = 'user_details.csv'):
    '''
    Combine all model serving functions into a single pipeline

    Args:
        model (surprise.prediction_algorithms.algo_base.AlgoBase): Trained model
        user_id (int): User ID
        list_of_movies (list): List of movie IDs
        criterium (function): Function to decide which model to use

    Returns:
        output (str): Formatted output string
    '''
    user_details = get_user_details(user_details_path)
    model = model1 if choose_model(user_id, user_details) else model2
    predictions = generate_predictions(model, user_id, list_of_movies)
    sorted_predictions = sort_predictions(predictions)
    output = format_output(sorted_predictions)

    return output

