package maze;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class Main {

    final static Scanner sc = new Scanner(System.in);
    static List<List<Integer>> maze;
    static Map<Integer, Runnable> menuPoints = new LinkedHashMap<>();

    static {
        menuPoints.put(1, Main::createMaze);
        menuPoints.put(2, Main::loadMaze);
        menuPoints.put(0, Main::loadMaze);
    }

    public static void main(String[] args) {
        printMenuAndRun();
    }

    static void printMenuAndRun() {
        int choice;
        do {

            System.out.println("=== Menu ===\n1. Generate a new maze.\n2. Load a maze.");
            int maxChoice = 2;

            if (maze != null) {
                System.out.println("3. Save the maze.\n4. Display the maze.\n5. Find the escape");
                maxChoice = 5;
            }

            System.out.println("0. Exit.");

            choice = sc.nextInt();
            sc.nextLine();
            switch (choice) {
                case 1:
                    createMaze();
                    break;
                case 2:
                    loadMaze();
                    break;
                case 0:
                    System.out.println("Buy!");
                    break;
                default:
                    if (maxChoice == 5 && choice > 2 && choice <= 5) {
                        switch (choice) {
                            case 3:
                                try {
                                    saveMaze();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case 4:
                                printMaze();
                                break;
                            case 5:
                                int x = 0;
                                for (int i = 1; i < maze.size() - 1; i++) {
                                    if (maze.get(i).get(0) == 0) {
                                        x = i;
                                        break;
                                    }
                                }
                                findTheWay("", String.format("%d 0", x));
                                printMaze();
                                break;
                        }
                    } else {
                        System.out.println("Incorrect option. Please try again\n");
                    }
            }
        } while (choice != 0);
    }

    static void printMaze() {
        for (List<Integer> i : maze) {
            for (int j : i) {
                if (j == 1) System.out.print("\u2588\u2588");
                else if (j == 2) System.out.print("//");
                else System.out.print("  ");
            }
            System.out.println();
        }
    }

    static void createMaze() {
        System.out.println("Please, enter the size of a maze");
        int x = sc.nextInt();

        if (x > 0) {
            maze = new ArrayList<>();
            for (int i = 0; i < x; i++) {
                List<Integer> temp = new ArrayList<>();
                for (int j = 0; j < x; j++) {
                    temp.add(1);
                }
                maze.add(temp);
            }

            dig("", String.format("%d 0", (new Random(System.nanoTime())).nextInt(x - 2) + 1));

            while (true) {
                int ex = (new Random(System.nanoTime())).nextInt(x - 2) + 1;
                if ((maze.get(ex)).get(x - 2) == 0) {
                    (maze.get(ex)).set(x - 1, 0);
                    break;
                }

            }

            printMaze();

        } else System.out.println("Incorrect maze size\n");

    }

    static void saveMaze() throws IOException {
        String filename = sc.next();
        File file = new File(filename);


        if (!file.exists()) file.createNewFile();
        PrintStream original = new PrintStream(System.out);
        System.setOut(new PrintStream(filename));

        for (List<Integer> i : maze) {
            for (Integer j : i) {
                System.out.printf("%d ", j);
            }
            System.out.println();
        }
        System.setOut(original);
    }

    static void loadMaze() {
        String filename = sc.nextLine();
        Scanner sc;
        try {
            File file = new File(filename);
            if (file.exists()) sc = new Scanner(file);
            else throw new FileNotFoundException();

            boolean incorrectFormat = true;
            List<List<Integer>> lines = new ArrayList<>();
            int baseLength = -1;
            while (sc.hasNextLine() && incorrectFormat) {
                String line = sc.nextLine();
                if (line.matches("([0-1]\\s)+")) {
                    if (baseLength == -1) baseLength = line.length();
                    if (line.length() == baseLength) {
                        List<Integer> ints = new ArrayList<>();
                        for (String s : line.trim().split("\\s")) {
                            ints.add(Integer.parseInt(s));
                        }
                        lines.add(ints);
                    } else {
                        incorrectFormat = false;
                    }
                } else {
                    incorrectFormat = false;
                }
            }

            if (incorrectFormat) {
                maze = lines;
            } else System.out.println("Cannot load the maze. It has an invalid format");

        } catch (FileNotFoundException e) {
            System.out.println("The file " + filename + " does not exist");
        }
    }

    static void dig(String prevPos, String currentPos) {
        boolean result = true;
        int currentX = Integer.parseInt(currentPos.split(" ")[0]);
        int currentY = Integer.parseInt(currentPos.split(" ")[1]);

        int prevX = 0, prevY = 0;

        if (!prevPos.equals("")) {
            prevX = Integer.parseInt(prevPos.split(" ")[0]);
            prevY = Integer.parseInt(prevPos.split(" ")[1]);
        }

        List<String> directions = new ArrayList<>();

        for (int i = -1; i <= 1 && result; i++) {
            for (int j = -1; j <= 1 && result; j++) {
                if (currentX + i > 0 && currentX + i < maze.size() - 1 && currentY + j > 0 && currentY + j < maze.get(0).size() - 1) {
                    String s = String.format("%d %d", currentX + i, currentY + j);
                    if (maze.get(currentX + i).get(currentY + j) == 0) {
                        if (!(currentX == prevX && (currentY + j == prevY) || currentY == prevY && (currentX + i == prevX)))
                            result = false;
                    } else if ((i == 0 && j != 0) || (i != 0 && j == 0)) directions.add(s);
                }
            }
        }
        if (result) {
            maze.get(currentX).set(currentY, 0);
            while (directions.size() > 0) {
                int direction = (new Random(System.nanoTime())).nextInt(directions.size());
                dig(currentPos, directions.get(direction));
                directions.remove(direction);
            }
        }
    }

    static boolean findTheWay(String prevPos, String currentPos) {

        int currentX = Integer.parseInt(currentPos.split(" ")[0]);
        int currentY = Integer.parseInt(currentPos.split(" ")[1]);

        boolean result = true;

        List<String> directions = new ArrayList<>();

        for (int i = -1; i <= 1 && result; i++) {
            for (int j = -1; j <= 1 && result; j++) {
                String s = String.format("%d %d", currentX + i, currentY + j);
                if (currentX + i >= 0 && currentY + j >= 0 && currentX + i < maze.size() && currentY + j < maze.size())
                    if (currentX < maze.size() - 1 && maze.get(currentX + i).get(currentY + j) == 0 && !s.equals(prevPos) && ((i == 0 && j != 0) || (j == 0 && i != 0)))
                        if (currentY + j == maze.size() - 1) {
                            result = false;
                            maze.get(currentX + i).set(currentY + j, 2);
                        } else directions.add(s);
            }
        }

        while (directions.size() > 0 && result) {
            int direction = (new Random(System.nanoTime())).nextInt(directions.size());
            result = findTheWay(currentPos, directions.get(direction));
            directions.remove(direction);
        }

        if (!result) maze.get(currentX).set(currentY, 2);

        return result;
    }
}
