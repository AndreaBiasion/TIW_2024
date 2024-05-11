package it.polimi.tiw.dao;

import it.polimi.tiw.beans.Group;
import it.polimi.tiw.beans.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GroupDAO {

    Connection connection;

    public GroupDAO(Connection connection) {
        this.connection = connection;
    }

    // creates the group
    public void createGroup(List<User> partecipants) {

    }

    // returns the groups associated with a user.id
    public List<Group> getGroupsById(int id) throws SQLException {
        String query = "SELECT id,titolo FROM partecipazione join gruppo on partecipazione.idgruppo = gruppo.id WHERE idpart = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);

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
    public void getDetails(Group group) {

    }
}
