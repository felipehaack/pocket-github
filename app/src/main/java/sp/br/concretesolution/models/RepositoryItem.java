package sp.br.concretesolution.models;

import com.google.gson.annotations.SerializedName;

public class RepositoryItem {

    private String name;
    private String description;

    @SerializedName("forks_count")
    private int forks;

    @SerializedName("stargazers_count")
    private int starsgazersCount;

    @SerializedName("full_name")
    private String fullName;

    private RepositoryOwner owner;

    public RepositoryItem(String name, String description, int forks, int starsgazersCount, String fullName, RepositoryOwner owner) {

        this.name = name;
        this.description = description;
        this.forks = forks;
        this.starsgazersCount = starsgazersCount;
        this.fullName = fullName;
        this.owner = owner;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public int getForks() {

        return forks;
    }

    public void setForks(int forks) {

        this.forks = forks;
    }

    public int getStarsgazersCount() {

        return starsgazersCount;
    }

    public void setStarsgazersCount(int starsgazersCount) {

        this.starsgazersCount = starsgazersCount;
    }

    public String getFullName() {

        return fullName;
    }

    public void setFullName(String fullName) {

        this.fullName = fullName;
    }

    public RepositoryOwner getOwner() {

        return owner;
    }

    public void setOwner(RepositoryOwner owner) {

        this.owner = owner;
    }
}
