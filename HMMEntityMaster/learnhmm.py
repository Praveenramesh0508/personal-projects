import argparse
import numpy as np


def get_inputs():
    """
    Collects all the inputs from the command line and returns the data. To use this function:

        train_data, words_to_index, tags_to_index, init_out, emit_out, trans_out = get_inputs()
    
    Where above the arguments have the following types:

        train_data --> A list of training examples, where each training example is a list
            of tuples train_data[i] = [(word1, tag1), (word2, tag2), (word3, tag3), ...]
        
        words_to_indices --> A dictionary mapping words to indices

        tags_to_indices --> A dictionary mapping tags to indices

        init_out --> A file path to which you should write your initial probabilities

        emit_out --> A file path to which you should write your emission probabilities

        trans_out --> A file path to which you should write your transition probabilities
    
    """
    parser = argparse.ArgumentParser()
    parser.add_argument("train_input", type=str)
    parser.add_argument("index_to_word", type=str)
    parser.add_argument("index_to_tag", type=str)
    parser.add_argument("hmmprior", type=str)
    parser.add_argument("hmmemit", type=str)
    parser.add_argument("hmmtrans", type=str)

    args = parser.parse_args()

    train_data = list()
    with open(args.train_input, "r") as f:
        examples = f.read().strip().split("\n\n")
        for example in examples:
            xi = [pair.split("\t") for pair in example.split("\n")]
            train_data.append(xi)
    
    with open(args.index_to_word, "r") as g:
        words_to_indices = {w: i for i, w in enumerate(g.read().strip().split("\n"))}
    
    with open(args.index_to_tag, "r") as h:
        tags_to_indices = {t: i for i, t in enumerate(h.read().strip().split("\n"))}
    
    return train_data, words_to_indices, tags_to_indices, args.hmmprior, args.hmmemit, args.hmmtrans


if __name__ == "__main__":
    # Collect the input data

    # Initialize the initial, emission, and transition matrices

    # Increment the matrices

    # Add a pseudocount

    # Save your matrices to the output files --- the reference solution uses 
    # np.savetxt (specify delimiter="\t" for the matrices)

    train_data, words_to_indices, tags_to_indices, hmmprior_file, hmmemit_file, hmmtrans_file=get_inputs()

    for item in train_data:
        for i in item:
            i[0] = words_to_indices[i[0]]
            i[1] = tags_to_indices[i[1]]

    pi = np.ones((len(tags_to_indices)), dtype=float)
    A = np.ones((len(tags_to_indices), len(words_to_indices)), dtype=float)
    B = np.ones((len(tags_to_indices), len(tags_to_indices)), dtype=float)

    #     pi Matrix

    for key, value in tags_to_indices.items():
        for item in train_data:
            if item[0][1] == value:
                pi[value] += 1

    pi = np.array([i / sum(pi) for i in pi])

    #     A Matrix

    for item in train_data:
        for i in item:
            A[i[1], i[0]] += 1

    for idx, row in enumerate(A):
        A[idx] = row / np.sum(row)

    #     B Matrix
    for item in train_data:
        for idx, i in enumerate(item):
            if idx != 0:
                B[item[idx - 1][1], i[1]] += 1

    for idx, row in enumerate(B):
        B[idx] = row / np.sum(row)

    np.savetxt(hmmprior_file, pi)
    np.savetxt(hmmemit_file, A)
    np.savetxt(hmmtrans_file, B)

