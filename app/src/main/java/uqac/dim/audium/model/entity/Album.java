package uqac.dim.audium.model.entity;

public class Album extends TrackContainer {

    protected Long id;
    protected Long artistId;

    private Album() {
        super();
    }

    public Album(String title, String description, Long id, Long artistId) {
        super(title, description);
        this.id = id;
        this.artistId = artistId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id != null && id > 0) {
            this.id = id;
        } else {
            throw new IllegalArgumentException("id cannot be null or lower or equal than 0");
        }
    }

    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistID) {
        if (artistID != null && artistID > 0) {
            this.artistId = artistID;
        } else {
            throw new IllegalArgumentException("artistId cannot be null or lower or equal than 0");
        }
    }

    @Override
    public int compareTo(TrackContainer trackContainer) {
        return this.title.compareTo(trackContainer.title);
    }

    @Override
    public String toString() {
        return "Album{" +
                "id=" + id +
                ", artistId=" + artistId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
