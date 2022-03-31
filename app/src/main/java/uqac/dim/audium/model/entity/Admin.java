package uqac.dim.audium.model.entity;

public class Admin extends User {

    /**
     * Constructs a new admin.
     * @param firstName First name
     * @param lastName Last name
     * @param age Age
     * @param login Login
     */
    public Admin(String firstName, String lastName, int age, String login) {
        super(firstName, lastName, age, login);
    }
}
