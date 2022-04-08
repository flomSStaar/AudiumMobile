package uqac.dim.audium.firebase;

public final class FirebaseTrack {
    private Long id;
    private String name;
    private String path;
    private String imagePath;
    private Long artistId;
    private Long albumId;

    public FirebaseTrack(Long id, String name, String path, String imagePath, Long artistId, Long albumId) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.imagePath = imagePath;
        this.artistId = artistId;
        this.albumId = albumId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public Long getAlbumId() {
        return albumId;
    }
}
