package it.polimi.tiw.dao;

import it.polimi.tiw.beans.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class handles data access operations related to users.
 */
public class UserDAO {

    private Connection connection;

    /**
     * Constructs a new UserDAO with the given database connection.
     * @param connection the database connection to be used by the DAO.
     */
    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Checks user credentials against the database.
     * @param email the email of the user.
     * @param password the password of the user.
     * @return a User object if the credentials are valid, null otherwise.
     * @throws SQLException if an SQL exception occurs while accessing the database.
     */
    public User checkCredentials(String email, String password) throws SQLException {
        String query = "SELECT * FROM utente WHERE email = ? AND password = ?";
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                return null; // No user found with the provided credentials
            } else {
                resultSet.next();
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setSurname(resultSet.getString("surname"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                return user; // Return the found user
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    /**
     * Adds a new user to the database.
     * @param name the name of the user.
     * @param surname the surname of the user.
     * @param email the email of the user.
     * @param password the password of the user.
     * @throws SQLException if an SQL exception occurs while accessing the database.
     */
    public void addUser(String name, String surname, String email, String password) throws SQLException {
        String query = "INSERT INTO utente (nome, cognome, email, password) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, surname);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, password);
            preparedStatement.executeUpdate(); // Execute the insert query
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }
}
