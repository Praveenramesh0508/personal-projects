# Group7: a movie recommendation service

## TODOs
 - [X] Add telemetry to pipeline @Praveen
 - [X] Add Online model evaluation @Praveen

 - [ ] Update Offline model evaluation of pipeline @Ameya

 - [X] Make API more robust @Prakhar
 - [X] Data quality checks (schema enforcement and detecting substantial drift) @Kedi? 

 - [X] Integrate with Jenkins/Github actions @Ankit

 - [X] Add tests for flask (ASK) @Prakhar
 - [ ] Shift saved data to a folder (Optional)




## DataScience
This folder contains all the notebooks used to develop the model


## Pipeline
This folder is a python package that contains all the parts of the DataScience pipeline:
1. Data collection [data_collection.py]
2. Data processing [data_processing.py]
3. Model training [model_training.py]
4. Model evaluation [model_eval.py]

Example of how to use the package:

``` python
from Pipeline import model_training

model_training.train_model(...)
```

## Server
This folder contains the server files
