import numpy as np

import numpy as np
import sys



def sigmoid(x):
    """
    Implementation of the sigmoid function.

    Parameters:
        x (str): Input np.ndarray.

    Returns:
        An np.ndarray after applying the sigmoid function element-wise to the
        input.
    """
    e = np.exp(x)
    return e / (1 + e)


def train(theta, X, y, num_epoch, learning_rate):
    for j in range(num_epoch):
        for i in range(len(X)):
            theta = theta - learning_rate * (X[i] * (sigmoid(np.dot(theta, X[i])) - y[i]))
    #             print(theta)
    #             print("***",j)

    return theta


def predict(theta, X):
    predicted_labels = np.empty((0, len(X)), float)
    for i in range(len(X)):
        predicted_labels = np.append(predicted_labels, sigmoid(np.dot(theta, X[i])))

    for i in range(len(predicted_labels)):
        if predicted_labels[i] >= 0.5:
            predicted_labels[i] = 1

        else:
            predicted_labels[i] = 0

    return predicted_labels


def compute_error(y_pred, y):
    error_rate = sum(y_pred != y) / len(y)
    return error_rate


if __name__ == '__main__':
    train_data = np.loadtxt(sys.argv[1], delimiter='\t', comments=None, encoding='utf-8',
                            dtype=float)
    test_data = np.loadtxt(sys.argv[3], delimiter='\t', comments=None, encoding='utf-8',
                           dtype=float)
    val_data = np.loadtxt(sys.argv[2], delimiter='\t', comments=None, encoding='utf-8',
                          dtype=float)

    train_X = train_data[:, 1:]
    train_y = train_data[:, 0]
    test_X = test_data[:, 1:]
    test_y = test_data[:, 0]
    val_X = val_data[:, 1:]
    val_y = val_data[:, 0]
    train_X = np.append(np.ones((len(train_X), 1), dtype=float), train_X, axis=1)
    test_X = np.append(np.ones((len(test_X), 1), dtype=float), test_X, axis=1)
    val_X = np.append(np.ones((len(val_X), 1), dtype=float), val_X, axis=1)
    num_epoch = int(sys.argv[7])
    learning_rate = float(sys.argv[8])
    theta = np.zeros(301, float)
    theta = train(theta, train_X, train_y, num_epoch, learning_rate)
    train_predictions = predict(theta, train_X)
    test_predictions = predict(theta, test_X)
    train_error = compute_error(train_predictions, train_y)
    test_error = compute_error(test_predictions, test_y)
    # print(train_error)
    # print(test_error)

    metric_file = open(sys.argv[6], "w")
    metric_file.write("error(train): {value:.6f}\n".format(value=train_error))
    metric_file.write("error(test): {value:.6f}".format(value=test_error))
    metric_file.close()

    np.savetxt(sys.argv[4], train_predictions.astype(int), delimiter="\n", fmt='%s')
    np.savetxt(sys.argv[5], test_predictions.astype(int), delimiter="\n", fmt='%s')


























