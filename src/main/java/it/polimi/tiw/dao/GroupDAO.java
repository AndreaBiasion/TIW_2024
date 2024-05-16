package it.polimi.tiw.dao;

import it.polimi.tiw.beans.Group;
import it.polimi.tiw.beans.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupDAO {

    Connection connection;

    public GroupDAO(Connection connection) {
        this.connection = connection;
    }

    // creates the group
    public void createGroup(List<String> parts_usernames, Group group, String username_creatore) throws SQLException {
        String titolo = group.getTitle();
        int durata = group.getActivity_duration();
        int min_part = group.getMin_parts();
        int max_part = group.getMax_parts();

        java.util.Date utilDate = new java.util.Date();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

        String insertGroupQuery = "INSERT INTO gruppo (username_creatore, titolo, data_creazione, durata_att, min_part, max_part) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertGroupQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, username_creatore);
            preparedStatement.setString(2, titolo);
            preparedStatement.setDate(3, sqlDate);
            preparedStatement.setInt(4, durata);
            preparedStatement.setInt(5, min_part);
            preparedStatement.setInt(6, max_part);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating group failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    group.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating group failed, no ID obtained.");
                }
            }
        }

        String insertParticipationQuery = "INSERT INTO partecipazione (idpart, idgruppo) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertParticipationQuery)) {
            for (String partId : parts_usernames) {
                preparedStatement.setString(1, partId);
                preparedStatement.setInt(2, group.getId());
                preparedStatement.executeUpdate();
            }
        }
    }


    // returns the groups associated with a user.id
    public List<Group> getGroupsByUsername(String username) throws SQLException {
        String query = "SELECT id,titolo FROM partecipazione join gruppo on partecipazione.idgruppo = gruppo.id WHERE idpart = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);

        ResultSet resultSet = statement.executeQuery();

        List<Group> groups = new ArrayList<>();
        while (resultSet.next()) {
            Group group = new Group();
            group.setId(resultSet.getInt("id"));
            group.setTitle(resultSet.getString("titolo"));

            groups.add(group);
        }

        return groups;
    }
    

    // returns the details of a specific group
    public Group getGroupById(int idgroup) throws SQLException {
        String query = "SELECT * FROM gruppo WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, idgroup);

        ResultSet resultSet = statement.executeQuery();

        if(!resultSet.isBeforeFirst())
            return null;


        resultSet.next();
        Group group = new Group();
        group.setId(resultSet.getInt("id"));
        group.setTitle(resultSet.getString("titolo"));
        group.setDate_creation(resultSet.getTimestamp("data_creazione"));
        group.setActivity_duration(resultSet.getInt("durata_att"));
        group.setMin_parts(resultSet.getInt("min_part"));
        group.setMax_parts(resultSet.getInt("max_part"));
        return group;

    }
}
