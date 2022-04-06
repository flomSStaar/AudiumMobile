package uqac.dim.audium.model.entity;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User extends Person implements Serializable {

    protected String username;
    @PropertyName("admin")
    protected boolean isAdmin;
    protected List<Playlist> playlists = new ArrayList<>();

    /**
     * Construcs a new User.
     * This constructor is used for deserializing from database.
     * Don't use this constructor!
     */
    protected User() {
        super();
    }

    /**
     * Constructs a new User with first name, last name, age and login.
     *
     * @param firstName First name of this user
     * @param lastName  Last name of this user
     * @param age       Age of this user
     * @param username  Login of this user
     * @param isAdmin   Admin state of this user
     */
    public User(String firstName, String lastName, int age, String username, boolean isAdmin) {
        super(firstName, lastName, age);
        this.username = username;
        this.isAdmin = isAdmin;
        playlists = new ArrayList<>();
    }

    /**
     * Returns the playlists of this user.
     *
     * @return Playlists of this user
     */
    @Exclude
    public List<Playlist> getPlaylists() {
        return playlists;
    }

    /**
     * Sets the list of playlist of this user.
     *
     * @param playlists The new list of playlist of this user
     */
    public void setPlaylists(List<Playlist> playlists) {
        if (playlists != null) {
            this.playlists = playlists;
        } else {
            throw new IllegalArgumentException("playlists cannot be null");
        }
    }

    /**
     * Returns the login of this user.
     *
     * @return Login of this user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns if the user is admin
     *
     * @return True if admin otherwise false
     */
    public boolean isAdmin() {
        return isAdmin;
    }


    @Override
    public String toString() {
        /*return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", username='" + username + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
        */
        return username;
    }
}
