package at.fhcampuswien.mqtt.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

public class DatabaseConnection {

    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    //TODO Change parameters
    private String url = "jdbc:mysql://195.201.96.148:3306/jumo_data";
    private String username = "developer";
    private String password = "campus09";


    //TODO Optimization
    //String co2, String v2o, String temperature - parameter
    public void readDataBase(float temp1, float temp2, float temp3, float rH1, float rH2, float rH3, float P1, float P2, float P3, float TA, float TB, float VOC1, float VOC2, float CO2, float rH, float v) throws Exception {
        try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Setup the connection with the DB
            connect = DriverManager.getConnection(url, username, password);
            System.out.println("connected to database");
            // Statements allow to issue SQL queries to the database
            statement = connect.createStatement();

            // Result set get the result of the SQL query
            /* we do not need this method anymore
            resultSet = statement.executeQuery("select * from jumo_values");
            writeResultSet(resultSet);
             */


            // PreparedStatements can use variables and are more efficient
            preparedStatement = connect.prepareStatement("insert into  jumo_values values (default, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            // Parameters start with 1
            preparedStatement.setFloat(1, temp1);
            preparedStatement.setFloat(2, temp2);
            preparedStatement.setFloat(3, temp3);
            preparedStatement.setFloat(4, rH1);
            preparedStatement.setFloat(5, rH2);
            preparedStatement.setFloat(6, rH3);
            preparedStatement.setFloat(7, P1);
            preparedStatement.setFloat(8, P2);
            preparedStatement.setFloat(9, P3);
            preparedStatement.setFloat(10, TA);
            preparedStatement.setFloat(11, TB);
            preparedStatement.setFloat(12, VOC1);
            preparedStatement.setFloat(13, VOC2);
            preparedStatement.setFloat(14, CO2);
            preparedStatement.setFloat(15, rH);
            preparedStatement.executeUpdate();
            System.out.println("updates executed");

            /* show whats in the database
            preparedStatement = connect
                    .prepareStatement("SELECT ID, Temp1, Temp2, Temp3, rH1, rH2, rH3, P1, P2, P3, TA, TB, VOC1, VOC2, CO2, rh from jumo_values");
            resultSet = preparedStatement.executeQuery();
            writeResultSet(resultSet);
             */

            /*
            // Remove again the insert comment
            preparedStatement = connect
                    .prepareStatement("delete from feedback.comments where myuser= ? ; ");
            preparedStatement.setString(1, "Test");
            preparedStatement.executeUpdate();


            resultSet = statement
                    .executeQuery("select * from jumo_values");
            writeMetaData(resultSet);
             */

        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }

    }

    private void writeMetaData(ResultSet resultSet) throws SQLException {
        //  Now get some metadata from the database
        // Result set get the result of the SQL query

        System.out.println("The columns in the table are: ");

        System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
        for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
            System.out.println("Column " + i + " " + resultSet.getMetaData().getColumnName(i));
        }
    }

    private void writeResultSet(ResultSet resultSet) throws SQLException {
        // ResultSet is initially before the first data set
        while (resultSet.next()) {
            // It is possible to get the columns via name
            // also possible to get the columns via the column number
            // which starts at 1
            // e.g. resultSet.getSTring(2);
            Float Temp1 = resultSet.getFloat(2);
            Float Temp2 = resultSet.getFloat(3);
            Float Temp3 = resultSet.getFloat(4);
            Float rH1 = resultSet.getFloat(5);
            Float rH2 = resultSet.getFloat(6);
            Float rH3 = resultSet.getFloat(7);
            Float P1 = resultSet.getFloat(8);
            Float P2 = resultSet.getFloat(9);
            Float P3 = resultSet.getFloat(10);
            Float TA = resultSet.getFloat(11);
            Float TB = resultSet.getFloat(12);
            Float VOC1 = resultSet.getFloat(13);
            Float VOC2 = resultSet.getFloat(14);
            Float CO2 = resultSet.getFloat(15);
            Float rH = resultSet.getFloat(16);

            //print the values
            System.out.println("Temp1: " + Temp1);
            System.out.println("Temp2: " + Temp2);
            System.out.println("Temp3: " + Temp3);
            System.out.println("rH1: " + rH1);
            System.out.println("rH2: " + rH2);
            System.out.println("rH3: " + rH3);
            System.out.println("P1: " + P1);
            System.out.println("P2: " + P2);
            System.out.println("P3: " + P3);
            System.out.println("TA: " + TA);
            System.out.println("TB: " + TB);
            System.out.println("VOC1: " + VOC1);
            System.out.println("VOC: " + VOC2);
            System.out.println("CO2: " + CO2);
            System.out.println("rH: " + rH);

        }
    }

    // You need to close the resultSet
    private void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (connect != null) {
                connect.close();
            }
        } catch (Exception e) {
            System.out.println("Couldn't close");
        }
    }
}
