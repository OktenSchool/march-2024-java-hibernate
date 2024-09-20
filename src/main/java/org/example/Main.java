package org.example;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;

public class Main {

    // List - ArrayList, LinkedList
    // Driver - PostresDriver, MysqlDriver, OracleDriver

    @SneakyThrows
    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/XXX", "postgres", "postgres")) {
            System.out.println("Is connected to Database: " + connection.isValid(5));

            System.out.println("Creating 'person' table...");
            createPersonTable(connection);

            createPersonWithPreparedStatement(connection, new Person(1, "Mike"));
            createPersonWithPreparedStatement(connection, new Person(2, "test'); DELETE FROM person WHERE ''=('"));

            getPersonById(connection, 1).ifPresent(person -> System.out.println("Person with id 1 exists: " + person));
            getPersonById(connection, 3).ifPresentOrElse(
                    person -> System.out.println("Person with id 3 exists"),
                    () -> System.out.println("Person with id 3 does not exist"));
        }
    }

    @SneakyThrows
    public static void createPersonTable(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS person (id INT, name VARCHAR(255))");
        }
    }

    // demonstrates SQL Injection
    @SneakyThrows
    public static void createPersonWithStatement(Connection connection, Person person) {
        try (Statement statement = connection.createStatement()) {
            statement.execute("INSERT INTO person (id, name) VALUES (" + person.getId() + ", '" + person.getName() + "')");
        }
    }

    // safes from SQL Injection
    @SneakyThrows
    public static void createPersonWithPreparedStatement(Connection connection, Person person) {
        String sql = "INSERT INTO person (id, name) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, person.getId());
            preparedStatement.setString(2, person.getName());
            preparedStatement.execute();
        }
    }

    @SneakyThrows
    public static Optional<Person> getPersonById(Connection connection, int id) {
        String sql = "SELECT * FROM person WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Person(resultSet.getInt("id"), resultSet.getString("name")));
                }
            }
        }
        return Optional.empty();
    }
}