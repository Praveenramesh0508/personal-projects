#!/bin/bash

script_duration=1800 

echo "Running first script..."
timeout $script_duration python3 /home/team07/Milestone2/group-project-s23-The-hangover-Part-ML/OnlineEvaluation/get_Service_Predictions.py

echo "Waiting for 1 hours..."
sleep 1800

echo "Running second script..."
timeout $script_duration python3 /home/team07/Milestone2/group-project-s23-The-hangover-Part-ML/OnlineEvaluation/get_User_Telemetry.py


echo "Running third script..."
python3 /home/team07/Milestone2/group-project-s23-The-hangover-Part-ML/OnlineEvaluation/get_evaluation_metric.py

echo "Done..."
