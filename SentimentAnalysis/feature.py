import numpy as np
import sys

def word_2_vec(data,dict_word_vec):
    final_vector = np.empty((0, 301), float)
    for i in data:
        count_words=0
        sum_vector= np.zeros(300)
        filtered =[word for word in i[1].split()  if word in dict_word_vec]
        for j in filtered:
            sum_vector=np.add(sum_vector,np.array(dict_word_vec[j]))
            count_words+=1
        sum_vector=(1/count_words)*sum_vector
        sum_vector = np.append(i[0],sum_vector)
        final_vector =np.vstack([final_vector,sum_vector])


    return final_vector

if __name__ == '__main__':

        file_name_train = sys.argv[1]
        file_name_test = sys.argv[3]
        file_name_val = sys.argv[2]
        file_name_word_vec = sys.argv[4]

        file_train = open(file_name_train)
        file_val = open(file_name_val)
        file_test = open(file_name_test)

        file_words = open(file_name_word_vec, "r")

        train_data = np.genfromtxt(file_train, delimiter='\t', dtype='str')
        val_data = np.genfromtxt(file_val, delimiter='\t', dtype='str')
        test_data = np.genfromtxt(file_test, delimiter='\t', dtype='str')

        list_of_lists = [(line.strip()).split() for line in file_words]
        dict_word_vec = {}
        for i in list_of_lists:
            dict_word_vec[i[0]] = list(map(float, i[1:]))

        train_formatted = word_2_vec(train_data, dict_word_vec)
        val_formatted = word_2_vec(val_data, dict_word_vec)
        test_formatted = word_2_vec(test_data, dict_word_vec)

        np.savetxt(sys.argv[5], train_formatted.astype(float), delimiter="\t", fmt='%1.6f')
        np.savetxt(sys.argv[7], test_formatted.astype(float), delimiter="\t", fmt='%1.6f')
        np.savetxt(sys.argv[6], val_formatted.astype(float), delimiter="\t", fmt='%1.6f')






