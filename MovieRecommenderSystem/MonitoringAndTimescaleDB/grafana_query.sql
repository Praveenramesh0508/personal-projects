-- Latency:
SELECT
  timestamp AT TIME ZONE 'EDT',
  latency
FROM
  movie_recommendation
WHERE
  timestamp >= $__timeFrom() AT TIME ZONE 'EDT'
  AND timestamp <= $__timeTo() AT TIME ZONE 'EDT';


-- Response status:

  SELECT
  timestamp AT TIME ZONE 'EDT',
  status
FROM
  movie_recommendation
WHERE
  timestamp >= $__timeFrom() AT TIME ZONE 'EDT'
  AND timestamp <= $__timeTo() AT TIME ZONE 'EDT';


-- Online Evaluation:

-- Number of users watching atleast one recommended movie:

WITH combined_data AS (
    SELECT
        A.user_id,
        A.movie_id,
        A.min_time,
        A.timestamp AT TIME ZONE 'EDT' AS a_timestamp_edt,
        B.recommended_movies,
        B.timestamp AT TIME ZONE 'EDT' AS b_timestamp_edt,
        CASE
            WHEN POSITION(A.movie_id::text IN B.recommended_movies) > 0 THEN 1
            ELSE 0
        END AS flag
    FROM
        (SELECT user_id, movie_id, timestamp, MIN(time) AS min_time
         FROM user_telemetry
         WHERE timestamp >= $__timeFrom() AT TIME ZONE 'EDT'
           AND timestamp <= $__timeTo() AT TIME ZONE 'EDT'
         GROUP BY user_id, movie_id, timestamp) A
    INNER JOIN
        (SELECT DISTINCT ON (user_id)
            user_id,
            recommended_movies,
            timestamp
         FROM movie_recommendation
         WHERE timestamp >= $__timeFrom() AT TIME ZONE 'EDT'
           AND timestamp <= $__timeTo() AT TIME ZONE 'EDT'
         ORDER BY user_id, timestamp) B
    ON A.user_id = B.user_id
    WHERE A.timestamp AT TIME ZONE 'EDT' > B.timestamp AT TIME ZONE 'EDT'
)
SELECT
    SUM(flag) as sum_flag
FROM combined_data;


-- Percentage of users watching atleast one recommended movie:

WITH combined_data AS (
    SELECT
        A.user_id,
        A.movie_id,
        A.min_time,
        A.timestamp AT TIME ZONE 'EDT' AS a_timestamp_edt,
        B.recommended_movies,
        B.timestamp AT TIME ZONE 'EDT' AS b_timestamp_edt,
        CASE
            WHEN POSITION(A.movie_id::text IN B.recommended_movies) > 0 THEN 1
            ELSE 0
        END AS flag
    FROM
        (SELECT user_id, movie_id, timestamp, MIN(time) AS min_time
         FROM user_telemetry
         WHERE timestamp >= $__timeFrom() AT TIME ZONE 'EDT'
           AND timestamp <= $__timeTo() AT TIME ZONE 'EDT'
         GROUP BY user_id, movie_id, timestamp) A
    INNER JOIN
        (SELECT DISTINCT ON (user_id)
            user_id,
            recommended_movies,
            timestamp
         FROM movie_recommendation
         WHERE timestamp >= $__timeFrom() AT TIME ZONE 'EDT'
           AND timestamp <= $__timeTo() AT TIME ZONE 'EDT'
         ORDER BY user_id, timestamp) B
    ON A.user_id = B.user_id
    WHERE A.timestamp AT TIME ZONE 'EDT' > B.timestamp AT TIME ZONE 'EDT'
)
SELECT
    AVG(flag) as avg_flag
FROM combined_data;




