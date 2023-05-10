import os
from kafka import KafkaConsumer, KafkaProducer
os.chdir('/home/team07/Milestone2/group-project-s23-The-hangover-Part-ML')


def data_collection_pipeline(csv_file_path='kafka_Service_Predictions.csv', topic='movielog7'):
        consumer = KafkaConsumer(
        topic,
        bootstrap_servers=['localhost:9092'],
        auto_offset_reset='latest',
        enable_auto_commit=True,
        auto_commit_interval_ms=1000
    )
        
        for message in consumer:
                message = message.value.decode()
                if "recommendation" in message:
                    os.system(f"echo {message} >> {csv_file_path}")



if __name__ == '__main__':
       data_collection_pipeline("experimentation_predictions.csv")