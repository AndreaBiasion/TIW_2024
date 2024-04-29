package com.example.tiw_2024.dao;

import com.example.tiw_2024.beans.Group;
import com.example.tiw_2024.beans.User;

import java.sql.Connection;
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
    public void getGroupsById(int id) {

    }

    // returns the details of a specific group
    public void getDetails(Group group) {

    }
}
