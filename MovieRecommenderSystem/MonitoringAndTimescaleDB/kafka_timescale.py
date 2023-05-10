
import os
import psycopg2
from kafka import KafkaConsumer
from datetime import datetime
from dateutil import parser
import re

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

cursor.execute("""DROP TABLE IF EXISTS movie_recommendation;""")


# Create a hypertable if it does not exist
cursor.execute("""
    CREATE TABLE IF NOT EXISTS movie_recommendation (
        timestamp TIMESTAMP NOT NULL,
        user_id TEXT,
        host TEXT,
        status INTEGER,
        latency INTEGER,
        recommended_movies TEXT,
        PRIMARY KEY (timestamp, user_id)
    );
""")

cursor.execute("""
    SELECT create_hypertable('movie_recommendation', 'timestamp');
""")
            

# Commit the changes to the database
conn.commit()

# Create a Kafka consumer
consumer = KafkaConsumer(
    'movielog7',
    bootstrap_servers=['localhost:9092'],
    auto_offset_reset='latest',
    enable_auto_commit=True,
    auto_commit_interval_ms=1000
)

# Consume messages and store the data in the database
for message in consumer:
    message = message.value.decode()
    if "recommendation" in message:
        try:
            # Parse the message
            fields = message.split(',')
            timestamp = parser.parse(fields[0])
            user_id = fields[1]
            host = fields[2].split(':')[0]
            status=fields[3][7:]
            latency = int(fields[-1][:-3].strip())
            movies_str = message.split("result: ")[1].strip().split(",")[:-1]
            recommended_movies = ",".join(movies_str[:-1])

            # Store the data in the database
            cursor.execute("""
                INSERT INTO movie_recommendation (timestamp, user_id, host, status ,latency, recommended_movies)
                VALUES (%s, %s, %s, %s, %s, %s)
                ON CONFLICT (timestamp, user_id) DO UPDATE
                SET host = excluded.host,
                    status = excluded.status,
                    latency = excluded.latency,
                    recommended_movies = excluded.recommended_movies;
            """, (timestamp, user_id, host, status, latency, recommended_movies))

            # Commit the changes to the database
            conn.commit()
        except Exception as e:
            print(f"Error while processing message: {e}")
            print(message)


        



