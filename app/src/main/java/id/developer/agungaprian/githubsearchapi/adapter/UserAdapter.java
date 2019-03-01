package id.developer.agungaprian.githubsearchapi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import id.developer.agungaprian.githubsearchapi.R;
import id.developer.agungaprian.githubsearchapi.model.User;

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<User> userList;
    private Context context;

    public UserAdapter(Context context) {
        this.context = context;
    }

    public void addAll(ArrayList<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_users, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UserViewHolder) {
            UserViewHolder userViewHolder = (UserViewHolder) holder;

            Picasso.get().load(userList.get(position).getAvatarUrl())
                    .error(R.drawable.ic_launcher_background)
                    .into(userViewHolder.userImage);
            userViewHolder.userName.setText(userList.get(position).getLogin());
            userViewHolder.userId.setText("user id " + userList.get(position).getId());
        }
    }

    @Override
    public int getItemCount() {
        return userList == null ? 0 : userList.size();
    }

    private class UserViewHolder extends RecyclerView.ViewHolder {
        private ImageView userImage;
        private TextView userName;
        private TextView userId;

        public UserViewHolder(View view) {
            super(view);

            userImage = (ImageView)view.findViewById(R.id.user_image);
            userName = (TextView)view.findViewById(R.id.user_name);
            userId = (TextView)view.findViewById(R.id.user_id);
        }
    }
}
