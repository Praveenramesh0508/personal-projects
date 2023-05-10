import numpy as np
import sys


class Node:
    def __init__(self):
        self.left = None
        self.right = None
        self.attr = None
        self.attr_i = None
        self.vote = None
        self.depth = None
        self.cols = None
        self._1_1_count = None
        self._1_0_count = None
        self._0_1_count = None
        self._0_0_count = None


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


def mutual_information(x):
    x_frequency_dict = {}
    I_inner_function = 0
    entropy_all_labels = entropy_cal(x[:, -1])
    unique_labels, label_frequency = np.unique(x[:, 0], return_counts=True)
    x_frequency_dict = dict(zip(unique_labels, label_frequency))
    for i in x_frequency_dict.keys():
        I_inner_function += (x_frequency_dict[i] / len(x[:, 0])) * (entropy_cal(x[x[:, 0] == i, -1]))
    I = entropy_all_labels - I_inner_function
    return I


def error(predicted_array, labels):
    error_rate = round(sum(predicted_array != labels) / len(labels), 6)
    return error_rate


def best_cols(D, cols):
    dict_MT = {}
    for i in cols:
        dict_MT[i] = mutual_information(D[:, [all_cols.index(i), -1]])
    best_MI = max(dict_MT.values())
    best_cols = [key for key, value in dict_MT.items() if value == max(dict_MT.values())]
    best_index = []
    for i in best_cols:
        best_index.append(all_cols.index(i))
    best_col_index = min(best_index)
    return all_cols[best_col_index], best_col_index, best_MI


def train(D, cols, curr_depth):
    def tree_recurse(D, cols, curr_depth):

        if len(cols) == 0 or D.size == 0 or best_cols(D, cols)[2] == 0 or curr_depth >= max_depth or entropy_cal(
                D[:, -1]) == 0:
            root = Node()
            root.left = None
            root.right = None
            root.vote = majority_vote(D[:, -1])

        else:

            best_x, best_x_i, best_MI = best_cols(D, cols)
            root = Node()
            root.attr = best_x
            root.attr_i = best_x_i
            root.depth = curr_depth + 1
            root.cols = [c for c in cols if c != best_x]
            root._0_1_count = sum(D[D[:, best_x_i] == 0, -1])
            root._0_0_count = len(D[D[:, best_x_i] == 0, -1]) - root._0_1_count

            root._1_1_count = sum(D[D[:, best_x_i] == 1, -1])
            root._1_0_count = len(D[D[:, best_x_i] == 1]) - root._1_1_count
            if (D[D[:, best_x_i] == 0].size != 0):
                root.left = tree_recurse(D[D[:, best_x_i] == 0], root.cols, root.depth)

            if (D[D[:, best_x_i] == 1].size != 0):
                root.right = tree_recurse(D[D[:, best_x_i] == 1], root.cols, root.depth)
        return root

    root = tree_recurse(D, cols, curr_depth)
    return root


def predict(root, x):
    if root.vote is not None:
        return root.vote
    else:
        if (x[root.attr_i] == 0):
            label = predict(root.left, x)
        if (x[root.attr_i] == 1):
            label = predict(root.right, x)
    return label




if __name__ == '__main__':

    file_name_train = sys.argv[1]
    file_name_test = sys.argv[2]
    file_train = open(file_name_train)
    train_data = np.genfromtxt(file_train, delimiter='\t', skip_header=1)
    file_train.close()

    file_train = open(file_name_train)
    first_line = file_train.readline()
    cols = first_line.strip().split("\t")
    cols = cols[:-1]
    file_train.close()

    D = train_data.copy()
    all_cols = cols.copy()
    max_depth = int(sys.argv[3])
    curr_depth = 0

    root = train(D, cols, curr_depth)

    with open(file_name_test) as file_test:
        test_data = np.genfromtxt(file_test, delimiter='\t', skip_header=1)

    train_x = train_data[:, :-1]
    train_labels = train_data[:, -1]
    test_x = test_data[:, :-1]
    test_labels = test_data[:, -1]

    train_predicted = []
    for i in range(len(train_x)):
        train_predicted.append(predict(root, train_x[i]))

    train_predicted = np.array(train_predicted)
    train_error=error(train_predicted, train_labels)

    test_predicted = []
    for i in range(len(test_x)):
        test_predicted.append(predict(root, test_x[i]))

    test_predicted = np.array(test_predicted)
    test_error=error(test_predicted, test_labels)

    print(train_error)
    print(test_error)

    # metric_file = open(sys.argv[6], "w")
    # metric_file.write("error(train): {value:.6f}\n".format(value=train_error))
    # metric_file.write("error(test): {value:.6f}".format(value=test_error))
    # metric_file.close()
    #
    # np.savetxt(sys.argv[4], train_predicted.astype(int), delimiter="\n", fmt='%s')
    # np.savetxt(sys.argv[5], test_predicted.astype(int), delimiter="\n", fmt='%s')


    def print_tree(root):
        if root.left is not None:
            # First print the data of node
            print("| " * (root.depth), root.attr, "= 0 :", "[", int(root._0_0_count), "0 /", int(root._0_1_count), "1 ]")
            print_tree(root.left)
            print("| " * (root.depth), root.attr, "= 1 :", "[", int(root._1_0_count), "0 /", int(root._1_1_count), "1 ]")
        if root.right is not None:
            # Finally recurse on right child
            print_tree(root.right)


    # print("[", int(len(train_labels) - sum(train_labels)), "0 /", int(sum(train_labels)), "1]")
    # print_tree(root)









