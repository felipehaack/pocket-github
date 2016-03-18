package br.sp.pocketgithub.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Repository {

    @SerializedName("total_count")
    private int totalCount;

    private String message;
    private List<RepositoryItem> items;

    public Repository(){

        items = new ArrayList<>();
    }

    public Repository(List<RepositoryItem> repositoryItems, String message, int totalCount) {

        this.items = repositoryItems;
        this.message = message;
        this.totalCount = totalCount;
    }

    public int getTotalCount() {

        return totalCount;
    }

    public void setTotalCount(int totalCount) {

        this.totalCount = totalCount;
    }

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {

        this.message = message;
    }

    public List<RepositoryItem> getRepositoryItems() {

        return items;
    }

    public void setRepositoryItems(List<RepositoryItem> repositoryItems) {

        this.items = repositoryItems;
    }
}
