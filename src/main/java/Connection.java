public class Connection {
    private Neuron next;
    private float weight;

    private Neuron prev;
    public Connection(Neuron next, Neuron prev, float weight) {
        this.next = next;
        this.weight = weight;
        this.prev = prev;
    }

    public Connection(Neuron next, Neuron prev) {
        this.next = next;
        this.weight = (float) (Math.random() * 0.6f - 0.3f);
        this.prev = prev;
    }

    public float getWeight() {
        return weight;
    }

    public Neuron getNext() {
        return next;
    }

    public void setNext(Neuron next) {
        this.next = next;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public Neuron getPrev() {
        return prev;
    }

    public void setPrev(Neuron prev) {
        this.prev = prev;
    }
}
