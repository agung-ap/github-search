package id.developer.agungaprian.githubsearchapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UsersList {
    @SerializedName("items")
    @Expose
    private ArrayList<User> items = null;

    public ArrayList getItems() {
        return items;
    }

    public void setItems(ArrayList<User> items) {
        this.items = items;
    }
}
