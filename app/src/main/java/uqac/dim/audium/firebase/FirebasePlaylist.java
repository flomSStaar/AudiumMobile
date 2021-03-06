package uqac.dim.audium.firebase;

import java.util.List;

public class FirebasePlaylist {
    private Long id;
    private String username;
    private String title;
    private String description;
    private List<Long> tracksId;
    private String imageUrl;

    public FirebasePlaylist(Long id, String idUser, String title, String description, List<Long> tracksId, String imageUrl) {
        this.id = id;
        this.username = idUser;
        this.title = title;
        this.description = description;
        this.tracksId = tracksId;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<Long> getTracksId() {
        return tracksId;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
