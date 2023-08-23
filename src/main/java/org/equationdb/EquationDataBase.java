package org.equationdb;

import org.equationdb.database.DBConnector;
import org.equationdb.equation.Equation;
import org.equationdb.equation.EquationEvaluator;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class EquationDataBase {

    private DBConnector dbConnector;

    public EquationDataBase() throws IOException, SQLException {
        dbConnector = new DBConnector();
    }

    public void start(String... args) {

        System.out.println("\n Equation Data Base v1.0");

        EquationEvaluator equationEvaluator = new EquationEvaluator();

        try (Scanner scanner = new Scanner(System.in)) {

            boolean continueProgram = true;
            while (continueProgram) {
                System.out.println("\n To enter equation, type: 'enter'." +
                        "\n To enter database credentials, type: 'credentials'." +
                        "\n To poll for equations from database, type: 'poll'." +
                        "\n To exit the program, type: 'exit'.");
                System.out.print(" Input: ");

                String command = scanner.nextLine();
                switch (command.toLowerCase()) {
                    case "exit": {
                        continueProgram = false;
                        break;
                    }
                    case "enter": {
                        enter(equationEvaluator, scanner);
                        break;
                    }
                    case "poll": {
                        poll(scanner);
                        break;
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void enter(EquationEvaluator equationEvaluator, Scanner scanner) throws Exception {

        try {
            System.out.println("\n Enter equation or 'exit' to get to previous menu.");
            System.out.print(" Input: ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit"))
                return;

            Equation equation = new Equation(input);
            equationEvaluator.checkCorrectness(input);

            System.out.println(" Do you wish to enter possible solutions? (y/n)");
            System.out.print(" Input: ");
            input = scanner.nextLine();

            if (yesNoChecker(input)) {

                List<Double> solutions = enterSolutions(scanner);

                double precision = 1.0e-9;
                List<Double> correctSolutions = solutions.stream().filter(solution -> equationEvaluator
                        .evaluateEquation(equation.getEquation(), Double.toString(solution), precision))
                        .collect(Collectors.toList());

                equation.addSolutions(correctSolutions);

                System.out.println(" These correct solutions will be added to database: " + correctSolutions);
            }

            dbConnector.saveEquation(equation);

            System.out.println(" Equation saved to database.");
            System.out.print(" Press Enter key to continue... ");
            scanner.nextLine();

        } catch (IllegalArgumentException e) {
            System.out.println(" Error: " + e.getMessage());;
        }
    }

    private boolean yesNoChecker(String input) {
        return input.equalsIgnoreCase("y")
                || input.equalsIgnoreCase("ye")
                || input.equalsIgnoreCase("yes")
                || input.equalsIgnoreCase("yea")
                || input.equalsIgnoreCase("yeah");
    }

    private List<Double> enterSolutions(Scanner scanner) {
        String input = null;
        List<Double> solutions = new ArrayList<>();

        System.out.println(" Enter solutions as numbers, or enter 'stop' to finalize your input.");

        while (true) {
            System.out.print(" Input: ");

            try {
                input = scanner.nextLine();

                if (input.equalsIgnoreCase("stop"))
                    break;

                Double solution = Double.parseDouble(input);
                solutions.add(solution);
            } catch (NumberFormatException e) {
                System.out.println(" Error: " + e.getMessage());
            }
        }

        return solutions;
    }

    private void poll(Scanner scanner) throws Exception {

        System.out.print("\n To get all equations, type: 'all'." +
                "\n To get equations based on solutions, type: 'solutions'." +
                "\n To get equations based on number of solutions, type: 'number'." +
                "\n To exit menu, type: 'exit'.");

        System.out.print("\n Input: ");
        String input = scanner.nextLine();
        switch (input) {
            case "all": {
                all(scanner);
                break;
            }
            case "solutions": {
                solutions(scanner);
                break;
            }
            case "number": {
                number(scanner);
                break;
            }
        }

    }

    private void all(Scanner scanner) {
        System.out.println(" Result: ");
        dbConnector.getAllEquations().forEach(System.out::println);
        System.out.print(" Press Enter key to continue... ");
        scanner.nextLine();
    }

    private void solutions(Scanner scanner) {
        String input = null;
        List<Double> solutions = new ArrayList<>();

        System.out.println(" Enter solutions as numbers, or enter 'stop' to finalize your input.");

        while (true) {
            System.out.print(" Input: ");

            try {
                input = scanner.nextLine();

                if (input.equalsIgnoreCase("stop"))
                    break;

                Double solution = Double.parseDouble(input);
                solutions.add(solution);
            } catch (NumberFormatException e) {
                System.out.println(" Error: " + e.getMessage());
            }
        }

        List<Equation> equations = dbConnector.getAllEquations();
        List<Equation> filteredEquations = equations.stream().filter(equation -> {
            boolean containsSolution = false;
            for (Double solution: solutions) {
                if (equation.getCopyOfSolutions().contains(solution)) {
                    containsSolution = true;
                    break;
                }
            }
            return containsSolution;
        }).collect(Collectors.toList());

        System.out.println(" Results: ");
        filteredEquations.forEach(System.out::println);
        System.out.print(" Press Enter key to continue... ");
        scanner.nextLine();
    }

    private void number(Scanner scanner) {

        int number = 0;

        boolean enteredCorrectInteger = false;
        while (!enteredCorrectInteger) {
            System.out.print(" Enter number of solutions: ");
            String input = scanner.nextLine();

            try {
                number = Integer.parseInt(input);
                enteredCorrectInteger = true;
            } catch (Exception e) {
                System.out.println(" Error: " + e.getMessage());
            }
        }

        final int solutionsSize = number;

        List<Equation> equations = dbConnector.getAllEquations();
        List<Equation> filteredEquations = equations
                .stream()
                .filter(equation -> equation.getCopyOfSolutions().size() == solutionsSize)
                .collect(Collectors.toList());

        System.out.println(" Result: ");
        filteredEquations.forEach(System.out::println);
        System.out.print(" Press Enter key to continue... ");
        scanner.nextLine();
    }

}
