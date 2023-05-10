# Real-time Movie Recommendation System

## Overview
The goal of this project was to develop a real-time movie recommendation system using Singular Value Decomposition (SVD) for a movie streaming service scenario. The system was designed to achieve a low root-mean-square error (RMSE) on live ratings data streamed via Kafka. The project involved implementing an end-to-end pipeline for data processing, model retraining, and deployment through Docker with load balancing capabilities. Continuous integration and continuous deployment (CI/CD) were accomplished using GitHub Actions. Monitoring infrastructure was established with Grafana and Timescale Cloud DB to track recommendations and user telemetry. An Android app was developed, powered by MongoDB and custom analytics.

## Milestones
This project was divided into four milestones:

### Milestone 1: Recommendation Model and First Deployment
- Collected data from multiple sources and engineered features for learning
- Applied state-of-the-art machine learning tools - Singular Value Decomposition (SVD)
- Deployed the model inference service - using Flask
- Measured and compared multiple quality attributes of the model

### Milestone 2: Model and Infrastructure Quality
- Tested all components of the learning infrastructure - using unit tests
- Built an infrastructure to assess model and data quality - Timescale Cloud DB
- Built an infrastructure to evaluate a model in production - Online Evaluation
- Used continuous integration to test infrastructure and models - GitHub Actions


![Image 1](https://github.com/Praveenramesh0508/CMUProjects/blob/main/MovieRecommenderSystem/img2.png)

### Milestone 3: Monitoring and Continuous Deployment
- Deployed the model prediction service with containers and supported model updates without downtime - Docker
- Built and operated a monitoring infrastructure for system health and model quality - Grafana visualization and alerts to the Slack channel
- Built an infrastructure for experimenting in production - A/B testing
- Created infrastructure for automatic periodic retraining of models - automating pipelines using cron jobs
- Versioned and tracked provenance of training data and models - Data Version Control (DVC)

![Image 2](https://github.com/Praveenramesh0508/CMUProjects/blob/main/MovieRecommenderSystem/img1.png)

### Milestone 4: Fairness
- Analyzed the fairness of the system with concrete data

## Technologies
- SVD for recommendation
- Flask for model inference service
- Docker for deployment and containerization
- GitHub Actions for CI/CD
- Timescale Cloud DB for data quality assessment
- Grafana for monitoring and visualization
- MongoDB for Android app data storage and custom analytics
- Data Version Control (DVC) for data and model versioning
