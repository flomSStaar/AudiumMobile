package uqac.dim.audium.model.entity;

import java.util.ArrayList;
import java.util.List;

public class User extends Person {

    // Mettre un id ?
    protected String login;
    protected List<Playlist> playlists;

    /**
     * Constructs a new User with first name, last name, age and login.
     *
     * @param firstName First name of this user
     * @param lastName  Last name of this user
     * @param age       Age of this user
     * @param login     Login of this user
     */
    public User(String firstName, String lastName, int age, String login) {
        super(firstName, lastName, age);
        setLogin(login);
        playlists = new ArrayList<>();
    }

    /**
     * Returns the playlists of this user.
     *
     * @return Playlists of this user
     */
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
    public String getLogin() {
        return login;
    }

    /**
     * Sets the login of this user.
     *
     * @param login The new login of this user
     */
    public void setLogin(String login) {
        if (login != null && !login.isEmpty()) {
            this.login = login;
        } else {
            throw new IllegalArgumentException("login cannot be null or empty");
        }
    }
}
