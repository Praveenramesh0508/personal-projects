# Project Title: EntityMaster: Named Entity Recognition with Hidden Markov Models

## Project Description
In this project, the main objective was to implement a Named Entity Recognition (NER) system using Hidden Markov Models (HMMs) to classify named entities like proper nouns into predefined categories, such as person, location, or organization.

### Dataset
- WikiANN dataset - English and French subsets
- English subset: 14,000 training examples, 3,300 test examples
- French subset: 7,500 training examples, 300 test examples

### Implementation Details
- Learned HMM parameters given the training data.
- Implemented the forward-backward algorithm to perform a smoothing query.
- Used the smoothing query results to predict the hidden tags for a sequence of words.
- Evaluated the performance of the NER system on the test examples.

## Key Components
- Hidden Markov Models for NER
- Forward-backward algorithm for smoothing queries
- Prediction of hidden tags for word sequences
- Evaluation of NER system performance on test data

## Objective
The project aimed to develop a machine learning system capable of analyzing and interpreting a body of natural language text by recognizing named entities and classifying them into predefined categories, such as person, location, or organization. This system has the potential to be useful in applications like automatic summarization of news articles or designing trivia bots.
