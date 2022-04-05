package uqac.dim.audium.firebase;

import uqac.dim.audium.model.entity.User;

public class FirebaseUser extends User {

    private final String password;

    /**
     * Constructs a new FirebaseUser with first name, last name, age and login.
     *
     * @param firstName First name of this user
     * @param lastName  Last name of this user
     * @param age       Age of this user
     * @param username  Login of this user
     */
    public FirebaseUser(String firstName, String lastName, int age, String username, String password) {
        super(firstName, lastName, age, username);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
