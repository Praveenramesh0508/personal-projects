import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from sklearn.ensemble import IsolationForest

# Function to detect anomalies using Isolation Forest
def detect_anomalies(data, contamination=0.01):
    isolation_forest = IsolationForest(contamination=contamination)
    isolation_forest.fit(data)
    anomalies = isolation_forest.predict(data)
    return anomalies

# Load the telemetry data
data_ingestion_logs = pd.read_csv('data_ingestion_logs.csv')
model_training_logs = pd.read_csv('model_training_logs.csv')
user_activity_logs = pd.read_csv('user_activity_logs.csv')

# Preprocess the data
data_ingestion_logs['timestamp'] = pd.to_datetime(data_ingestion_logs['timestamp'])
model_training_logs['timestamp'] = pd.to_datetime(model_training_logs['timestamp'])
user_activity_logs['timestamp'] = pd.to_datetime(user_activity_logs['timestamp'])

# Analyze model performance
model_training_logs.set_index('timestamp', inplace=True)
model_training_logs_daily = model_training_logs.resample('D').mean()

# Detect anomalies in model performance
model_performance_anomalies = detect_anomalies(model_training_logs_daily)
model_training_logs_daily['anomaly'] = model_performance_anomalies

# Plot model performance with anomalies
fig, ax = plt.subplots()
model_training_logs_daily.plot(y='performance', ax=ax)
model_training_logs_daily[model_training_logs_daily['anomaly'] == -1].plot(y='performance', ax=ax, linestyle='', marker='o', color='red')
plt.show()

# Analyze user activity
user_activity_logs.set_index('timestamp', inplace=True)
user_activity_logs_hourly = user_activity_logs.resample('H').sum()

# Detect anomalies in user activity
user_activity_anomalies = detect_anomalies(user_activity_logs_hourly)
user_activity_logs_hourly['anomaly'] = user_activity_anomalies

# Plot user activity with anomalies
fig, ax = plt.subplots()
user_activity_logs_hourly.plot(y='activity', ax=ax)
user_activity_logs_hourly[user_activity_logs_hourly['anomaly'] == -1].plot(y='activity', ax=ax, linestyle='', marker='o', color='red')
plt.show()

# Investigate anomalies in user activity and data ingestion
combined_anomalies = user_activity_logs_hourly[user_activity_logs_hourly['anomaly'] == -1].merge(data_ingestion_logs_daily[data_ingestion_logs_daily['anomaly'] == -1], left_index=True, right_index=True, how='inner')
print("Combined anomalies in user activity and data ingestion:\n", combined_anomalies)
