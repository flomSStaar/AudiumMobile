package uqac.dim.audium.model.entity;

import java.util.Set;
import java.util.TreeSet;

public class Artist extends Person{

    private Set<Album> albums;

    public Artist(String firstName, String lastName, int age) {
        super(firstName, lastName, age);
        albums = new TreeSet<>();
    }

    public Set<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(Set<Album> albums) {
        this.albums = albums;
    }

    private void addAlbum(Album a){
        albums.add(a);
    }

    private void removeAlbum(Album a){
        albums.remove(a);
    }
}
