package sp.br.concretesolution.interfaces;

import java.util.List;

import sp.br.concretesolution.models.PullRequest;

public interface PullRequestInterface {

    void pullRequestAPIResult(List<PullRequest> pullRequests, String message);
}
