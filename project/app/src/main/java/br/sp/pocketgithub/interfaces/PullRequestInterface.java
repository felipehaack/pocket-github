package br.sp.pocketgithub.interfaces;

import java.util.List;

import br.sp.pocketgithub.models.PullRequest;

public interface PullRequestInterface {

    void pullRequestAPIResult(List<PullRequest> pullRequests, String message);
}
