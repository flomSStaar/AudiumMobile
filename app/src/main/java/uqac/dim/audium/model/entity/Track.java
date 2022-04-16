package uqac.dim.audium.model.entity;

import java.util.List;
import java.util.Objects;

public class Track implements Comparable<Track> {

    protected Long id;
    protected String name;
    protected Long artistId;
    protected Long albumId;
    protected String url;
    protected String imageUrl;
    protected List<Long> playlistsId;

    private Track() {
    }

    /**
     * Constructs a new Track composed of an id, a name, the url, the artist id, the album id and the image url.
     *
     * @param id       Id of this track
     * @param name     Title of this track
     * @param url      Url of this track
     * @param artistId Artist Id of this track
     * @param albumId  Album Id of this track
     * @param imageUrl Image url of this track
     */
    public Track(Long id, String name, String url, Long artistId, Long albumId, String imageUrl) {
        setId(id);
        setName(name);
        setUrl(url);
        setArtistId(artistId);
        setAlbumId(albumId);
        setImageUrl(imageUrl);
    }

    /**
     * Returns the id of this track.
     *
     * @return Id of this track
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id of this track.
     *
     * @param id The new id of this track
     */
    private void setId(Long id) {
        if (id != null && id > 0) {
            this.id = id;
        } else {
            throw new IllegalArgumentException("id cannot be null or lower or equal than 0");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        } else {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
    }

    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        if (artistId != null && artistId > 0) {
            this.artistId = artistId;
        } else {
            throw new IllegalArgumentException("artistId cannot be null or lower or equal than 0");
        }
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        if (albumId == null || albumId > 0) {
            this.albumId = albumId;
        } else {
            throw new IllegalArgumentException("albumId cannot be null or lower or equal than 0");
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Long> getPlaylistsId() {
        return playlistsId;
    }

    public void setPlaylistsId(List<Long> playlistsId) {
        this.playlistsId = playlistsId;
    }

    @Override
    public int compareTo(Track track) {
        return name.compareTo(track.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Track track = (Track) o;
        return Objects.equals(id, track.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Track{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", artistId=" + artistId +
                ", albumId=" + albumId +
                '}';
    }
}
