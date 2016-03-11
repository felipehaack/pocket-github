package sp.br.githubpocket.interfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import sp.br.githubpocket.models.Repository;

public interface GitHubRepositoryInterface {

    @GET("/search/repositories?")
    Call<Repository> repositories(
            @Query("q") String language,
            @Query("page") int page,
            @Query("sort") String sort
    );
}
