import java.io.*;

public class MnistTest {
    public static void main(String[] args) throws IOException {
        InputStream imgStream = MnistTest.class.getClassLoader().getResourceAsStream("train-images.idx3-ubyte");
        InputStream lblStream = MnistTest.class.getClassLoader().getResourceAsStream("train-labels.idx1-ubyte");
        InputStream predictImgStream = MnistTest.class.getClassLoader().getResourceAsStream("t10k-images.idx3-ubyte");
        InputStream predictLblStream = MnistTest.class.getClassLoader().getResourceAsStream("t10k-labels.idx1-ubyte");

        if (imgStream == null || lblStream == null || predictImgStream == null || predictLblStream == null) {
            throw new RuntimeException("Could not find MNIST files in resources.");
        }

        System.out.println("Streams initialized. Parsing training data...");

        DataInputStream imgIn = new DataInputStream(new BufferedInputStream(imgStream));
        DataInputStream lblIn = new DataInputStream(new BufferedInputStream(lblStream));

        imgIn.readInt();
        int numImgs = imgIn.readInt();
        imgIn.readInt();
        imgIn.readInt();
        lblIn.readInt();
        lblIn.readInt();

        System.out.println("Training Header: " + numImgs + " images detected.");

        String[] numbers = new String[10];
        for (int i = 0; i < 10; i++) {
            numbers[i] = String.valueOf(i);
        }

        NeuralNetwork<Double> neuralNetwork = new NeuralNetwork<>(1, 16, numbers);
        System.out.println("Neural Network initialized (Input -> 16 Hidden -> 10 Output).");

        Double[] pixelKeys = new Double[784];
        for (int k = 0; k < 784; k++) {
            pixelKeys[k] = (double) k;
        }

        long trainStartTime = System.currentTimeMillis();
        for (int i = 0; i < numImgs; i++) {
            int label = lblIn.readUnsignedByte();
            Double[] pixelValues = new Double[784];
            for (int p = 0; p < 784; p++) {
                pixelValues[p] = imgIn.readUnsignedByte() / 255.0;
            }

            neuralNetwork.train(pixelKeys, pixelValues, String.valueOf(label), 0.1f);

            if (i % 100 == 0) {
                System.out.println("Training Status: Image " + i + "/" + numImgs + " processed.");
            }
        }
        System.out.println("Training Phase Complete. Elapsed Time: " + (System.currentTimeMillis() - trainStartTime) + "ms");

        System.out.println("Transitioning to Testing Phase...");
        DataInputStream testImgIn = new DataInputStream(new BufferedInputStream(predictImgStream));
        DataInputStream testLblIn = new DataInputStream(new BufferedInputStream(predictLblStream));

        testImgIn.readInt();
        int numTestImgs = testImgIn.readInt();
        testImgIn.readInt();
        testImgIn.readInt();
        testLblIn.readInt();
        testLblIn.readInt();

        int correct = 0;
        for (int i = 0; i < numTestImgs; i++) {
            int expectedLabel = testLblIn.readUnsignedByte();
            Double[] testPixels = new Double[784];
            for (int p = 0; p < 784; p++) {
                testPixels[p] = testImgIn.readUnsignedByte() / 255.0;
            }

            String prediction = neuralNetwork.predict(pixelKeys, testPixels);
            boolean passes = prediction.equals(String.valueOf(expectedLabel));

            if (passes) correct++;

            if (i % 500 == 0 || i < 10) {
                System.out.println("Test Case " + i + " | Expected: " + expectedLabel + " | Predicted: " + prediction + " | Result: " + (passes ? "PASS" : "FAIL"));
            }
        }

        double finalPercent = (correct / (double) numTestImgs) * 100;
        System.out.println("\n========================================");
        System.out.println("PERFORMANCE SUMMARY");
        System.out.println("Total Accuracy: " + finalPercent + "%");
        System.out.println("Correct Predictions: " + correct + " / " + numTestImgs);
        System.out.println("========================================\n");

        imgIn.close();
        lblIn.close();
        testImgIn.close();
        testLblIn.close();
    }
}