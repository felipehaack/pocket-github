package br.sp.pocketgithub.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import br.sp.pocketgithub.models.PullRequest;

public interface GitHubPullRequestInterface {

    @GET("/repos/{owner}/{repository}/pulls")
    Call<List<PullRequest>> repositories(
            @Path("owner") String owner,
            @Path("repository") String repository
    );
}
