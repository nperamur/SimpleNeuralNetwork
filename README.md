## SimpleNeuralNetwork

![Java](https://img.shields.io/badge/Java-21+-orange?logo=openjdk)

This is a simple Neural Network I developed from scratch in java. As a test, I trained this Neural Network on historical Real Madrid matches and it is able to form a prediction
on whether Real Madrid will win lose or draw based on many factors such as opposition, home/away, days of rest, and more.

When constructing it, you can specify the number of hidden layers, the number of hidden nodes per layer, and the possible outputs the model can return.
As inputs during training, the model takes in keys, numerical values that correspond to those keys, the expected value, and the learning rate.
During inference, the model takes in the keys and values and tries to predict the most likely output value based on its training data.

### Architecture
This is a graph-based implementation of a multi-layer perceptron. 

A multi-layer perceptron consists of multiple layers. We model these layers through Neurons and Connections. Each Neuron is initialized with a random bias and points to an ArrayList of connections.
Each Connection has a randomly generated weight and pointers to the previous and next nodes. This forms a graph structure which is the foundation for our implementation of this Neural Network.

**Prediction**:
During Forward Propagation, it simply does a BFS graph traversal using a unique queue (so we do not visit the same node multiple times), doing a weighted sum of all the weights and biases of the previous layer.
As for the activation function, this program uses sigmoid. This accumulation is then stored within the Neuron object itself. When we reach the output layer, whichever Neuron that has the highest output value wins 
and the program returns the data contained in that output Neuron.

**Training**:
For training, we begin with our forward propagation except now we apply a softmax function to all the outputs. Then, for backpropagation, we traverse the graph by doing the BFS in reverse.
At each node, we propagate our activation error using gradient descent. After that, we use the activation errors to update our weights and our biases by doing gradient descent as well. 
As a result, with iterations of training, our model learns patterns in the input data and its predictions get more and more refined.

### Testing the Model
To test if the model actually learns, I put it through the MNIST benchmark. This is a benchmark that determines how well the model can classify handwritten numbers. 
When I tested it, I found it scores around 92% on the 1 hidden layer with 16 Neurons per hidden layer configuration. Not bad for a really simple, small model. 
Thus, it can be seen that this model is able to work as intended: It can learn patterns and form accurate predictions based on those patterns.

### The Real Madrid Game Predictor
As a real-world test for my Neural Network I decided to train this model on historical Real Madrid matches in order to form an educated prediction on whether they will win the game, lose, or draw.
When I first tested it, the Neural Network was biased towards always predicting win, something that would optimize its accuracy due to the fact Madrid win a lot but does not satisfy my expectations of 
it utilizing various factors. To make up for this discrepancy, I adjusted my training so that it would be punished more than usual for getting a loss incorrect by training it more on losses and draws
than the natural occurrence in the original dataset. Although its accuracy technically did not improve due to the unpredictable nature of sports, I would consider this a win because I noticed how it developed patterns
like how Real Madrid tends to win in the UCL and at home more frequently than away, and how Madrid is more likely to beat easier opposition than tough teams, and how too little or too much rest can
cause Madrid to play worse.

### Limitations
I developed this Neural Network from scratch in order to really learn how neural networks work. As a result, I used this graph-based CPU approach because it was the best way for me to picture and understand how they work.
However, the limitation is that using this particular method is slower compared to other methods for two reasons. Most production neural networks use matrix math rather than graphs because it is faster to compute and there is 
less object overhead. Additionally, this runs on the CPU, whereas most production models run on the GPU because it is much faster and allows the models to operate at significantly larger scales. 

### Conclusion
Overall, I was really surprised by the results. I never expected the concepts for this Neural Network to be so simple yet, it produces a model that is able to learn all on its own.
Feel free to clone this repository and test this model out for yourself. Try training it on different datasets and try using different configurations and see how it does.


