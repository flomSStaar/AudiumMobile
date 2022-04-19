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
import uqac.dim.audium.model.entity.Album;
import uqac.dim.audium.model.entity.Track;

public class ListViewAlbumAdapter extends ArrayAdapter<Album> {
    private List<Album> albumList;
    private Context context;
    private Album album;
    private DatabaseReference database;

    public ListViewAlbumAdapter(List<Album> albums, Context context){
        super(context, R.layout.list_view_track_item,albums);
        this.context = context;
        albumList = new ArrayList<>();
        this.albumList = albums;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View row = inflater.inflate(R.layout.list_view_track_item,parent,false);
        TextView tv = row.findViewById(R.id.track_infos);
        database = FirebaseDatabase.getInstance().getReference();
        database.child("albums").child(String.valueOf(albumList.get(position).getId())).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    album = dataSnapshot.getValue(Album.class);
                    tv.setText(album.getTitle());
                }
            }
        });

        return row;
    }
}