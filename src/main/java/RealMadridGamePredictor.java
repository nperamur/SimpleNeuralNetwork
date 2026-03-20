import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class RealMadridGamePredictor {
    public static void main(String[] args) {
        NeuralNetwork<String> neuralNetwork = new NeuralNetwork<>(1, 64, new String[] {"Win", "Draw", "Loss"});
        String[] keys = new String[] {"Match Gap", "Month", "Match Type", "Home Away", "Opposing Team", "Opposing Team Ranking", "Prev Score Goal", "Prev Concede Goal"};
        System.out.println("Welcome to the Real Madrid Game Predictor\n----------------------------------");
        HashMap<String, Integer> teams = new HashMap<>();
        HashMap<Integer, String> reverseMap = new HashMap<>();
        ArrayList<Double[]> valueArray = new ArrayList<>();
        ArrayList<Integer[]> printingValuesArray = new ArrayList<>();
        ArrayList<String> answerArray = new ArrayList<>();
        int teamCounter = 0;
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(RealMadridGamePredictor.class.getResourceAsStream("team-names.txt")))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                teams.put(clean(line), teamCounter);
                reverseMap.put(teamCounter, clean(line));
                teamCounter++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (CSVReader csvReader = new CSVReader(
                new InputStreamReader(RealMadridGamePredictor.class.getResourceAsStream("real-madrid-data.csv")))) {
            String[] data;
            for (int i = 0; i < 3; i++) {
                csvReader.readNext();
            }
            while ((data = csvReader.readNext()) != null) {
//                if (!teams.containsKey(data[7])) {
//                    teamCounter++;
//                    teams.put(data[7], teamCounter);
//                    reverseMap.put(teamCounter, data[7]);
//                }
                try {
                    Integer[] printingValues = new Integer[]
                            {(int) Double.parseDouble(data[0]), (int) Double.parseDouble(data[1]),
                                    (int) Double.parseDouble(data[4]), (int) Double.parseDouble(data[6]), (Integer) teams.get(data[7]),
                                    (int) Double.parseDouble(data[9]), (int) Double.parseDouble(data[18]), (int) Double.parseDouble(data[19])};
                    Double[] values = new Double[]
                            {Math.min(Double.parseDouble(data[0]) / 7.0, 1) + 0.001f, Double.parseDouble(data[1]) / 12.0 + 0.001f,
                                    Double.parseDouble(data[4]) / 2.0 + 0.001f, Double.parseDouble(data[6]) + 0.001f, (double) (teams.get(clean(data[7])) / 120.0) + 0.001f,
                                    Double.parseDouble(data[9]) / 21.0, Math.min(Double.parseDouble(data[18]), 4) / 4.0 + 0.001f, Math.min(Double.parseDouble(data[19]), 3) / 3.0 + 0.001f};
                    String gameStatus = getGameStatus(Integer.parseInt(data[15]), Integer.parseInt(data[16]));
                    valueArray.add(values);
                    answerArray.add(gameStatus);
                    printingValuesArray.add(printingValues);

                } catch (NumberFormatException e) {}
                //System.out.println(neuralNetwork.predict(keys, new Integer[]{5, 9, 1, 1, 0, 12, 0, 2}));

            }
            System.out.println("Training Model...");
            for (int i = 0; i < 32000; i++) {
//                int index = (int) (Math.random() * valueArray.size() * 0.8f);
                int index = (int) (Math.random() * valueArray.size());
                Double[] values = valueArray.get(index);
                int repeat = 5;
                if (answerArray.get(index).equals("Win") && printingValuesArray.get(index)[3] == 1 && printingValuesArray.get(index)[2] != 0) repeat = 6;
                if (answerArray.get(index).equals("Draw") && printingValuesArray.get(index)[3] == 0) repeat = 7;
                if (answerArray.get(index).equals("Loss") && printingValuesArray.get(index)[3] == 0) repeat = 10;
                if (answerArray.get(index).equals("Loss") && printingValuesArray.get(index)[3] == 1 && printingValuesArray.get(index)[2] == 0) repeat = 8;
                for (int j = 0; j < repeat; j++) {
                    neuralNetwork.train(keys, values, answerArray.get(index), 0.1f);
                }

                //System.out.println("Post-Training Prediction:" + neuralNetwork.predict(keys, values));
            }

//            System.out.println("Evaluation:");
//            float sum = 0;
//            float n = 0;
//            for (int i = (int) (valueArray.size() * 0.8f); i < valueArray.size(); i++) {
//                Double[] values = valueArray.get(i);
//                Integer[] printingValues = printingValuesArray.get(i);
//                System.out.println(Arrays.toString(printingValues));
//                String prediction = neuralNetwork.predict(keys, values);
//                System.out.println(reverseMap.get(printingValues[4]));
//                System.out.println("Prediction:" + prediction);
//
//                System.out.println("EXPECTED:" + answerArray.get(i));
//                if (prediction.equals(answerArray.get(i))) n++;
//                sum++;
//            }
//
//            System.out.println("Score: " + (n / sum) * 100 + "%");

            System.out.println("Pick a team to play against");
            Thread.sleep(1000);
            HashMap<Integer, String> teamNumbers = new HashMap<>();
            int i = 1;
            for (String team : teams.keySet()) {
                System.out.println(i + ". " + team);
                teamNumbers.put(i, team);
                i++;
            }
            boolean validInput = false;
            double team = 0;
            while (!validInput) {
                System.out.print("Choose a team to play against (1-93): ");
                Scanner scanner = new Scanner(System.in);
                int num;
                try {
                    num = Integer.parseInt(scanner.nextLine());
                    if (num >= 1 && num <= 93) {
                        validInput = true;
                        team = (double) (teams.get(teamNumbers.get(num)) / 120.0) + 0.001f;
                    } else {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Input. Try again.");
                }
            }

            System.out.println("\nMatch Types:\n0 - Champions League, 1 - La Liga, 2 - Spanish Cup");
            double matchType = 0;
            validInput = false;
            while (!validInput) {
                System.out.print("Choose a match type (0, 1, or 2): ");
                Scanner scanner = new Scanner(System.in);
                int num;
                try {
                    num = Integer.parseInt(scanner.nextLine());
                    if (num >= 0 && num <= 2) {
                        validInput = true;
                        matchType = ((double) num) / 2 + 0.001f;
                    } else {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Input. Try again.");
                }
            }


            double homeAway = 0;
            validInput = false;
            while (!validInput) {
                System.out.print("Is this match home or away (0 - Home, 1 - Away)? ");
                Scanner scanner = new Scanner(System.in);
                int num;
                try {
                    num = Integer.parseInt(scanner.nextLine());
                    if (num >= 0 && num <= 1) {
                        validInput = true;
                        homeAway = ((double) num) + 0.001f;
                    } else {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Input. Try again.");
                }
            }

            double matchGap = 0;
            validInput = false;
            while (!validInput) {
                System.out.print("How many days of rest does Madrid get before this game? ");
                Scanner scanner = new Scanner(System.in);
                int num;
                try {
                    num = Integer.parseInt(scanner.nextLine());
                    if (num >= 0 && num <= 365) {
                        validInput = true;
                        matchGap = Math.min(((double) num), 7) / 7f + 0.001f;
                    } else {
                        if (num > 365) {
                            System.out.println("There is no way Madrid hasn't played a game for that long!");
                        }
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Input. Try again.");
                }
            }

            double prevGoals = 0;
            validInput = false;
            while (!validInput) {
                System.out.print("How many goals did Real Madrid score in their last match? ");
                Scanner scanner = new Scanner(System.in);
                int num;
                try {
                    num = Integer.parseInt(scanner.nextLine());
                    if (num >= 0 && num <= 15) {
                        validInput = true;
                        prevGoals = ((double) Math.min(num, 4)) / 4f + 0.001f;
                    } else {
                        if (num > 15) {
                            System.out.println("There is no way Madrid scored that many goals in one game! ");
                        }
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Input. Try again.");
                }
            }


            double prevConceded = 0;
            validInput = false;
            while (!validInput) {
                System.out.print("How many goals did Madrid concede in their last game?" );
                Scanner scanner = new Scanner(System.in);
                int num;
                try {
                    num = Integer.parseInt(scanner.nextLine());
                    if (num >= 0 && num <= 15) {
                        validInput = true;
                        prevConceded = Math.min((double) num, 3) / 3f + 0.001f;
                    } else {
                        if (num > 15) {
                            System.out.println("There is no way Madrid conceded that many goals in one game!");
                        }
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Input. Try again.");
                }
            }
            String prediction = neuralNetwork.predict(new String[] {"Opposing Team", "Match Type", "Home Away",
                    "Match Gap", "Prev Score Goal", "Prev Concede Goal"}, new Double[] {team, matchType, homeAway, matchGap, prevGoals, prevConceded});
            System.out.println("Prediction: " + prediction);
        } catch (IOException | CsvValidationException | InterruptedException e) {
            throw new RuntimeException(e);
        }



    }

    public static String getGameStatus(int scored, int against) {
        if (scored > against) {
            return "Win";
        } else if (scored == against) {
            return "Draw";
        } else {
            return "Loss";
        }
    }
    public static String clean(String input) {
        if (input == null) return "";

        String text = input.replace("\uFEFF", "").replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");

        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        String result = normalized.replaceAll("\\p{M}", "");

        return result.replaceAll("[^a-zA-Z0-9 ]", " ").replaceAll("\\s+", " ").trim();
    }
}

