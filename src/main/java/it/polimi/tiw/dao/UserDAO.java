package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.*;

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
        PreparedStatement pstatement = null;
        ResultSet result = null;

        try {
            pstatement = connection.prepareStatement(query);
            pstatement.setString(1, email);
            pstatement.setString(2, password);

            result = pstatement.executeQuery();
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
        } catch (SQLException e) {
            throw new SQLException();

        } finally {
            try {
                if (result != null) {
                    result.close();
                }
            }catch (Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if (pstatement != null) {
                    pstatement.close();
                }
            }catch (Exception e2) {
                throw new SQLException(e2);
            }
        }
    }

    public boolean checkRegister(String email, String username) throws SQLException {
        String query = "SELECT * FROM utente WHERE email = ? or username = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = connection.prepareStatement(query);
            statement.setString(1, email);
            statement.setString(2, username);
            result = statement.executeQuery();
        } catch (SQLException e) {
            throw new SQLException();

        } finally {
            try {
                if (result != null) {
                    result.close();
                }
            }catch (Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if (statement != null) {
                    statement.close();
                }
            }catch (Exception e2) {
                throw new SQLException(e2);
            }
        }

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
        } catch (SQLException e){
            throw new SQLException(e);

        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }catch (Exception e2) {
                throw new SQLException(e2);
            }
        }
    }
    /**
     * Find the users of the group
     * @param idgroup
     * @return all the users of the group
     * @throws SQLException
     */
    public List<User> getUsersFromGroup(int idgroup) throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "select nome,cognome,email,username, password from partecipazione join utente on partecipazione.idpart = utente.username where idgruppo = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(query);
            statement.setInt(1, idgroup);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                User user = new User();
                user.setName(resultSet.getString("nome"));
                user.setSurname(resultSet.getString("cognome"));
                user.setEmail(resultSet.getString("email"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                users.add(user);
            }
        }  catch (SQLException e) {
            throw new SQLException();

        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            }catch (Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if (statement != null) {
                    statement.close();
                }
            }catch (Exception e2) {
                throw new SQLException(e2);
            }
        }

        return users;
    }
    /**
     * list of all users order by decreasing surname
     * @param username
     * @return the list of all users order by decreasing surname
     * @throws SQLException
     */
    public List<User> getAllUsers(String username) throws SQLException {
        String query = "select * from utente where username <> ? order by utente.cognome asc";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<User> users = new ArrayList<>();
        try {
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                User user = new User();
                user.setUsername(resultSet.getString("username"));
                user.setName(resultSet.getString("nome"));
                user.setSurname(resultSet.getString("cognome"));
                users.add(user);
            }
        }catch (SQLException e){
            throw new SQLException(e);
        }finally {
            try{
                resultSet.close();
            }catch (Exception e1){
                throw new SQLException("cannot close result");
            }
            try{
                statement.close();
            }catch (Exception e2){
                throw new SQLException("cannot close statement");
            }

        }
        return users;
    }
}