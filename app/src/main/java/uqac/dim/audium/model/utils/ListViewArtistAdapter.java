package uqac.dim.audium.model.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.model.entity.Artist;

public class ListViewArtistAdapter extends ArrayAdapter<Artist> {
    private List<Artist> artistList;
    private Context context;
    private Artist artist;
    private DatabaseReference database;

    public ListViewArtistAdapter(List<Artist> artists, Context context) {
        super(context, R.layout.list_view_track_item, artists);
        this.context = context;
        artistList = new ArrayList<>();
        this.artistList = artists;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(R.layout.list_view_track_item, parent, false);
        TextView tv = row.findViewById(R.id.tv_track_name);
        database = FirebaseDatabase.getInstance().getReference();
        database.child("artists").child(String.valueOf(artistList.get(position).getId())).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    artist = dataSnapshot.getValue(Artist.class);
                    tv.setText(artist.getStageName() + " - " + artist.getFirstName() + " " + artist.getLastName());
                }
            }
        });
        return row;
    }
}