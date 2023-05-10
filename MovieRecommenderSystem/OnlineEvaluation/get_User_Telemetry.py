import os
from kafka import KafkaConsumer, KafkaProducer
import re
os.chdir('/home/team07/Milestone2/group-project-s23-The-hangover-Part-ML')


def data_collection_pipeline(csv_file_path_rating='kafka_user_telemetry_rating.csv',csv_file_path_time='kafka_user_telemetry_time.csv', topic='movielog7'):
        consumer = KafkaConsumer(
        topic,
        bootstrap_servers=['localhost:9092'],
        auto_offset_reset='latest',
        enable_auto_commit=True,
        auto_commit_interval_ms=1000
    )
        if os.path.exists(csv_file_path_rating):
                os.remove(csv_file_path_rating)
        
        if os.path.exists(csv_file_path_time):
                os.remove(csv_file_path_time)

        search_str = "/rate/"
        search_str_1="/m/"
        for message in consumer:
                try:
                    message = message.value.decode()
                    if "/rate/" in message:
                        index_ = message.index(search_str)
                        rating = message[-1:]
                        user_id=message.split(",")[1].strip()
                        user_id=re.sub(r"\D", "", user_id)
                        movie_id = message[index_ + len(search_str):-2]
                        movie_id = re.sub(r"[^a-zA-Z0-9+]", "", movie_id)
                        final_message=user_id+"," + movie_id + "," + rating
                        os.system(f"echo {final_message} >> {csv_file_path_rating}")



                    if "/m/" in message:
                            index_ = message.index(search_str_1)
                            time = message[message.rfind("/")+1:-4]
                            time=re.sub(r"\D", "", time)
                            user_id=message.split(",")[1].strip()
                            user_id=re.sub(r"\D", "", user_id)
                            movie_id = message[index_ +len(search_str_1): message.rfind("/")]
                            movie_id = re.sub(r"[^a-zA-Z0-9+]", "", movie_id)
                            final_message= user_id+ "," + movie_id + "," + time
                            os.system(f"echo {final_message} >> {csv_file_path_time}")
                
                except Exception as e:
                        # print(message)
                        # print(f"Error occurred while processing message: {message}")
                        # print(f"Error message: {str(e)}")
                        continue

        # for message in consumer:
        #         try:
        #             message = message.value.decode()
        #             if "/rate/" in message:
        #                 rating = message[-1:]
        #                 index_ = message.index(search_str)
        #                 movie_id = message[index_ + len(search_str):-2]
        #                 movie_id = re.sub(r"[^a-zA-Z0-9+]", "", movie_id)
        #                 final_message=message[:message.index("GET")] + movie_id + "," + rating
        #                 os.system(f"echo {final_message} >> {csv_file_path_rating}")



        #             if "/m/" in message:
        #                     time = message[message.rfind("/")+1:-4]
        #                     index_ = message.index(search_str_1)
        #                     movie_id = message[index_ +len(search_str_1): message.rfind("/")]
        #                     movie_id = re.sub(r"[^a-zA-Z0-9+]", "", movie_id)
        #                     final_message= message[:message.index("GET")] + movie_id + "," + time
        #                     os.system(f"echo {final_message} >> {csv_file_path_time}")
                
        #         except Exception as e:
        #                 # print(message)
        #                 # print(f"Error occurred while processing message: {message}")
        #                 # print(f"Error message: {str(e)}")
        #                 continue


if __name__ == '__main__':
       data_collection_pipeline("kafka_user_telemetry_rating.csv","kafka_user_telemetry_time.csv",)