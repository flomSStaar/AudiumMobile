package uqac.dim.audium.model.entity;

import java.util.ArrayList;
import java.util.List;

public class User extends Person {


    private String login;
    private String password;

    private List<Playlist> playlists;

    public User(String firstName, String lastName, int age, String login, String password) {
        super(firstName, lastName, age);
        this.login = login;
        this.password = password;
        playlists = new ArrayList<>();
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
