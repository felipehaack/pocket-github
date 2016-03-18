package br.sp.pocketgithub.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.sp.pocketgithub.models.PullRequest;
import br.sp.pocketgithub.R;

public class RecyclerPullRequestAdapter extends RecyclerView.Adapter<RecyclerPullRequestAdapter.RecyclerViewHolder> {

    private Context context;
    private List<PullRequest> pullRequestList;

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        ImageView avatar;
        TextView name, description, user, createdAt;

        RecyclerViewHolder(View itemView) {

            super(itemView);

            avatar = (ImageView) itemView.findViewById(R.id.pull_request_avatar);
            name = (TextView) itemView.findViewById(R.id.pull_request_name);
            user = (TextView) itemView.findViewById(R.id.pull_request_username);
            description = (TextView) itemView.findViewById(R.id.pull_request_description);
            createdAt = (TextView) itemView.findViewById(R.id.pull_request_created_at);
        }
    }

    public RecyclerPullRequestAdapter(Context context, List<PullRequest> pullRequests) {

        this.context = context;
        this.pullRequestList = pullRequests;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_pull_request, viewGroup, false);

        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        PullRequest pullRequest = pullRequestList.get(position);

        holder.name.setText(pullRequest.getTitle().replaceAll("[^a-zA-Z0-9]+", " "));
        holder.description.setText(pullRequest.getPullRequestDescription());
        holder.user.setText(String.valueOf(pullRequest.getOwner().getLogin()));

        try {

            String[] dateParse = pullRequest.getCreatedAt().split("T")[0].split("-");
            holder.createdAt.setText(dateParse[2] + "/" + dateParse[1] + "/" + dateParse[0]);
        } catch (Exception e) {

            holder.createdAt.setText("");
        }

        Picasso.with(context).load(pullRequest.getOwner().getAvatarUrl()).into(holder.avatar);
    }

    @Override
    public int getItemCount() {

        return pullRequestList.size();
    }

    public void setPullRequestList(List<PullRequest> pullRequestList) {

        this.pullRequestList = pullRequestList;
    }

    public List<PullRequest> getPullRequestList() {

        return pullRequestList;
    }
}
