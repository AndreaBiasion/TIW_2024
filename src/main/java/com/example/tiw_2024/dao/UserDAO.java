package com.example.tiw_2024.dao;

import com.example.tiw_2024.beans.Group;
import com.example.tiw_2024.beans.User;

import java.sql.Connection;
import java.util.List;

public class UserDAO {

    Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    public User checkCredentials(String email, String password) {
        return null;
    }

    public void addUser(User user) {

    }

    public void createGroup(Group group) {

    }

    public List<Group> findGroups() {
        return null;
    }

    public void acceptInvite() {

    }
}
