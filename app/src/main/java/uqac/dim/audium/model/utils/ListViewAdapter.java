package uqac.dim.audium.model.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.activity.admin.AddAlbumActivity;
import uqac.dim.audium.model.entity.Track;

public class ListViewAdapter extends ArrayAdapter<Track> {

    private List<Track> titles;
    private Context context;

    public ListViewAdapter(List<Track> names, Context context) {
        super(context, R.layout.track_list_item, names);
        this.context = context;
        titles = new ArrayList<>();
        this.titles = names;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(R.layout.track_list_item, parent, false);
        TextView name = row.findViewById(R.id.track_name);
        name.setText(titles.get(position).getName());

        CheckBox checkBox = row.findViewById(R.id.track_checkbox);
        checkBox.setTag(position);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int position = (int) compoundButton.getTag();
                if (AddAlbumActivity.idTracksSelected.contains(titles.get(position))) {
                    AddAlbumActivity.idTracksSelected.remove(titles.get(position));
                } else {
                    AddAlbumActivity.idTracksSelected.add(titles.get(position).getId());
                }

            }
        });

        return row;
    }
}