import argparse
import numpy as np

def get_inputs():
    """
    Collects all the inputs from the command line and returns the data. To use this function:

        validation_data, words_to_indices, tags_to_indices, hmminit, hmmemit, hmmtrans, predicted_file, metric_file = parse_args()

    Where above the arguments have the following types:

        validation_data --> A list of validation examples, where each element is a list:
            validation_data[i] = [(word1, tag1), (word2, tag2), (word3, tag3), ...]
        
        words_to_indices --> A dictionary mapping words to indices

        tags_to_indices --> A dictionary mapping tags to indices

        hmminit --> A np.ndarray matrix representing the initial probabilities

        hmmemit --> A np.ndarray matrix representing the emission probabilities

        hmmtrans --> A np.ndarray matrix representing the transition probabilities

        predicted_file --> A file path (string) to which you should write your predictions

        metric_file --> A file path (string) to which you should write your metrics
    """

    parser = argparse.ArgumentParser()
    
    parser.add_argument("validation_data", type=str)
    parser.add_argument("index_to_word", type=str)
    parser.add_argument("index_to_tag", type=str)
    parser.add_argument("hmminit", type=str)
    parser.add_argument("hmmemit", type=str)
    parser.add_argument("hmmtrans", type=str)
    parser.add_argument("predicted_file", type=str)
    parser.add_argument("metric_file", type=str)

    args = parser.parse_args()

    validation_data = list()
    with open(args.validation_data, "r") as f:
        examples = f.read().strip().split("\n\n")
        for example in examples:
            xi = [pair.split("\t") for pair in example.split("\n")]
            validation_data.append(xi)
    
    with open(args.index_to_word, "r") as g:
        words_to_indices = {w: i for i, w in enumerate(g.read().strip().split("\n"))}
    
    with open(args.index_to_tag, "r") as h:
        tags_to_indices = {t: i for i, t in enumerate(h.read().strip().split("\n"))}
    
    hmminit = np.loadtxt(args.hmminit, dtype=float, delimiter=" ")
    hmmemit = np.loadtxt(args.hmmemit, dtype=float, delimiter=" ")
    hmmtrans = np.loadtxt(args.hmmtrans, dtype=float, delimiter=" ")

    return validation_data, words_to_indices, tags_to_indices, hmminit, hmmemit, hmmtrans, args.predicted_file, args.metric_file


def logsumtrick(V):
    m = np.max(V, axis=1)
    m = m.reshape(-1, 1)
    return (m + np.log((np.sum(np.exp(V - m), axis=1))).reshape(-1, 1))



def forwardbackward(seq, loginit, logtrans, logemit):
    """

        seq is an input sequence, a list of words (represented as strings)

        loginit is a np.ndarray matrix containing the log of the initial matrix

        logtrans is a np.ndarray matrix containing the log of the transition matrix

        logemit is a np.ndarray matrix containing the log of the emission matrix
    
    """
    L = len(seq)
    M = len(loginit)

    alpha = np.zeros((len(tags_to_indices), len(seq)), dtype=float)
    for i in range(alpha.shape[1]):
        if i == 0:
            alpha[:, 0] = np.log(hmminit) + np.log(hmmemit[:, seq[i]])
        else:
            alpha[:, i] = (np.log(hmmemit[:, seq[i]].reshape(-1, 1)) + logsumtrick(
                alpha[:, i - 1] + np.log(hmmtrans).T)).flatten()

    beta = np.zeros((len(tags_to_indices), len(seq)), dtype=float)
    for i in range(beta.shape[1] - 1, -1, -1):
        if i == beta.shape[1] - 1:
            beta[:, -1] = np.log(np.ones(beta.shape[0]))
        else:
            beta[:, i] = (logsumtrick(np.log(hmmemit[:, seq[i + 1]]) + beta[:, i + 1] + np.log(hmmtrans))).flatten()

    return np.argmax(alpha+beta,axis=0),logsumtrick(alpha[:,len(seq)-1].reshape(1,-1))[0][0]


    pass
    

    
    
if __name__ == "__main__":


    validation_data, words_to_indices, tags_to_indices, hmminit, hmmemit, hmmtrans, predicted_file, metric_file=get_inputs()
    for item in validation_data:
        for i in item:
            i[0] = words_to_indices[i[0]]
            i[1] = tags_to_indices[i[1]]

    log_like = []
    seq_pred = []
    prediction_list=[]
    for idx in range(len(validation_data)):
        seq = []
        for pair in validation_data[idx]:
            seq.append(pair[0])


        predicted_tags,logl=forwardbackward(seq,hmminit,hmmemit,hmmtrans)

        seq_pred.extend(predicted_tags)
        log_like.append(logl)
        prediction_list.append(list(predicted_tags))

    actual_tags = []
    for idx in range(len(validation_data)):
        seq_tags = []
        for pair in validation_data[idx]:
            seq_tags.append(pair[1])
        actual_tags.extend(np.array(seq_tags))

    with open(metric_file, "w") as f:
        f.write("Average Log-Likelihood: {}\n".format(np.mean(log_like)))
        f.write("Accuracy: {}".format(np.sum(np.array(seq_pred) == np.array(actual_tags)) / len(actual_tags)))

    indices_to_tags = dict((value, key) for key, value in tags_to_indices.items())
    indices_to_words = dict((value, key) for key, value in words_to_indices.items())
    actual_words = []
    for idx in range(len(validation_data)):
        seq_words = []
        for pair in validation_data[idx]:
            seq_words.append(indices_to_words[pair[0]])
        actual_words.append(seq_words)
    with open(predicted_file,"w") as f:
        for i, j in zip(actual_words, prediction_list):
            for k, l in zip(i, j):
                f.write(k + "\t" + indices_to_tags[l] + "\n")

            f.write("\n")

    # print(np.mean(log_like))
    # print(np.sum(np.array(seq_pred) == np.array(actual_tags)) / len(actual_tags))







    pass