package uqac.dim.audium.firebase;

import java.util.List;

public final class FirebaseTrack {
    private Long id;
    private String name;
    private String url;
    private String imageUrl;
    private Long artistId;
    private Long albumId;
    private List<Long> playlistsId;

    public FirebaseTrack(Long id, String name, String url, String imageUrl, Long artistId, Long albumId, List<Long> playlistsId) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.imageUrl = imageUrl;
        this.artistId = artistId;
        this.albumId = albumId;
        this.playlistsId = playlistsId;
    }

    public List<Long> getPlaylistsId() {
        return playlistsId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Long getArtistId() {
        return artistId;
    }

    public Long getAlbumId() {
        return albumId;
    }
}
