package org.equationdb.equation;

import java.util.ArrayList;
import java.util.List;

public class Equation {

    private final ArrayList<Double> solutions = new ArrayList<>();
    private String equation;

    public Equation() {

    }

    public Equation(String equation) {
        this.equation = equation;
    }

    public void addSolution(Double solution) {
        this.solutions.add(solution);
    }

    public void addSolutions(List<Double> solutions) {
        this.solutions.addAll(solutions);
    }

    public void clearSolutions() {
        this.solutions.clear();
    }

    public ArrayList<Double> getCopyOfSolutions() {
        return new ArrayList<>(this.solutions);
    }

    public String getEquation() {
        return this.equation;
    }

    @Override
    public String toString() {
        return "Equation{" +
                "equation='" + equation +
                ", solutions=" + solutions + '\'' +
                '}';
    }
}
