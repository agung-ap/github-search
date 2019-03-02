package id.developer.agungaprian.githubsearchapi.api;

import id.developer.agungaprian.githubsearchapi.model.UsersList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchApi {
    @GET("search/users")
    Call<UsersList> searchRequest(@Query("q") String Username, @Query("page") int page);
}
