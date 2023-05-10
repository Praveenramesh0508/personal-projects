import os
import psycopg2
from kafka import KafkaConsumer
from datetime import datetime
from dateutil import parser
import re
import time
from time import sleep

# Connect to the Timescale Cloud database
conn = psycopg2.connect(
    dbname="tsdb",
    user="tsdbadmin",
    password="xus2e6kh8iecv918",
    host="yxwiwu71o8.rvzh9hqg0d.tsdb.cloud.timescale.com",
    port="32943",
)

# Create a cursor object
cursor = conn.cursor()

cursor.execute("""DROP TABLE IF EXISTS user_rate;""")

# Create the user_telemetry table if it does not exist
cursor.execute("""
    CREATE TABLE IF NOT EXISTS user_rate (
        timestamp TIMESTAMP NOT NULL,
        user_id TEXT,
        movie_id TEXT,
        rate INTEGER,
        PRIMARY KEY (timestamp, user_id)
    );
""")

cursor.execute("""
    SELECT create_hypertable('user_rate', 'timestamp');
""")

# Commit the changes to the database
conn.commit()

# Create a Kafka consumer
consumer = KafkaConsumer(
    'movielog7',
    bootstrap_servers=['localhost:9092'],
    auto_offset_reset='latest',
    enable_auto_commit=True,
    auto_commit_interval_ms=1000,
    max_poll_records=1,
)

# Continuously consume the latest messages
while True:
    # Seek to the end of each partition to start consuming only the latest messages
    for tp in consumer.assignment():
        consumer.seek_to_end(tp)

    # Poll for the latest message
    records = consumer.poll(timeout_ms=1000)  # Adjust the timeout as needed

    for tp, messages in records.items():
        for message in messages:
            message = message.value.decode()
            if "/rate/" in message:
                try:
                    search_str_1 = "/rate/"
                    index_ = message.index(search_str_1)
                    timestamp=parser.parse(message.split(",")[0])
                    user_id = message.split(",")[1].strip()
                    movie_id = message.split("=")[0].split("/rate/")[1].strip()
                    rate = message.split("=")[1].strip()

                    cursor.execute("""
                        INSERT INTO user_rate (timestamp, user_id, movie_id, rate)
                        VALUES (%s, %s, %s, %s)
                        ON CONFLICT (timestamp, user_id) DO UPDATE
                        SET movie_id = excluded.movie_id,
                            rate = excluded.rate;
                    """, (timestamp, user_id, movie_id, rate))

                    # Commit the changes to the database
                    conn.commit()
                except Exception as e:
                    print(f"Error while processing message: {e}")
                    print(message)



