package uqac.dim.audium.model.entity;

public class Playlist extends TrackContainer {

    protected Long id;
    protected String imageUrl;
    private String username;


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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    @Override
    public int compareTo(TrackContainer trackContainer) {
        return this.title.compareTo(trackContainer.title);
    }

    @Override
    public String toString() {
        /*return "Playlist{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';/*

         */
        return title;
    }
}
