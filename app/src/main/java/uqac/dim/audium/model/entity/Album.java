package uqac.dim.audium.model.entity;

public class Album extends TrackContainer {



    protected Long id;
    protected Long artist;

    private Album(){
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    public Album(String title, String description, Long id){
        super(title, description);
        artist=id;
    }

    public Long getArtist() {
        return artist;
    }

    public void setArtist(Long artistID) {
        this.artist = artistID;
    }

    public Album(String title, String description) {
        super(title, description);

    }


    @Override
    public int compareTo(TrackContainer trackContainer) {
        return this.title.compareTo(trackContainer.title);
    }

    @Override
    public String toString() {
        return title;
    }
}
