package org.equationdb.equation;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class EquationEvaluator {

    public void checkCorrectness(String equation) {

        if (!equation.contains("x"))
            throw new IllegalArgumentException("Equation doesn't contain 'x' variable!");

        String simplifiedEquation = simplifyEquation(equation);
        Expression expression = new ExpressionBuilder(simplifiedEquation)
                .variables("x")
                .build()
                .setVariable("x", 0);

        expression.evaluate();
    }

    public boolean evaluateEquation(String equation, String value, double precision) {

        String simplifiedEquation = simplifyEquation(equation);

        Expression expression = new ExpressionBuilder(simplifiedEquation)
                .variables("x")
                .build()
                .setVariable("x", Double.parseDouble(value));

        double result = expression.evaluate();

        return -precision <= result && result <= precision;
    }

    private String simplifyEquation(String equation) {
        String simplifiedEquation = null;
        String[] equationParts = equation.split("=");
        if (equationParts.length != 2)
            throw new IllegalArgumentException("Incorrect equation structure, one and only one equal sign '=' must be present!");
        simplifiedEquation = equationParts[0] + "-(" + equationParts[1] + ")";
        return simplifiedEquation;
    }



}
