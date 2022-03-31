package uqac.dim.audium.model.entity;

public class Album extends TrackContainer {

    protected Artist author;

    /**
     * Constructs a new Album with a title and the author of this album.
     *
     * @param title  Title of this album
     * @param author Author of this album
     */
    public Album(String title, Artist author) {
        this(title, null, author);
    }

    /**
     * Constructs a new Album with a title, a description and the author of this album.
     *
     * @param title       Title of this album
     * @param description Description of this album
     * @param author      Author of this album
     */
    public Album(String title, String description, Artist author) {
        super(title, description);
        this.author = author;
    }

    /**
     * Returns the author of this album.
     *
     * @return Author of this album
     */
    public Artist getAuthor() {
        return author;
    }

    /**
     * Sets the author of this album.
     *
     * @param author The new author of this album
     */
    public void setAuthor(Artist author) {
        this.author = author;
    }

    @Override
    public int compareTo(TrackContainer trackContainer) {
        return this.title.compareTo(trackContainer.title);
    }
}
