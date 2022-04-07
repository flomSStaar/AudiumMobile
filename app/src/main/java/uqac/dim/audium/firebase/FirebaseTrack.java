package uqac.dim.audium.firebase;

public final class FirebaseTrack {
    private Long id;
    private String title;
    private String path;
    private String imagePath;
    private Long artistId;
    private Long album;

    public FirebaseTrack(Long id, String title, String path, String imagePath, Long artistId, Long album) {
        this.id = id;
        this.title = title;
        this.path = path;
        this.imagePath = imagePath;
        this.artistId = artistId;
        this.album = album;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Long getArtistId() {
        return artistId;
    }

    public Long getAlbum() {
        return album;
    }
}
