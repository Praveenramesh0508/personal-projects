# Project Name: P2P Lending Club Portfolio Optimization using Multi-Model ML Framework

Created a comprehensive machine learning framework for optimizing P2P lending portfolios by integrating an SVM classifier, which achieved 80% accuracy in predicting loan defaults, and a multi-layer perceptron regression model for determining the return on investment (ROI). This strategy enabled focused investments in high-return loans, fostering informed financial decision-making.

## Machine Learning Models
Various classification models were developed for predicting loan defaults, such as Logistic Regression, RandomForest, and SVM classifier. In addition, regression models for estimating returns were built using Linear Regression, RandomForest, and Neural networks.

## Investment Strategies
Four primary investment strategies were examined to establish an optimized lending portfolio, with the potential to explore additional approaches. These strategies include:

1. **Random**: Made investments in loans arbitrarily, without any specific considerations.
2. **Default-based**: Employed the previously created classification model to organize loans according to their estimated probability of default and invested in those with the lowest likelihood.
3. **Return-based**: Developed a regression model (e.g., linear, random forest, neural network regressor) to directly predict the calculated return on historical loans. Subsequently, out-of-sample loans were sorted based on their projected returns, and investments were made in loans with the highest predicted returns.
4. **Default & Return-based**: Designed two supplementary models – one for estimating the return on loans that did not default and another for determining the return on loans that defaulted. Using the default likelihood predicted by the classification model, the expected value of return from each future loan was computed, and investments were allocated to loans with the highest anticipated returns.


![Image 1](https://github.com/Praveenramesh0508/CMUProjects/blob/main/P2PLendingMultiModel/img1.png)

![Image 2](https://github.com/Praveenramesh0508/CMUProjects/blob/main/P2PLendingMultiModel/img2.png)

![Image 3](https://github.com/Praveenramesh0508/CMUProjects/blob/main/P2PLendingMultiModel/img3.png)