package uqac.dim.audium.model.entity;

import java.util.Set;

public class Album extends TrackContainer{

    public Album(String title, String description, Artist author) {
        super(title, description);
        this.author = author;
    }

    private Artist author;

}
