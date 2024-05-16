package it.polimi.tiw.dao;

import it.polimi.tiw.beans.Group;
import it.polimi.tiw.beans.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

        try (PreparedStatement pstatement = connection.prepareStatement(query);) {
            pstatement.setString(1, email);
            pstatement.setString(2, password);
            try (ResultSet result = pstatement.executeQuery();) {
                if (!result.isBeforeFirst()) // no results, credential check failed
                    return null;
                else {
                    result.next();
                    User user = new User();
                    user.setUsername(result.getString("username"));
                    user.setName(result.getString("nome"));
                    user.setSurname(result.getString("cognome"));
                    user.setEmail(email);
                    user.setPassword(password);
                    return user;
                }
            }
        }
    }

    public boolean checkRegister(String email, String username) throws SQLException {
        String query = "SELECT * FROM utente WHERE email = ? or username = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, email);
        statement.setString(2, username);
        ResultSet result = statement.executeQuery();

        return result.isBeforeFirst();
    }

    /**
     * Adds a new user to the database.
     * @param name the name of the user.
     * @param surname the surname of the user.
     * @param email the email of the user.
     * @param password the password of the user.
     * @throws SQLException if an SQL exception occurs while accessing the database.
     */
    public void addUser(String username, String name, String surname, String email, String password) throws SQLException {
        String query = "INSERT INTO utente (username, nome, cognome, email, password) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, surname);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5, password);
            preparedStatement.executeUpdate(); // Execute the insert query
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    public List<User> getUsersFromGroup(int idgroup) throws SQLException {
        String query = "select nome,cognome from partecipazione join utente on partecipazione.idpart = utente.username where idgruppo = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, idgroup);

        ResultSet resultSet = statement.executeQuery();

        List<User> users = new ArrayList<>();
        while (resultSet.next()) {
            User user = new User();
            user.setName(resultSet.getString("nome"));
            user.setSurname(resultSet.getString("cognome"));

            users.add(user);
        }

        return users;
    }

    public List<User> getAllUsers(String username) throws SQLException {
        String query = "select * from utente where username <> ? order by utente.cognome asc";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);

        ResultSet resultSet = statement.executeQuery();

        List<User> users = new ArrayList<>();
        while (resultSet.next()) {
            User user = new User();
            user.setUsername(resultSet.getString("username"));
            user.setName(resultSet.getString("nome"));
            user.setSurname(resultSet.getString("cognome"));

            users.add(user);
        }

        return users;
    }


}
