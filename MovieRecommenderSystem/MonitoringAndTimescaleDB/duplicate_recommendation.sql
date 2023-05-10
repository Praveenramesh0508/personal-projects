SELECT user_id, COUNT(DISTINCT movie_id) AS unique_movies_count, COUNT(movie_id) AS total_movies_count, ROUND(CAST(COUNT(DISTINCT movie_id) AS NUMERIC) / COUNT(movie_id) * 100, 2) AS duplicate_movies_percentage
FROM movie_recommendation
WHERE user_id IN (
    SELECT user_id
    FROM user_rate
    WHERE rate < 3
    GROUP BY user_id
    HAVING COUNT(*) > 5
)
GROUP BY user_id;
