package org.equationdb;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.equationdb.equation.EquationEvaluator;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String... args) {
        try {
            new EquationDataBase().start(args);
        } catch (Exception e) {
            System.out.println(" " + e.getMessage());
        }

    }

}
