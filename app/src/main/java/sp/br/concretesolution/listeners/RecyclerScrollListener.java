package sp.br.concretesolution.listeners;

import android.support.v7.widget.RecyclerView;

public class RecyclerScrollListener extends RecyclerView.OnScrollListener {

    private ActivityListener activityListener;

    public RecyclerScrollListener(ActivityListener activityListener) {

        this.activityListener = activityListener;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

        super.onScrolled(recyclerView, dx, dy);

        if(!recyclerView.canScrollVertically(1)){

            activityListener.setStartDetect(true);
        }
    }
}
