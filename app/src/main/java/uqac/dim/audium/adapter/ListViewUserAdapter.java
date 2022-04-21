package uqac.dim.audium.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import uqac.dim.audium.R;
import uqac.dim.audium.model.entity.User;

public class ListViewUserAdapter extends ArrayAdapter<User> {
    private final List<User> userList;
    private final Context context;

    public ListViewUserAdapter(List<User> users, Context context) {
        super(context, R.layout.list_view_user_item, users);
        this.context = context;
        this.userList = users;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(R.layout.list_view_user_item, parent, false);
        }

        User user = userList.get(position);

        TextView tvUserInfos = row.findViewById(R.id.tv_user_infos);
        tvUserInfos.setText(user.getUsername() + " - " + user.getLastName() + " " + user.getFirstName());

        return row;
    }
}