package br.sp.pocketgithub.apis;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import br.sp.pocketgithub.interfaces.GitHubPullRequestInterface;
import br.sp.pocketgithub.interfaces.GitHubRepositoryInterface;
import br.sp.pocketgithub.interfaces.PullRequestInterface;
import br.sp.pocketgithub.interfaces.RepositoryInterface;
import br.sp.pocketgithub.models.PullRequest;
import br.sp.pocketgithub.models.Repository;

public abstract class GithubAPI implements RepositoryInterface, PullRequestInterface {

    final String BASE_URL = "https://api.github.com";

    private Retrofit retrofit;
    private OkHttpClient okHttpClient = new OkHttpClient();

    public GithubAPI() {

    }

    public void startAPI() {

        okHttpClient.newBuilder().readTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).connectTimeout(10, TimeUnit.SECONDS).build();

        retrofit = new Retrofit.Builder().
                baseUrl(BASE_URL).
                addConverterFactory(GsonConverterFactory.create()).
                client(okHttpClient).
                build();
    }

    public void getRepositories(final String currentLanguage, final int currentPage, final String currentSort) {

        GitHubRepositoryInterface gitHubInterface = retrofit.create(GitHubRepositoryInterface.class);

        Call<Repository> response = gitHubInterface.repositories("language:" + currentLanguage, currentPage, currentSort);

        response.enqueue(new Callback<Repository>() {
            @Override
            public void onResponse(Call<Repository> call, Response<Repository> response) {

                if (response.isSuccess()) {

                    repositoryAPIResult(response.body(), response.message());
                } else {

                    repositoryAPIResult(null, response.message());
                }
            }

            @Override
            public void onFailure(Call<Repository> call, Throwable t) {

                repositoryAPIResult(null, t.getMessage());
            }
        });
    }

    public void getPullRequests(final String owner, final String repository) {

        GitHubPullRequestInterface gitHubInterface = retrofit.create(GitHubPullRequestInterface.class);

        Call<List<PullRequest>> response = gitHubInterface.repositories(owner, repository);

        response.enqueue(new Callback<List<PullRequest>>() {
            @Override
            public void onResponse(Call<List<PullRequest>> call, Response<List<PullRequest>> response) {

                if (response.isSuccess()) {

                    pullRequestAPIResult(response.body(), response.message());
                } else {

                    pullRequestAPIResult(null, response.message());
                }
            }

            @Override
            public void onFailure(Call<List<PullRequest>> call, Throwable t) {

                pullRequestAPIResult(null, t.getMessage());
            }
        });
    }
}