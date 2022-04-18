package uqac.dim.audium.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import uqac.dim.audium.R;
import uqac.dim.audium.activity.admin.AddTrackActivity;
import uqac.dim.audium.activity.admin.ArtistListActivity;
import uqac.dim.audium.activity.admin.TrackListActivity;
import uqac.dim.audium.activity.admin.TrackPageActivity;
import uqac.dim.audium.activity.admin.UserListActivity;
import uqac.dim.audium.model.entity.Track;

public class AdminFragment extends Fragment {
    private Button btnArtists;
    private Button btnTracks;
    private Button btnUsers;
    private Button btnAddTrack;
    private String username;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.admin_fragment, container, false);

        username = getArguments().getString("username");
        btnArtists = root.findViewById(R.id.btn_manage_artists);
        btnUsers = root.findViewById(R.id.btn_manage_users);
        btnTracks = root.findViewById(R.id.btn_manage_tracks);
        btnAddTrack = root.findViewById(R.id.btn_add_track);

        btnArtists.setOnClickListener(this::viewArtists);
        btnUsers.setOnClickListener(this::viewUsers);
        btnTracks.setOnClickListener(this::viewTracks);
        btnAddTrack.setOnClickListener(this::addTrack);

        return root;
    }

    private void viewArtists(View view) {
        Intent intent = new Intent(getContext(), ArtistListActivity.class);
        intent.putExtra("username",username);
        startActivity(intent);
    }

    private void viewUsers(View view) {
        startActivity(new Intent(getContext(), UserListActivity.class));
    }

    private void viewTracks(View view) {
        Intent intent = new Intent(getContext(), TrackListActivity.class);
        intent.putExtra("username",username);
        startActivity(intent);
    }

    private void addTrack(View view) {
        startActivity(new Intent(getContext(), AddTrackActivity.class));
    }
}
