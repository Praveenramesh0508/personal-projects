import numpy as np
import sys


def majority_vote(train_labels):
    l_1 = train_labels[0]
    count_l_1 = 1
    count_l_2 = 0
    for i in train_labels[1:]:
        if i == l_1:
            count_l_1 = count_l_1 + 1
        else:
            l_2 = i
            count_l_2 = count_l_2 + 1

    if count_l_1 > count_l_2:
        dv = l_1
    elif count_l_1 < count_l_2:
        dv = l_2
    else:
        dv = max(l_1, l_2)

    return dv


def entropy_cal(y):
    frequency_dict = {}
    entropy = 0
    len_y = len(y)
    unique_labels, label_frequency = np.unique(y, return_counts=True)
    frequency_dict = dict(zip(unique_labels, label_frequency))
    for i in frequency_dict.keys():
        entropy += -(frequency_dict[i] / len_y * np.log2(frequency_dict[i] / len_y))
    return entropy


def error(dv):
    train_predicted = np.full(shape=len(train_labels), fill_value=dv)
    train_error = round(sum(train_labels != train_predicted) / len(train_labels), 6)
    return train_error


with open(sys.argv[1]) as heart_train:
    train_data = np.genfromtxt(heart_train, delimiter='\t', skip_header=1)

train_labels = train_data[:, -1]
dv = majority_vote(train_labels)
error_value = error(dv)
entropy_value = entropy_cal(train_labels)

metric_file = open(sys.argv[2], "w")
metric_file.write("entropy: {value:.6f}\n".format(value=entropy_value))
metric_file.write("error: {value:.6f}".format(value=error_value))
metric_file.close()







