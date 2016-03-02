package sp.br.concretesolution.interfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import sp.br.concretesolution.models.Repository;

public interface GitHubRepositoryInterface {

    @GET("/search/repositories?q=language:Java&sort=stars")
    Call<Repository> repositories(
            @Query("page") int page
    );
}
