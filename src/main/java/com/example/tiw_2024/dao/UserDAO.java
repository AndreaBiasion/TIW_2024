package com.example.tiw_2024.dao;

import com.example.tiw_2024.beans.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    Connection connection;


    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    public User checkCredentials(String email, String password) throws SQLException {
        String query = "SELECT * FROM utente WHERE email = ? AND password = ?";
        ResultSet resultSet = null;

        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();

            // in case of no results
            if (!resultSet.next()) {
                return null;
            } else {
                resultSet.next();
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setSurname(resultSet.getString("surname"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));

                return user;
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

    public void addUser(String name, String surname, String email, String password) throws SQLException {

        String query = "INSERT INTO utente (nome, cognome, email, password) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, surname);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, password);
            preparedStatement.executeUpdate();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }

    }


}
