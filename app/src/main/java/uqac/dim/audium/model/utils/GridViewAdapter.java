package uqac.dim.audium.model.utils;

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

import com.squareup.picasso.Picasso;

import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.model.entity.Playlist;

public class GridViewAdapter extends ArrayAdapter<Playlist> {
    private final List<Playlist> playlistsList;
    private final Context context;

    public GridViewAdapter(List<Playlist> playlists, Context context) {
        super(context, R.layout.grid_view_item, playlists);
        this.context = context;
        this.playlistsList = playlists;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(R.layout.grid_view_item, parent, false);
        TextView tv = row.findViewById(R.id.gv_playlist_name);
        ImageView imageView = row.findViewById(R.id.playlist_image);
        tv.setText(playlistsList.get(position).getTitle());
        Picasso.with(getContext()).load(playlistsList.get(position).getImageUrl()).error(R.drawable.ic_notes).into(imageView);
        return row;
    }
}
