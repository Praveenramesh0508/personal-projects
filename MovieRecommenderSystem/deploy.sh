#!/bin/bash

echo "Closing any existing server on port 8082"

lsof -ti:8082 | xargs kill
run nohup.out

echo "opening python virtual environment"

python3 -m venv env;
source env/bin/activate;
pip3 install -r requirements.txt;

echo "Launching server ..."
nohup flask --app ./Server/app.py run --port 8082 --host=128.2.205.109  & > /dev/null
disown;

echo "Server running with pid $(lsof -ti:8082)"
