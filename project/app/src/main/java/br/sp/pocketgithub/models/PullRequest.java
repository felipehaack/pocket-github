package br.sp.pocketgithub.models;

import com.google.gson.annotations.SerializedName;

public class PullRequest {

    private String title;

    @SerializedName("html_url")
    private String htmlUrl;

    @SerializedName("body")
    private String pullRequestDescription;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("state")
    private String status;

    @SerializedName("user")
    private RepositoryOwner owner;

    public PullRequest(String title, String htmlUrl, String pullRequestDescription, String createdAt, String status, RepositoryOwner owner) {

        this.title = title;
        this.htmlUrl = htmlUrl;
        this.pullRequestDescription = pullRequestDescription;
        this.createdAt = createdAt;
        this.status = status;
        this.owner = owner;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public String getHtmlUrl() {

        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {

        this.htmlUrl = htmlUrl;
    }

    public String getPullRequestDescription() {

        return pullRequestDescription;
    }

    public void setPullRequestDescription(String pullRequestDescription) {

        this.pullRequestDescription = pullRequestDescription;
    }

    public String getCreatedAt() {

        return createdAt;
    }

    public void setCreatedAt(String createdAt) {

        this.createdAt = createdAt;
    }

    public String getStatus() {

        return status;
    }

    public void setStatus(String status) {

        this.status = status;
    }

    public RepositoryOwner getOwner() {

        return owner;
    }

    public void setOwner(RepositoryOwner owner) {

        this.owner = owner;
    }
}