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
import uqac.dim.audium.model.entity.Artist;
import uqac.dim.audium.model.entity.User;

public class ListViewUserAdapter extends ArrayAdapter<User> {
    private List<User> userList;
    private Context context;
    private User user;
    private DatabaseReference database;

    public ListViewUserAdapter(List<User> users, Context context){
        super(context, R.layout.list_view_track_item,users);
        this.context = context;
        userList = new ArrayList<>();
        this.userList = users;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View row = inflater.inflate(R.layout.list_view_track_item,parent,false);
        TextView tv = row.findViewById(R.id.track_infos);
        database = FirebaseDatabase.getInstance().getReference();
        database.child("users").child(String.valueOf(userList.get(position).getUsername())).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    user = dataSnapshot.getValue(User.class);
                    tv.setText(user.getUsername() + " - " + user.getFirstName() + " " + user.getLastName());
                }
            }
        });
        return row;
    }
}