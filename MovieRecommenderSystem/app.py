import os
import threading
from flask import Flask, request
from surprise import SVD
from surprise import Dataset
from surprise.model_selection import cross_validate
import pickle
import dvc.api

import sys
sys.path.insert(1, './')


from Pipeline import model_serving
from schema_check import validate_userid
from load_balancer_helper import load_models, load_config, monitor_config_and_models, get_model_id_for_user

# ----------------------------------- SETUP ---------------------------------- #

MODEL_DIR = "/home/team07/Milestone3/group-project-s23-The-hangover-Part-ML/Models"
CONFIG_PATH = '/home/team07/Milestone3/group-project-s23-The-hangover-Part-ML/Server/congif.json'
MOVIE_LIST_PATH = 'top_1000_movie_list_final.pkl'

app = Flask(__name__)

# ----------------------------------- INIT ----------------------------------- #

models = {}
user_model_mappings = {}

models = load_models(MODEL_DIR)
user_model_mappings = load_config(CONFIG_PATH)

# Start the configuration monitoring thread
def config_update_callback():
    global user_model_mappings
    user_model_mappings = load_config(CONFIG_PATH)

def models_update_callback():
    global models
    models = load_models(MODEL_DIR)

config_monitor_thread = threading.Thread(target=monitor_config_and_models, args=(CONFIG_PATH, MODEL_DIR, config_update_callback, models_update_callback))
config_monitor_thread.daemon = True
config_monitor_thread.start()


# Load movie list
with open(MOVIE_LIST_PATH, 'rb') as f:
    movie_ids = pickle.load(f)

# ---------------------------------- ROUTES ---------------------------------- #

@app.route('/recommend/<userid>')
def recommend_movies(userid):
    error = validate_userid(userid)
    if error:
        return error, 400

    model_id = get_model_id_for_user(user_model_mappings, userid)
    if model_id is None or model_id not in models:
        return f"Invalid model_id for user {userid}", 400
    print(model_id)
    model = models[model_id]
    result = model_serving.model_serve_pipeline(model, userid, movie_ids)
    
    #<Time>, <Movies list>, <UserID>, <Model ID>
    # TODO: @Praveen Save to DB

    return result, 200

# -------------------------------------------------------------------------- #

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=8000)