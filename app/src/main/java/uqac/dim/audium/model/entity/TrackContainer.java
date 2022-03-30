package uqac.dim.audium.model.entity;

import java.util.Set;
import java.util.TreeSet;

abstract public class TrackContainer {

    private Set<Track> trackList;
    private String title;

    public TrackContainer(String title, String description) {
        trackList = new TreeSet<>();
        this.title = title;
        this.description = description;
    }

    private String description;

    public Set<Track> getTrackList() {
        return trackList;
    }

    public void setTrackList(Set<Track> trackList) {
        this.trackList = trackList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private void removeTrack(Track t){
        trackList.remove(t);
    }

    private void addTrack(Track t){
        trackList.add(t);
    }
}
