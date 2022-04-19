package uqac.dim.audium.model.entity;

public class Album extends TrackContainer {

    protected Long id;
    protected Long artistId;
    protected String imagePath;

    private Album() {
        super();
    }

    public Album(String title, String description, Long id, Long artistId,String path) {
        super(title, description);
        this.id = id;
        this.artistId = artistId;
        this.imagePath = path;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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
