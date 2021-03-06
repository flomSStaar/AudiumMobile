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

public class ListViewAlbumAdapter extends ArrayAdapter<Album> {
    private final Context context;
    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    private final List<Album> albums;
    private boolean hasIndex = true;

    public ListViewAlbumAdapter(Context context, List<Album> albums) {
        super(context, R.layout.list_view_track_item, albums);
        this.context = context;
        this.albums = albums;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(R.layout.list_view_album_item, parent, false);
        }

        Album album = albums.get(position);

        TextView tvAlbumNumber = row.findViewById(R.id.tv_album_number);
        TextView tvAlbumName = row.findViewById(R.id.tv_album_name);
        TextView tvArtistName = row.findViewById(R.id.tv_artist_name);
        ImageView ivAlbum = row.findViewById(R.id.iv_album);

        if (hasIndex) {
            tvAlbumNumber.setText(String.valueOf(position));
        } else {
            tvAlbumNumber.setVisibility(View.GONE);
        }
        tvAlbumName.setText(album.getTitle());
        Picasso.with(context).load(album.getImageUrl()).placeholder(R.drawable.ic_notes).error(R.drawable.ic_notes).into(ivAlbum);

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

        return row;
    }

    public ListViewAlbumAdapter setHasIndex(boolean hasIndex) {
        this.hasIndex = hasIndex;
        return this;
    }
}