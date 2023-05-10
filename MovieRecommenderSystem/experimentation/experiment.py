import pickle
from flask import Flask

import sys
sys.path.insert(1,'./')

from models_serving import model_serve_pipeline

MODEL_PATH = 'movie_recommender_model.pkl'
MOVIE_LIST_PATH = 'top_1000_movie_list_final.pkl'


app = Flask(__name__)

# ----------------------------------- INIT ----------------------------------- #
# Load saved model
with open(MODEL_PATH, 'rb') as file:
    model = pickle.load(file)

with open(MOVIE_LIST_PATH, 'rb') as f:
    movie_ids = pickle.load(f)


# ---------------------------------- ROUTES ---------------------------------- #

@app.route('/recommend/<userid>')
def recommend_movies(userid):
    return model_serve_pipeline(model, userid, movie_ids),200

# -------------------------------------------------------------------------- #


if __name__ == '__main__':
    app.run(debug=True,host='0.0.0.0',port=8083)
