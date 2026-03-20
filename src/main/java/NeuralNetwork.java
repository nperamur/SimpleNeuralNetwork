import java.util.ArrayList;
import java.util.HashMap;

public class NeuralNetwork<T> {
    private HashMap<T, Connection> inputs = new HashMap<>();

    private ArrayList<Neuron> hiddenLayers;

    private int numHiddenLayers;
    private ArrayList<Connection> outputConnections = new ArrayList<>();

    private ArrayList<Neuron> outputLayer;
    private int numHiddenNodes;


    public NeuralNetwork(int numHiddenLayers, int numHiddenNodes, String[] outputs) {
        this.numHiddenLayers = numHiddenLayers;
        this. numHiddenNodes = numHiddenNodes;
        ArrayList<Neuron> layer = new ArrayList<>();
        for (String output: outputs) {
            layer.add(new Neuron(output));
        }
        outputLayer = layer;
        for (int i = 0; i < this.numHiddenLayers; i++) {
            ArrayList<Neuron> currentLayer = new ArrayList<>();
            for (int j = 0; j < numHiddenNodes; j++) {
                Neuron neuron = new Neuron();
                neuron.setConnections(initConnections(layer, neuron));
                currentLayer.add(neuron);
                if (i == 0) outputConnections.addAll(neuron.getNextLayer());
            }
            layer = currentLayer;
        }
        hiddenLayers = layer;
    }

    private ArrayList<Connection> initConnections(ArrayList<Neuron> nodes, Neuron prev) {
        ArrayList<Connection> connections = new ArrayList<>();
        for (Neuron node : nodes) {
            Connection connection = new Connection(node, prev);
            node.addPrevConnection(connection);
            connections.add(connection);
        }
        return connections;
    }

    private void addInput(T data) {
        Neuron node = new Neuron();
        node.setIsInput(true);
        node.setConnections(initConnections(hiddenLayers, node));
        Connection connection = new Connection(node, null);
        inputs.put(data, connection);
    }


    public String predict(T[] keys) {
        return predict(keys, true);
    }

    public String predict(T[] keys, Number[] data) {
        return predict(keys, data, true);
    }

    private String predict(T[] keys, boolean cleanUp) {
        for (int i = 0; i < keys.length; i++) {
            T s = keys[i];
            if (!inputs.containsKey(s)) {
                addInput(s);
            }
            Connection input = inputs.get(s);
            Neuron node = input.getNext();
            node.setInputValue(1);
        }

        return forwardPropagation(cleanUp);
    }


    private String predict(T[] keys, Number[] data, boolean cleanUp) {
        for (int i = 0; i < keys.length; i++) {
            T s = keys[i];
            if (!inputs.containsKey(s)) {
                addInput(s);
            }
            Connection input = inputs.get(s);
            Neuron node = input.getNext();
            if (data[i] instanceof Number n) {
                float value = n.floatValue();
                node.setInputValue(value);
            } else {
                node.setInputValue(1);
            }
            float value = data[i].floatValue();
            node.setInputValue(value);
        }
        return forwardPropagation(cleanUp);
    }

    private String forwardPropagation(boolean cleanUp) {
        float maxOutputWeight = Float.NEGATIVE_INFINITY;
        UniqueQueue<Neuron> queue = new UniqueQueue<>();
        String output = "";
        for (Connection connection : inputs.values()) {
            queue.add(connection.getNext());
        }
        while (!queue.isEmpty()) {
            Neuron currNeuron = queue.pop();

            float value = currNeuron.isInput() ? currNeuron.getInputValue() : sigmoid(currNeuron.getAccumulation() + currNeuron.getBias());
            if (currNeuron.isOutput()) {
                if (value > maxOutputWeight) {
                    maxOutputWeight = value;
                    output = currNeuron.getData();
                }
            } else {
                for (Connection connection : currNeuron.getNextLayer()) {
                    connection.getNext().setAccumulation(
                            connection.getNext().getAccumulation()
                                    + connection.getWeight() * value);
                    queue.add(connection.getNext());
                }
            }
            //cleanup
            if (cleanUp) {
                currNeuron.setAccumulation(0);
                currNeuron.setInputValue(0);
            }
        }
        return output;
    }

    public void train(T[] keys, String expected, float step) {
        predict(keys, false);
        applySoftmax();
        backPropagation(step, expected);
    }
    public void train(T[] keys, Number[] data, String expected, float step) {
        predict(keys, data, false);
        applySoftmax();
        backPropagation(step, expected);
    }

    private void backPropagation(float step, String expected) {
        UniqueQueue<Connection> queue = new UniqueQueue<>();
        addOutputsToQueue(queue);
        updateActivation(queue, expected, step);
        addOutputsToQueue(queue);
        updateWeights(queue, expected, step);
        updateBiases(expected, step);
        cleanUpNetwork();
    }

    private void addOutputsToQueue(UniqueQueue<Connection> queue) {
        for (Connection connection : outputConnections) {
            queue.add(connection);
        }
    }


