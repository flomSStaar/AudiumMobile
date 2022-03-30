package uqac.dim.audium.model.entity;

public class Track {

    private String titre;
    private Artist artist;
    private String path; // A changer

    public Track(String titre, Artist artist, String path) {
        this.titre = titre;
        this.artist = artist;
        this.path = path;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
