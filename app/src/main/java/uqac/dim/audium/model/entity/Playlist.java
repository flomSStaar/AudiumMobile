package uqac.dim.audium.model.entity;

public class Playlist extends TrackContainer {

    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Playlist(){}

    /**
     * Constructs a new Playlist with a title.
     *
     * @param title Title of this playlist
     */
    public Playlist(String title) {
        this(title, null);
    }

    /**
     * Constructs a new Playlist with a title and a description.
     *
     * @param title       Title of this playlist
     * @param description Description of this playslit
     */
    public Playlist(String title, String description) {
        super(title, description);
    }

    @Override
    public int compareTo(TrackContainer trackContainer) {
        return this.title.compareTo(trackContainer.title);
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
