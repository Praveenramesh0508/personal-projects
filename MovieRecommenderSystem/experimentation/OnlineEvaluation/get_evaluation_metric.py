import pandas as pd
import os
os.chdir('/home/team07/Milestone2/group-project-s23-The-hangover-Part-ML')
from ..data_split import split_by_gender, split_users

def get_metric(predictions_csv_path,telemetry_csv_path,user_details_path='user_details.csv'):
            '''
            Args:
                predictions_csv_path (str): The path to the csv file containing the predictions
                telemetry_csv_path (str): The path to the csv file containing the telemetry data
            
            '''

            df_prediction = pd.read_csv(predictions_csv_path, header=None)
            user_id = df_prediction.iloc[:, 1]
            top_20 = df_prediction.iloc[:, 4:24].astype(str).apply(','.join, axis=1)
            top_20 = top_20.str.replace('result: ', '').str.strip()
            predictions = pd.DataFrame({'user_id': user_id, 'top_20': top_20})

            df_telemetry = pd.read_csv(telemetry_csv_path, header=None,names=['user_id', 'movie_id', 'mins'])
            telemetry= df_telemetry.groupby(['user_id', 'movie_id'])['mins'].max().reset_index()

            merged_df = pd.merge(predictions, telemetry, on='user_id', how='inner')
            
            user_details = pd.read_csv(user_details_path, names=['userid','gender'])
            df = pd.merge(merged_df, user_details, on='userid', how='inner')
            df1, df2 = split_users(df)

            df1['watched'] = df1.apply(lambda row: row['movie_id'] in row['top_20'], axis=1)
            df2['watched'] = df2.apply(lambda row: row['movie_id'] in row['top_20'], axis=1)
            return(df1["watched"].mean(), df2["watched"].mean)


if __name__ == '__main__':
       mean = get_metric("kafka_Service_Predictions.csv","kafka_user_telemetry_time.csv")
       print("Percentage of users in group1 who watched at least one of the recommended movie:", mean[0])
       print("Percentage of users in group2 who watched at least one of the recommended movie:", mean[1])