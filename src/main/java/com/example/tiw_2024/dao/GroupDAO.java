package com.example.tiw_2024.dao;

import com.example.tiw_2024.beans.Group;

import java.sql.Connection;
import java.util.List;

public class GroupDAO {

    Connection connection;

    public GroupDAO(Connection connection) {
        this.connection = connection;
    }

    // returns the groups associated with a user.id
    public void getGroupsById(int id) {

    }

    // returns the details of a specific group
    public void getDetails(Group group) {

    }
}
