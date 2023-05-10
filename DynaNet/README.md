# Project Name: DynaNet: A Dynamic Neural Network Library for Optical Character Recognition

The goal of this project was to develop a dynamic neural network library called DynaNet, capable of classifying images of handwritten characters, specifically a subset of an Optical Character Recognition (OCR) dataset.

## Key Components

### 1. Module-based Automatic Differentiation (AD)
DynaNet employed module-based AD, a technique widely used in developing libraries for deep learning. This approach allowed for the dynamic specification of the computation graph at runtime, similar to libraries such as Torch, PyTorch, and DyNet.

**Implementation Details:**
- **Dataset**: A subset of an OCR dataset with different data sizes (small, medium, and large) was used, consisting of two CSV files for training and validation. Each row contained 129 columns, with the first column representing the label and the other 128 columns representing the pixel values of a 16x8 image.
- **Model definition**: A single-hidden-layer neural network with a sigmoid activation function for the hidden layer and a softmax function on the output layer was implemented. The model was trained using the average cross-entropy over the training dataset.
- **Initialization**: Two initialization schemes, RANDOM (weights initialized randomly from a uniform distribution between -0.1 and 0.1, biases initialized to zero) and ZERO (all weights initialized to 0), were supported.
- **Training**: Stochastic Gradient Descent (SGD) was used to optimize the parameters for the one-hidden-layer neural network, with the number of epochs, learning rate, and number of hidden units determined by command line flags.
- **Performance evaluation**: The performance of the neural network was assessed using cross-entropy, error rates, and other relevant evaluation metrics.

### 2. Flexible and Modular Design
DynaNet was designed with flexibility and modularity in mind, allowing users to easily modify the network structure, activation functions, and optimization techniques, making it adaptable to various tasks and applications.

This project showcased the ability to create a dynamic and flexible neural network library from scratch, providing a deep understanding of the underlying concepts and methodologies involved in image classification and character recognition tasks. DynaNet served as an excellent foundation for future developments and improvements in the field of deep learning and computer vision.
