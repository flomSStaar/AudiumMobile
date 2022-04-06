package uqac.dim.audium.model.entity;

import com.google.firebase.database.Exclude;

import java.util.Set;
import java.util.TreeSet;

public class Artist extends Person {

    protected Long id;
    protected Set<Album> albums;
    protected String stageName;

    public Artist(){}

    /**
     * Constructs a new artist having only artist name and age.
     *
     * @param stageName Stage name
     * @param age       Age of the artist
     */
    public Artist(String stageName, int age, Long id) {
        super("a", "a", age); //astuce pour pouvoir affecter artistName
        setStageName(stageName);
        setId(id);
        setFirstName(null);
        setLastName(null);
    }

    /**
     * Constructs a new Artist with first name, last name and age of the artist.
     *
     * @param firstName First name of the artist
     * @param lastName  Last Name of the artist
     * @param age       Age of the artist
     */
    public Artist(String firstName, String lastName, int age, Long id) {
        this(firstName, lastName, null, age,id);
    }


    /**
     * Constructs a new Artist with first name, last name, stage name and age of the artist.
     *
     * @param firstName First name of the artist
     * @param lastName  Last name of the artist
     * @param stageName Stage name of the artist
     * @param age       Age of the artist
     */
    public Artist(String firstName, String lastName, String stageName, int age, Long id) {
        this(firstName, lastName, stageName, age, new TreeSet<>(),id);
    }

    /**
     * Constructs a new Artist with all attributes of the artist.
     * Only use this constructor for loading artist from database.
     *
     * @param firstName First name of the artist
     * @param lastName  Last name of the artist
     * @param stageName Stage name of the artist
     * @param age       Age of the artist
     * @param albums    Albums of the artist
     */
    public Artist(String firstName, String lastName, String stageName, int age, Set<Album> albums, Long id) {
        super(firstName, lastName, age);
        setStageName(stageName);
        setId(id);
        setAlbums(albums);
    }

    /**
     * Returns the albums of this artist.
     *
     * @return Albums of this artist
     */
    @Exclude
    public Set<Album> getAlbums() {
        return albums;
    }

    /**
     * Change the albums of this artist.
     *
     * @param albums The new albums of this artist
     */
    public void setAlbums(Set<Album> albums) {
        if (albums != null) {
            this.albums = albums;
        } else {
            throw new IllegalArgumentException("albums cannot be null");
        }
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id != null && id > 0 ) {
            this.id = id;
        } else {
            throw new IllegalArgumentException("id cannot be lower than 0 or null");
        }
    }


    /**
     * Returns the stage name of this artist.
     *
     * @return Stage name of this artist
     */
    public String getStageName() {
        return stageName;
    }

    /**
     * Sets the stage name of this artist.
     *
     * @param stageName The new stage name for this artist
     */
    public void setStageName(String stageName) {
        if ((stageName == null || stageName.trim().isEmpty()) && firstName != null && lastName != null) {
            this.stageName = null;
        } else if (stageName != null && !stageName.trim().isEmpty()) {
            this.stageName = stageName;
        } else {
            throw new IllegalArgumentException("artistName cannot be null or empty");
        }
    }

    //TODO
    private void addAlbum(Album a) {
        if (a != null) {
            albums.add(a);
        }
    }

    //TODO
    private void removeAlbum(Album a) {
        albums.remove(a);
    }

    /**
     * Overriding setFirstName to implement the possibility of having only an artist name.
     *
     * @param firstName First name of the person
     */
    @Override
    public void setFirstName(String firstName) {
        if ((firstName == null || firstName.trim().isEmpty()) && stageName != null) {
            this.firstName = null;
        } else if (firstName != null && !firstName.trim().isEmpty()) {
            this.firstName = firstName;
        } else {
            throw new IllegalArgumentException("firstName cannot be null or empty");
        }
    }

    /**
     * Overriding setLastName to implement the possibility of having only an artist name.
     *
     * @param lastName Last name of the person
     */
    @Override
    public void setLastName(String lastName) {
        if ((lastName == null || lastName.trim().isEmpty()) && stageName != null) {
            this.lastName = null;
        } else if (lastName != null && !lastName.trim().isEmpty()) {
            this.lastName = lastName;
        } else {
            throw new IllegalArgumentException("lastName cannot be null or empty");
        }
    }

    /**
     * Returns a string representation of this artist.
     *
     * @return A string representation of this artist.
     */
    @Override
    public String toString() {
        return "Artist{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", stageNameName='" + stageName + '\'' +
                '}';


        //return stageName;
    }
}
