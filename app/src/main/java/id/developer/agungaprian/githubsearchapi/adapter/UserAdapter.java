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
    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private ArrayList<User> userList;
    private Context context;

    private boolean isLoadingAdded = false;

    public UserAdapter(Context context) {
        this.context = context;
        userList = new ArrayList<>();
    }

    public ArrayList<User> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<User> userList) {
        this.userList = userList;
    }
    /*
    public void addAll(ArrayList<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }*/

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case ITEM:
                View v1 = inflater.inflate(R.layout.list_item_users, parent, false);
                viewHolder = new UserViewHolder(v1);
                return viewHolder;

            case LOADING:
                View v2 = inflater.inflate(R.layout.loading, parent, false);
                viewHolder = new Loading(v2);
                break;
        }
        return viewHolder;
    }
    /*
    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.list_item_users, parent, false);
        viewHolder = new MovieVH(v1);
        return viewHolder;
    }*/


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

    @Override
    public int getItemViewType(int position) {
        return (position == userList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    //helper

    public void add(User r) {
        userList.add(r);
        notifyItemInserted(userList.size() - 1);
    }

    public void addAll(ArrayList<User> moveResults) {
        for (User result : moveResults) {
            add(result);
        }
    }

    public void remove(User r) {
        int position = userList.indexOf(r);
        if (position > -1) {
            userList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new User());
    }

    public void removeLoadingFooter() {

        isLoadingAdded = false;

        int position = userList.size() - 1;
        User user = getItem(position);

        if (user != null) {
            userList.remove(position);
            notifyItemRemoved(position);
        }
    }


    public User getItem(int position) {
        return userList.get(position);
    }

    //content view holder

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

    private class Loading extends RecyclerView.ViewHolder {

        public Loading(View view) {
            super(view);

        }
    }

}
