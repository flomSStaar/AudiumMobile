package uqac.dim.audium.model.entity;

import java.util.List;
import java.util.Objects;

public class Track implements Comparable<Track> {

    protected String title;
    protected Long artist;
    protected Long id;
    protected List<Long> albums;

    private Track(){}

    /**
     * Constructs a new Track composed of a title, the artist and an id.
     *
     * @param title  Title of this track
     * @param artist Artist of this track
     * @param id     Id of this track
     */
    public Track(String title, Long artist, Long id) {
        setTitle(title);
        setArtist(artist);
        setId(id);
    }

    /**
     * Returns the title of this track.
     *
     * @return Title of this track
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of this track.
     *
     * @param title The new title of this track
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the artist of this track.
     *
     * @return Artist of this track
     */
    public Long getArtist() {
        return artist;
    }

    /**
     * Sets the artist of this track.
     *
     * @param artist The new artist of this track
     */
    public void setArtist(Long artist) {
        this.artist = artist;
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
        return title.compareTo(track.title);
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
        return title;
    }
}
