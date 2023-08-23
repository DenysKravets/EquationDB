package org.equationdb.database;

import org.equationdb.equation.Equation;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBConnector {

    private String user;
    private String password;
    private String myUrl;
    private String databaseName;

    public DBConnector() throws IOException, SQLException {
        loadDefaultsFromPropertiesFile();
        createTableIfNotExist();
    }

    private void loadDefaultsFromPropertiesFile() throws IOException {

        try {
            String fileLocation = System.getProperty("user.dir") + "/default_database_credentials.properties";
            InputStream inputStream = new FileInputStream(fileLocation);
            String data = readFromInputStream(inputStream);


            HashMap<String, String> properties = new HashMap<>();
            String[] lines = data.split("\n");
            for (String line : lines) {
                int separatorLocation = line.indexOf(':');
                String property = line.substring(0, separatorLocation).trim();
                String value = line.substring(separatorLocation + 1).trim();
                properties.put(property, value);
            }

            this.myUrl = properties.get("url");
            this.databaseName = properties.get("database_name");
            this.user = properties.get("user");
            this.password = properties.get("password");

            System.out.println("\n Database info loaded successfully!");

        } catch (Exception e) {
            System.out.println(" Please populate all fields in 'default_database_credentials.properties' file." +
                    " Create file if it doesn't exist, place in the same folder as jar file." +
                    " These are required fields: " +
                    " url: <url>\n" +
                    " database_name: <database name>\n" +
                    " user: <username>\n" +
                    " password: <password>");
            throw e;
        }

    }

    private String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    private void createTableIfNotExist() throws SQLException {
        // Create table if it doesn't exist

        Connection conn = DriverManager.getConnection(myUrl + databaseName, user, password);

        String sqlCreate = "create table if not exists equations("
                + "   id int not null auto_increment,"
                + "   equation varchar(256) not null,"
                + "   solution double,"
                + "   primary key (id)"
                + ")";

        Statement stmt = conn.createStatement();
        stmt.execute(sqlCreate);

    }

    public void saveEquation(Equation equation) {

        try {

            Connection conn = DriverManager.getConnection(myUrl + databaseName, user, password);

            if (equation.getCopyOfSolutions().size() == 0) {

                String query = "insert into equations (equation)" +
                        " values (\"" + equation.getEquation() + "\")";
                Statement statement = conn.createStatement();
                statement.executeUpdate(query);
                statement.close();

            } else {

                Statement statement = conn.createStatement();
                for (Double solution: equation.getCopyOfSolutions()) {
                    String query = "insert into equations (equation, solution)" +
                            " values (\"" + equation.getEquation() + "\", " + solution + ")";
                    statement.addBatch(query);
                }

                statement.executeBatch();
                statement.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void saveAllEquations(List<Equation> equations) {
        equations.forEach(this::saveEquation);
    }

    public List<Equation> getAllEquations() {
        List<Equation> equations = new ArrayList<>();
        HashMap<String, Equation> equationHashMap = new HashMap<>();

        try {

            Connection conn = DriverManager.getConnection(myUrl + databaseName, user, password);

            String query = "SELECT * FROM equations";
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String equationString = resultSet.getString("equation");
                double solution = resultSet.getDouble("solution");

                if (equationHashMap.containsKey(equationString)) {
                    Equation existingEquation = equationHashMap.get(equationString);
                    existingEquation.addSolution(solution);
                } else {
                    Equation newEquation = new Equation(equationString);
                    newEquation.addSolution(solution);
                    equationHashMap.put(equationString, newEquation);
                }
            }
            statement.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        equationHashMap.forEach((key, value) -> equations.add(value));

        return equations;
    }

}
