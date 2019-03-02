package id.developer.agungaprian.githubsearchapi;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
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
    private TextView message;
    private Button refreshButton;
    private ProgressDialog progressDialog;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    private int currentPage = PAGE_START;
    // batasi page yang bisa di load
    private int TOTAL_PAGES = currentPage;
    private String query;

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

        usersList.setVisibility(View.GONE);
        message.setVisibility(View.VISIBLE);
        message.setText("Silahkan ketuk icon pencarian di pojok kanan atas dan cari berdasarkan username");

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
        usersList.addOnScrollListener(new PaginationScroll(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                // network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage();
                    }
                }, 1000);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        //loadFirstPage();
    }

    private void loadFirstPage() {
        //show progress bar
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        SearchApi searchApi = RetrofitBuilder.getApiService().create(SearchApi.class);
        Call<UsersList> callSearchRequest = searchApi.searchRequest(query, currentPage);
        callSearchRequest.enqueue(new Callback<UsersList>() {
            @Override
            public void onResponse(Call<UsersList> call, Response<UsersList> response) {
                if (response.isSuccessful()){
                    progressDialog.dismiss();

                    UsersList usersLists = response.body();
                    prepareData(usersLists);

                    if (currentPage <= TOTAL_PAGES) {
                        userAdapter.addLoadingFooter();
                    }
                    else {
                        isLastPage = true;
                        currentPage++;
                    }
                }else {
                    progressDialog.dismiss();
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

    private void loadNextPage(){

        SearchApi searchApi = RetrofitBuilder.getApiService().create(SearchApi.class);
        Call<UsersList> callSearchRequest = searchApi.searchRequest(query, currentPage);
        callSearchRequest.enqueue(new Callback<UsersList>() {
            @Override
            public void onResponse(Call<UsersList> call, Response<UsersList> response) {
                if (response.isSuccessful()) {

                    userAdapter.removeLoadingFooter();
                    isLoading = false;

                    UsersList usersLists = response.body();
                    prepareData(usersLists);

                    if (currentPage != TOTAL_PAGES) {
                        userAdapter.addLoadingFooter();
                    }else {
                        isLastPage = true;
                        currentPage++;
                    }

                }else {

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

    private void prepareData(UsersList usersList) {
        userAdapter.addAll(usersList.getItems());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user, menu);
        searchGithubUsername(menu);
        return true;
    }

    private void searchGithubUsername(Menu menu) {
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
                            query = s;
                            userAdapter.clear();
                            currentPage = PAGE_START;
                            loadFirstPage();
                        }
                    });

                }else {
                    usersList.setVisibility(View.VISIBLE);
                    message.setVisibility(View.GONE);
                    refreshButton.setVisibility(View.GONE);

                    query = s;
                    userAdapter.clear();
                    currentPage = PAGE_START;
                    loadFirstPage();
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
                userAdapter.clear();
                currentPage = PAGE_START;

                usersList.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
                message.setText("Silahkan ketuk icon pencarian di pojok kanan atas dan cari berdasarkan username");

                return true;
            }
        });
    }
    //cek koneksi internet
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
