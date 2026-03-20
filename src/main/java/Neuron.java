import java.util.ArrayList;

public class Neuron {
    private String data;


    private ArrayList<Connection> nextLayer;


    private float bias;


    private float inputValue;

    private boolean isInput;

    private float outputProbability;

    private ArrayList<Connection> prevConnections = new ArrayList<>();

    private float accumulation;

    private float activationError;

    public Neuron(String data) {
        this.data = data;
    }

    public Neuron() {
        bias = (float) Math.random() * 0.2f - 0.1f;
    }

    public Neuron(ArrayList<Connection> nodes) {
        this.nextLayer = nodes;
    }
    public void setData(String data) {
        this.data = data;
    }

    public void setConnections(ArrayList<Connection> nodes) {
        this.nextLayer = nodes;
    }


    public void addConnection(Connection connection) {
        nextLayer.add(connection);
    }

    public boolean isOutput() {
        return nextLayer == null;
    }

    public boolean hasData() {
        return data != null;
    }

    public String getData() {
        return this.data;
    }
    public float getAccumulation() {
        return accumulation;
    }

    public void setAccumulation(float accumulation) {
        this.accumulation = accumulation;
    }
    public ArrayList<Connection> getNextLayer() {
        return nextLayer;
    }

    public void setNextLayer(ArrayList<Connection> nextLayer) {
        this.nextLayer = nextLayer;
    }
    public float getBias() {
        return bias;
    }

    public void setBias(float bias) {
        this.bias = bias;
    }

    public float getActivationError() {
        return activationError;
    }

    public void setActivationError(float activationError) {
        this.activationError = activationError;
    }

    public ArrayList<Connection> getPrevConnections() {
        return prevConnections;
    }

    public void setPrevConnections(ArrayList<Connection> connections) {
        this.prevConnections = connections;
    }


    public void addPrevConnection(Connection connection) {
        this.prevConnections.add(connection);
    }


    public void setIsInput(boolean input) {
        this.isInput = input;
    }

    public boolean isInput() {
        return this.isInput;
    }

    public float getInputValue() {
        return inputValue;
    }

    public void setInputValue(float inputValue) {
        this.inputValue = inputValue;
    }

    public void setInput(boolean input) {
        isInput = input;
    }

    public void setOutputProbability(float value) {
        this.outputProbability = value;
    }

    public float getOutputProbability() {
        return outputProbability;
    }
}
