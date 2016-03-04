package sp.br.concretesolution.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import sp.br.concretesolution.R;

public class RecyclerLanguageAdapter extends RecyclerView.Adapter<RecyclerLanguageAdapter.RecyclerViewHolder> {

    private String[] languages;

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView language;

        RecyclerViewHolder(View itemView) {

            super(itemView);

            language = (TextView) itemView.findViewById(R.id.language_name);
        }
    }

    public RecyclerLanguageAdapter(String[] languages) {

        this.languages = languages;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_language_main, viewGroup, false);

        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        holder.language.setText(languages[position]);
    }

    @Override
    public int getItemCount() {

        return languages.length;
    }
}
