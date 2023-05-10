#!/bin/bash

echo "Closing any existing server on port 8083"

lsof -ti:8083 | xargs kill
run nohup.out

echo "opening python virtual environment"

python3 -m venv env;
source env/bin/activate;
pip3 install -r requirements.txt;

echo "Launching the experiment server ..."
nohup flask --app ./app run --port 8083 --host=128.2.205.109  & > /dev/null
disown;

echo "Server running with pid $(lsof -ti:8083)"
