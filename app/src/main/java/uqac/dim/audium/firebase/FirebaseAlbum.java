package uqac.dim.audium.firebase;

import java.util.List;

public final class FirebaseAlbum {
    private Long id;
    private String title;
    private String description;
    private String imagePath;
    private Long artistId;
    private List<Long> tracksId;

    public FirebaseAlbum(Long id, String title, String description, String imagePath, Long artistId, List<Long> tracksId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
        this.artistId = artistId;
        this.tracksId = tracksId;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Long getArtistId() {
        return artistId;
    }

    public List<Long> getTracksId() {
        return tracksId;
    }
}
