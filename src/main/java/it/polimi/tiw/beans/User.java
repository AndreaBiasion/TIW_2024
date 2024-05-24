package it.polimi.tiw.beans;

public class User {

    private String username;
    private String name;
    private String surname;
    private String email;
    private String password;
    /**
     *
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the username of the user
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return the name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the user
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return the surname of the user
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Set the surname of the user
     * @param surname
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     *
     * @return the email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the email of the user
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }
    /**
     *
     * @return the password of the user
     */
    public String getPassword() {
        return password;
    }
    /**
     * Set the password of the user
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

}