    private void updateActivation(UniqueQueue<Connection> queue, String expected, float step) {
        while (!queue.isEmpty()) {
            Connection connection = queue.pop();
            Neuron neuron = connection.getNext();

            float error;
            if (neuron.isOutput()) {
                float expectedVal = neuron.getData().equals(expected) ? 1 : 0;
                error = expectedVal - neuron.getOutputProbability();
            } else {
                error = neuron.getActivationError();
            }
            float sigmoidDerivative = sigmoidDerivative(neuron.getAccumulation() + neuron.getBias());
            float activationDerivative = sigmoidDerivative * connection.getWeight();
            if (connection.getPrev() == null) {
                continue;
            }
            float activationError = error * activationDerivative;
            connection.getPrev().setActivationError(connection.getPrev().getActivationError() + activationError);

            for (Connection c : connection.getPrev().getPrevConnections()) {
                queue.add(c);
            }


        }
    }
    private void updateWeights(UniqueQueue<Connection> queue, String expected, float step) {
        while (!queue.isEmpty()) {
            Connection connection = queue.pop();
            Neuron neuron = connection.getNext();
            if (connection.getPrev() == null) {
                continue;
            }
            float error;
            float weightFit;
            float sigmoidDerivative = sigmoidDerivative(neuron.getAccumulation() + neuron.getBias());
            float prevValue = connection.getPrev().isInput() ?
                    connection.getPrev().getInputValue() :
                    sigmoid(connection.getPrev().getAccumulation() + connection.getPrev().getBias());
            float weightDerivative = sigmoidDerivative
                    * prevValue;

            if (neuron.isOutput()) {
                float expectedVal = neuron.getData().equals(expected) ? 1 : 0;
                error = expectedVal - neuron.getOutputProbability();

            } else {
                error = neuron.getActivationError();
            }
            weightFit = connection.getWeight() + error * weightDerivative * step;
            connection.setWeight(weightFit);
            for (Connection c : connection.getPrev().getPrevConnections()) {
                queue.add(c);
            }

        }
    }


    private void updateBiases(String expected, float step) {
        UniqueQueue<Neuron> neurons = new UniqueQueue<>();
        for (Neuron neuron : outputLayer) {
            neurons.add(neuron);
        }
        while (!neurons.isEmpty()) {
            Neuron neuron = neurons.pop();
            float error;
            if (neuron.isOutput()) {
                float expectedVal = neuron.getData().equals(expected) ? 1 : 0;
                error = expectedVal - neuron.getOutputProbability();
                float sigmoidDerivative = sigmoidDerivative(neuron.getAccumulation() + neuron.getBias());
                float biasDerivative = sigmoidDerivative;
                float biasFit = neuron.getBias() + error * biasDerivative * step;
                neuron.setBias(biasFit);
            } else {
                error = neuron.getActivationError();
                float sigmoidDerivative = sigmoidDerivative(neuron.getAccumulation() + neuron.getBias());

                float biasFit = neuron.getBias() + error * step * sigmoidDerivative;
                neuron.setBias(biasFit);
            }
            for (Connection connection : neuron.getPrevConnections()) {
                if (connection.getPrev() != null) {
                    neurons.add(connection.getPrev());
                }
            }

        }
    }

    private void cleanUpNetwork() {
        UniqueQueue<Neuron> queue = new UniqueQueue<>();
        for (Connection connection : inputs.values()) {
            queue.add(connection.getNext());
        }
        while (!queue.isEmpty()) {
            Neuron currNeuron = queue.pop();
            currNeuron.setActivationError(0);
            currNeuron.setAccumulation(0);
            currNeuron.setInputValue(0);
            currNeuron.setOutputProbability(0);
            if (currNeuron.isOutput()) continue;
            for (Connection connection : currNeuron.getNextLayer()) {
                queue.add(connection.getNext());
            }
        }
    }



    //optimize: activation, weight, bias
    //original equation: sigmoid(w * a + b)
    //gradient descent derivatives
    //activation: dsigmoid * w
    //weight: dsigmoid * a
    //bias: dsigmoid

    //to find needed change to put do error diff times gradient descent derivative for each one
    //remember to multiply the change needed by learning rate and add to original number
    //error diff: target - actual
    private float sigmoid(float input) {
        return (float) (1.0f / (1 + Math.exp(-input)));
    }

    private float sigmoidDerivative(float input) {
        return sigmoid(input) * (1 - sigmoid(input));
    }


    private float relu(float input) {
        return Math.max(0.0f, input);
    }

    private float reluDerivative(float input) {
        return input > 0 ? 1.0f : 0.0f;
    }

    private void applySoftmax() {
        float totalExp = 0;
        for (Neuron neuron : outputLayer) {
            totalExp += (float) Math.exp(neuron.getAccumulation() + neuron.getBias());
        }

        for (Neuron neuron : outputLayer) {
            neuron.setOutputProbability((float) Math.exp(neuron.getAccumulation() + neuron.getBias()) / totalExp);
        }

    }










}
