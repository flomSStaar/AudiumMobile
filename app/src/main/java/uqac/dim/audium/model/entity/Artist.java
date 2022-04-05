package uqac.dim.audium.model.entity;

import java.util.Set;
import java.util.TreeSet;

public class Artist extends Person {

    protected Set<Album> albums;
    protected String stageName;

    /**
     * Constructs a new artist having only artist name and age.
     *
     * @param stageName Stage name
     * @param age       Age of the artist
     */
    public Artist(String stageName, int age) {
        super("a", "a", age); //astuce pour pouvoir affecter artistName
        setStageName(stageName);
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
    public Artist(String firstName, String lastName, int age) {
        this(firstName, lastName, null, age);
    }

    /**
     * Constructs a new Artist with first name, last name, stage name and age of the artist.
     *
     * @param firstName First name of the artist
     * @param lastName  Last name of the artist
     * @param stageName Stage name of the artist
     * @param age       Age of the artist
     */
    public Artist(String firstName, String lastName, String stageName, int age) {
        this(firstName, lastName, stageName, age, new TreeSet<>());
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
    public Artist(String firstName, String lastName, String stageName, int age, Set<Album> albums) {
        super(firstName, lastName, age);
        setStageName(stageName);
        setAlbums(albums);
    }

    /**
     * Returns the albums of this artist.
     *
     * @return Albums of this artist
     */
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
    }
}
