package uqac.dim.audium.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.adapter.ListViewAdapter;
import uqac.dim.audium.firebase.FirebasePlaylist;
import uqac.dim.audium.firebase.FirebaseUtils;
import uqac.dim.audium.model.entity.Track;
import uqac.dim.audium.model.entity.User;

public class AddPlaylistFragment extends Fragment {

    private final StorageReference storeRef = FirebaseStorage.getInstance().getReference();
    private String username;
    private View root;
    private User user;
    public static List<Long> idTracksSelected;
    private AbsListView.MultiChoiceModeListener modeListener;
    private ListView listView;
    private DatabaseReference database;
    private Button add;

    private Button chooseImg;
    private ImageView image;
    private Uri localFileImageUri;
    private ActivityResultLauncher<String> imageResultLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.activity_add_playlist, container, false);
        listView = ((ListView) root.findViewById(R.id.tracks));
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(modeListener);
        add = ((Button)root.findViewById(R.id.add_playlist_frag));
        add.setOnClickListener(this::addPlaylist);
        chooseImg = root.findViewById(R.id.btn_choose_playlist_image_file);
        chooseImg.setOnClickListener(this::addImage);
        image = root.findViewById(R.id.playlist_image_path);
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username = getArguments().getString("username");
        idTracksSelected = new ArrayList<>();
        ActionMode actionMode = null;



        imageResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), this::getImageResult);


        database = FirebaseDatabase.getInstance().getReference();
        database.child("users").child(username).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    user = dataSnapshot.getValue(User.class);
                }
            }
        });


        ArrayList<Track> tracks = new ArrayList<>();
         modeListener = new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                actionMode = actionMode;
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                actionMode = null;
                idTracksSelected.clear();
            }
        };

        database = FirebaseDatabase.getInstance().getReference();
        database.child("tracks").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                tracks.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Track t = snap.getValue(Track.class);
                    if (t != null) {
                        tracks.add(t);
                    }
                }
                if (tracks.size() != 0) {
                    listView.setAdapter(new ListViewAdapter(tracks, getContext(),"Playlist"));
                }
            }
        });

    }

    private void getImageResult(Uri uri) {
        if (uri != null) {
            localFileImageUri = uri;
            Picasso.with(getContext()).load(localFileImageUri).placeholder(R.drawable.ic_notes).error(R.drawable.ic_notes).into(image);
        }
    }


    private void addImage(View view) {
        imageResultLauncher.launch("image/*");
    }

    public void addPlaylist(View view) {
        if (idTracksSelected.size() != 0) {
            String title = ((EditText) root.findViewById(R.id.playlist_title)).getText().toString();
            String description = ((EditText) root.findViewById(R.id.playlist_description)).getText().toString();

            FirebaseDatabase db = FirebaseDatabase.getInstance();
            db.getReference("ids/lastPlaylistId").get()
                    .addOnSuccessListener(dataSnapshot -> {
                        Long lastPlaylistId = dataSnapshot.getValue(Long.class);
                        if (lastPlaylistId != null) {
                            StorageReference imageRef = storeRef.child(FirebaseUtils.PLAYLIST_IMAGE_FILE_PATH).child(String.valueOf(lastPlaylistId));
                            Long finalLastPlaylistId = lastPlaylistId;
                            imageRef.putFile(localFileImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                 @Override
                                 public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                     imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                         @Override
                                         public void onSuccess(Uri uri) {
                                             FirebasePlaylist playlist = new FirebasePlaylist(finalLastPlaylistId, username, title, description, idTracksSelected, uri.toString());
                                             db.getReference("playlists/").child(username).child(String.valueOf(finalLastPlaylistId)).setValue(playlist);

                                             /// Ajouter l'id de la playlist a la track - A FAIRE

                                             db.getReference("tracks").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                                 @Override
                                                 public void onSuccess(DataSnapshot dataSnapshot) {
                                                     for (DataSnapshot data: dataSnapshot.getChildren()) {
                                                         Track t = data.getValue(Track.class);
                                                         if(idTracksSelected.contains(t.getId())){
                                                             if(t.getPlaylistsId()==null) {
                                                                 List<Long> playlistsID = new ArrayList<>();
                                                                 playlistsID.add(finalLastPlaylistId);
                                                                 db.getReference("tracks/").child(String.valueOf(t.getId())).child("playlistsId").setValue(playlistsID);
                                                             }else{
                                                                 t.getPlaylistsId().add(finalLastPlaylistId);
                                                                 db.getReference("tracks/").child(String.valueOf(t.getId())).child("playlistsId").setValue(t.getPlaylistsId());
                                                             }
                                                         }
                                                     }
                                                 }
                                             });
                                         }
                                     });
                                 }
                            });

                            ////
                            /// Ajouter l'id de la playlist au user
                            if(user.getPlaylists()==null) {
                                List<Long> playlistsIds = new ArrayList<>();
                                playlistsIds.add(lastPlaylistId);
                                db.getReference("users/" + user.getUsername()).child("playlists").setValue(playlistsIds);
                            }
                            else {
                                user.getPlaylists().add(lastPlaylistId);
                                db.getReference("users/" + user.getUsername()).child("playlists").setValue(user.getPlaylists());
                            }

                            db.getReference("ids/lastPlaylistId").setValue(++lastPlaylistId);
                            PlaylistFragment homeFragment = new PlaylistFragment(getContext());
                            Bundle b = new Bundle();
                            b.putString("username", username);
                            homeFragment.setArguments(b);
                            FragmentManager manager = getParentFragmentManager();
                            manager.beginTransaction()
                                    .replace(R.id.fragment_container, homeFragment)
                                    .commit();
                        }
                    });
        } else {
            Toast.makeText(getContext(), "You need to select at least one track", Toast.LENGTH_SHORT).show();
        }
    }
}
