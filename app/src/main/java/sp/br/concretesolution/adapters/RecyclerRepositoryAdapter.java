package sp.br.concretesolution.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.WordUtils;

import java.util.List;

import sp.br.concretesolution.models.Repository;
import sp.br.concretesolution.models.RepositoryItem;
import sp.br.concretesolution.R;

public class RecyclerRepositoryAdapter extends RecyclerView.Adapter<RecyclerRepositoryAdapter.RecyclerViewHolder> {

    private int countItems;
    private Context context;
    private Repository repository;

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        ImageView avatar;
        TextView name, description, forks, stars, user, fullname;

        RecyclerViewHolder(View itemView) {

            super(itemView);

            avatar = (ImageView) itemView.findViewById(R.id.repository_avatar);

            name = (TextView) itemView.findViewById(R.id.repository_name);
            forks = (TextView) itemView.findViewById(R.id.repository_forks);
            stars = (TextView) itemView.findViewById(R.id.repository_stars);
            user = (TextView) itemView.findViewById(R.id.repository_username);
            fullname = (TextView) itemView.findViewById(R.id.repository_fullname);
            description = (TextView) itemView.findViewById(R.id.repository_description);
        }
    }

    public RecyclerRepositoryAdapter(Repository repository, Context context) {

        this.repository = repository;
        this.context = context;
        this.countItems = repository.getRepositoryItems().size();
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_repository_main, viewGroup, false);

        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        RepositoryItem repositoryItem = repository.getRepositoryItems().get(position);

        holder.name.setText(WordUtils.capitalize(repositoryItem.getName().replaceAll("[^a-zA-Z0-9]+", " ").toLowerCase()));
        holder.description.setText(repositoryItem.getDescription());
        holder.forks.setText(String.valueOf(repositoryItem.getForks()));
        holder.stars.setText(String.valueOf(repositoryItem.getStarsgazersCount()));
        holder.user.setText(String.valueOf(repositoryItem.getOwner().getLogin()));
        holder.fullname.setText(String.valueOf(repositoryItem.getFullName()));

        Picasso.with(context).load(repositoryItem.getOwner().getAvatarUrl()).into(holder.avatar);
    }

    @Override
    public int getItemCount() {

        return countItems;
    }

    public void setCountItems(int countItems) {

        this.countItems = countItems;
    }

    public Repository getRepository() {

        return repository;
    }

    public void setRepository(Repository repository) {

        this.repository = repository;
    }

    public void clearRepository(){

        List<RepositoryItem> repositoryItems = this.repository.getRepositoryItems();
        repositoryItems.clear();

        this.countItems = 0;
        this.repository.setRepositoryItems(repositoryItems);

        notifyDataSetChanged();
    }
}
