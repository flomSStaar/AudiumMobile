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

import com.squareup.picasso.Picasso;

import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.model.entity.Artist;

public class ListViewArtistAdapter extends ArrayAdapter<Artist> {
    private final List<Artist> artists;
    private final Context context;

    public ListViewArtistAdapter(@NonNull List<Artist> artists, @NonNull Context context) {
        super(context, R.layout.list_view_artist_item, artists);
        this.context = context;
        this.artists = artists;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(R.layout.list_view_artist_item, parent, false);
        }

        Artist artist = artists.get(position);

        TextView tvArtistName = row.findViewById(R.id.tv_artist_name);
        ImageView ivArtist = row.findViewById(R.id.iv_artist);

        tvArtistName.setText(artist.getPrintableName());
        Picasso.with(context)
                .load(artist.getImageUrl())
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(ivArtist);

        return row;
    }
}