import json
import os
import pickle
import threading
import time

def load_models(model_dir):
    model_files = [f for f in os.listdir(model_dir) if f.endswith('.pkl')]
    return {model_file: pickle.load(open(os.path.join(model_dir, model_file), 'rb')) for model_file in model_files}

def load_config(config_path):
    with open(config_path, 'r') as file:
        return json.load(file)

def monitor_config_and_models(config_path, model_dir, config_callback, models_callback):
    config_mtime = os.path.getmtime(config_path)
    model_dir_mtime = os.path.getmtime(model_dir)
    while True:
        time.sleep(5)  # Check for changes every 5 seconds
        new_config_mtime = os.path.getmtime(config_path)
        new_model_dir_mtime = os.path.getmtime(model_dir)
        
        if new_config_mtime != config_mtime:
            config_callback()
            config_mtime = new_config_mtime

        if new_model_dir_mtime != model_dir_mtime:
            models_callback()
            model_dir_mtime = new_model_dir_mtime

def get_model_id_for_user(user_model_mappings, userid):
    if user_model_mappings['default_model'] is not None:
        return user_model_mappings['default_model']

    for condition in user_model_mappings['conditions']:
        if eval(condition['expression']):
            return condition['model_id']
    return None
