package uqac.dim.audium.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.model.entity.Album;

public class GridViewAlbumAdapter extends ArrayAdapter<Album> {
    private List<Album> albumsList;
    private Context context;
    private DatabaseReference database;


    public GridViewAlbumAdapter(List<Album> albums, Context context) {
        super(context, R.layout.grid_view_item,albums);
        this.context = context;
        albumsList = new ArrayList<>();
        this.albumsList = albums;;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View row = inflater.inflate(R.layout.grid_view_album_item,parent,false);
        TextView title = row.findViewById(R.id.gv_album_name);
        TextView artistName = row.findViewById(R.id.gv_artist_name);
        ImageView imageView = row.findViewById(R.id.gv_album_image);
        database = FirebaseDatabase.getInstance().getReference();
        database.child("artists").child(String.valueOf(albumsList.get(position).getArtistId())).child("stageName").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String stageName = dataSnapshot.getValue(String.class);
                    artistName.setText(stageName);
                }
            }
        });
        title.setText(albumsList.get(position).getTitle());
        Picasso.with(getContext()).load(albumsList.get(position).getImagePath()).error(R.drawable.ic_notes).into(imageView);


        //imageView.
        return row;
    }
}
