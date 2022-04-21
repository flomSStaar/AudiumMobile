package uqac.dim.audium.firebase;

import java.util.List;

public final class FirebaseAlbum {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private Long artistId;
    private List<Long> tracksId;

    public FirebaseAlbum(Long id, String title, String description, String imageUrl, Long artistId, List<Long> tracksId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public Long getArtistId() {
        return artistId;
    }

    public List<Long> getTracksId() {
        return tracksId;
    }
}
