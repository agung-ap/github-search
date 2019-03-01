package id.developer.agungaprian.githubsearchapi;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import id.developer.agungaprian.githubsearchapi.adapter.UserAdapter;
import id.developer.agungaprian.githubsearchapi.api.RetrofitBuilder;
import id.developer.agungaprian.githubsearchapi.api.SearchApi;
import id.developer.agungaprian.githubsearchapi.model.UsersList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private LinearLayoutManager linearLayoutManager;
    private UserAdapter userAdapter;
    private RecyclerView usersList;
    private CoordinatorLayout coordinatorLayout;
    private TextView message;
    private Button refreshButton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindView();
        if (!isNetworkAvailable()){
            progressDialog.dismiss();
            usersList.setVisibility(View.GONE);
            message.setVisibility(View.VISIBLE);
            refreshButton.setVisibility(View.VISIBLE);
            message.setText("Tidak ada koneksi internet, coba cek internet kamu dan refresh");

        }
    }

    private void bindView(){
        getSupportActionBar().setTitle("github search");

        usersList = (RecyclerView)findViewById(R.id.users_list);
        message = (TextView)findViewById(R.id.message);
        refreshButton = (Button)findViewById(R.id.refresh_button);

        progressDialog = new ProgressDialog(this);
        userAdapter = new UserAdapter(this);
        linearLayoutManager = new LinearLayoutManager(this);

        usersList.setLayoutManager(linearLayoutManager);
        usersList.setAdapter(userAdapter);
    }

    private void prepareData(UsersList usersList) {
        userAdapter.addAll(usersList.getItems());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user, menu);
        searchMagangPost(menu);
        return true;
    }

    private void searchMagangPost(Menu menu) {
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search_magang));
        searchView.setQueryHint("Search by Username");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String s) {
                if (!isNetworkAvailable()){
                    progressDialog.dismiss();
                    usersList.setVisibility(View.GONE);
                    message.setVisibility(View.VISIBLE);
                    refreshButton.setVisibility(View.VISIBLE);
                    message.setText("Tidak ada koneksi internet, coba cek internet kamu dan refresh");

                    refreshButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SearchByUsername(s);
                        }
                    });

                }else {
                    usersList.setVisibility(View.VISIBLE);
                    message.setVisibility(View.GONE);
                    refreshButton.setVisibility(View.GONE);

                    SearchByUsername(s);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        MenuItem menuItem = menu.findItem(R.id.search_magang);
        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                return true;
            }
        });
    }

    private void SearchByUsername(String s) {
        //show progress bar
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        SearchApi searchApi = RetrofitBuilder.getApiService().create(SearchApi.class);
        Call<UsersList> callSearchRequest = searchApi.searchRequest(s);
        callSearchRequest.enqueue(new Callback<UsersList>() {
            @Override
            public void onResponse(Call<UsersList> call, Response<UsersList> response) {
                if (response.isSuccessful()){
                    progressDialog.dismiss();
                    usersList.setVisibility(View.VISIBLE);
                    message.setVisibility(View.GONE);
                    refreshButton.setVisibility(View.GONE);

                    UsersList userList = response.body();
                    prepareData(userList);
                }
            }

            @Override
            public void onFailure(Call<UsersList> call, Throwable t) {
                usersList.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
                refreshButton.setVisibility(View.VISIBLE);
                message.setText("Request not Sucessful :(");
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
