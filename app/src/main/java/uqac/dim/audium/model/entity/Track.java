package uqac.dim.audium.model.entity;

import java.util.List;
import java.util.Objects;

public class Track implements Comparable<Track> {

    protected String name;
    protected Long artistId;
    protected Long id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    protected Long albumId;

    private Track(){}

    /**
     * Constructs a new Track composed of a title, the artist and an id.
     *
     * @param title  Title of this track
     * @param artist Artist of this track
     * @param id     Id of this track
     */
    public Track(String title, Long artist, Long id) {
        setName(title);
        setArtistId(artist);
        setId(id);
    }


    /**
     * Returns the id of this track.
     *
     * @return Id of this track
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id of this track.
     *
     * @param id The new id of this track
     */
    private void setId(Long id) {
        this.id = id;
    }




    @Override
    public int compareTo(Track track) {
        return name.compareTo(track.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Track track = (Track) o;
        return Objects.equals(id, track.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
