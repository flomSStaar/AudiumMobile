package uqac.dim.audium.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.model.entity.Album;
import uqac.dim.audium.model.entity.Artist;

public class GridViewAlbumAdapter extends ArrayAdapter<Album> {
    private final List<Album> albums;
    private final Context context;

    public GridViewAlbumAdapter(List<Album> albums, Context context) {
        super(context, R.layout.grid_view_album_item, albums);
        this.context = context;
        this.albums = albums;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(R.layout.grid_view_album_item, parent, false);
        }

        Album album = albums.get(position);

        TextView tvTitle = row.findViewById(R.id.gv_album_name);
        TextView tvArtistName = row.findViewById(R.id.gv_artist_name);
        ImageView ivAlbum = row.findViewById(R.id.gv_album_image);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("artists")
                .child(String.valueOf(album.getArtistId())).get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        Artist artist = dataSnapshot.getValue(Artist.class);
                        if (artist != null) {
                            tvArtistName.setText(artist.getPrintableName());
                        } else {
                            tvArtistName.setText("");
                        }
                    }
                });
        tvTitle.setText(albums.get(position).getTitle());
        Picasso.with(context).load(album.getImageUrl()).placeholder(R.drawable.ic_notes).error(R.drawable.ic_notes).into(ivAlbum);

        return row;
    }
}
