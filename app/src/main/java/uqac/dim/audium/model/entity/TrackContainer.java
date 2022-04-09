package uqac.dim.audium.model.entity;

import java.util.ArrayList;
import java.util.List;

public abstract class TrackContainer implements Comparable<TrackContainer> {

    protected String title;
    protected String description;
    protected List<Long> tracksId;

    protected TrackContainer() {
    }

    /**
     * Constructs a new TrackContainer with a title.
     *
     * @param title Title of this track container
     */
    public TrackContainer(String title) {
        this(title, null);
    }

    /**
     * Constructs a new TrackContainer with a title and a description.
     *
     * @param title       Title of this track container
     * @param description Description of this track container
     */
    public TrackContainer(String title, String description) {
        setTitle(title);
        setDescription(description);
        setTracksId(new ArrayList<>());
    }

    /**
     * Returns the title of this container.
     *
     * @return Title of this container
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of this container.
     *
     * @param title The new title of this container
     */
    public void setTitle(String title) {
        if (title != null) {
            this.title = title;
        } else {
            throw new IllegalArgumentException("title cannot be null");
        }
    }

    /**
     * Returns the description of this container.
     *
     * @return Description of this container
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this container.
     *
     * @param description The new description of this container
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the tracks of this container.
     *
     * @return Tracks of this container
     */
    public List<Long> getTracksId() {
        return tracksId;
    }

    /**
     * Sets the list of track of this container.
     *
     * @param tracks The new list of tracks of this container
     */
    private void setTracksId(List<Long> tracks) {
        if (tracks != null) {
            this.tracksId = tracks;
        } else {
            throw new IllegalArgumentException("tracks cannot be null");
        }
    }
}
