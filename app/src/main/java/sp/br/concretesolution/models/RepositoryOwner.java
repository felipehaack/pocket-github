package sp.br.concretesolution.models;

import com.google.gson.annotations.SerializedName;

public class RepositoryOwner {

    @SerializedName("avatar_url")
    private String avatarUrl;

    private String login;

    public RepositoryOwner(String avatarUrl, String login) {

        this.avatarUrl = avatarUrl;
        this.login = login;
    }

    public String getAvatarUrl() {

        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {

        this.avatarUrl = avatarUrl;
    }

    public String getLogin() {

        return login;
    }

    public void setLogin(String login) {

        this.login = login;
    }
}
