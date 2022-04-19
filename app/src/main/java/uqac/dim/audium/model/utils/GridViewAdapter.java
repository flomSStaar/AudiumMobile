package uqac.dim.audium.model.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.model.entity.Playlist;

public class GridViewAdapter extends ArrayAdapter<Playlist> {
    private List<Playlist> playlistsList;
    private Context context;

    public GridViewAdapter(List<Playlist> playlists, Context context){
        super(context, R.layout.grid_view_item,playlists);
        this.context = context;
        playlistsList = new ArrayList<>();
        this.playlistsList = playlists;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View row = inflater.inflate(R.layout.grid_view_item,parent,false);
        TextView tv = row.findViewById(R.id.gv_playlist_name);
        tv.setText(playlistsList.get(position).getTitle());

        return row;
    }
}