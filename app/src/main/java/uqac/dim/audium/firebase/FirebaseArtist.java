package uqac.dim.audium.firebase;

import java.util.List;

public final class FirebaseArtist {
    private Long id;
    private String firstName;
    private String lastName;
    private int age;
    private String stageName;
    private List<Long> tracksId;
    private List<Long> albumsId;
    private String imagePath;

    public FirebaseArtist(Long id, String firstName, String lastName, int age, String stageName, List<Long> tracksId, List<Long> albumsId, String imagePath) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.stageName = stageName;
        this.albumsId = albumsId;
        this.imagePath = imagePath;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return age;
    }

    public String getStageName() {
        return stageName;
    }

    public List<Long> getTracksId() {
        return tracksId;
    }

    public List<Long> getAlbumsId() {
        return albumsId;
    }

    public String getImagePath() {
        return imagePath;
    }
}
