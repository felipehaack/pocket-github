package sp.br.githubpocket.interfaces;

import java.util.List;

import sp.br.githubpocket.models.PullRequest;

public interface PullRequestInterface {

    void pullRequestAPIResult(List<PullRequest> pullRequests, String message);
}
