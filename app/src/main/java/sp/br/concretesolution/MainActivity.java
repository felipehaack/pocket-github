package sp.br.concretesolution;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import sp.br.concretesolution.apis.GitHubAPI;
import sp.br.concretesolution.activities.PullRequestActivity;
import sp.br.concretesolution.adapters.RecyclerRepositoryAdapter;
import sp.br.concretesolution.listeners.ActivityListener;
import sp.br.concretesolution.listeners.RecyclerClickListener;
import sp.br.concretesolution.listeners.RecyclerScrollListener;
import sp.br.concretesolution.models.PullRequest;
import sp.br.concretesolution.models.Repository;
import sp.br.concretesolution.models.RepositoryItem;

public class MainActivity extends AppCompatActivity {

    public int currentPage = 1;

    private Menu menu;
    private GitHubAPI gitHubAPI;
    private ActivityListener activityListener;
    private RecyclerRepositoryAdapter recyclerViewAdapter;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        /* Create ToolBar for future customization */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* Create Adapter for RecyclerView and LinearLayoutManager basic elements */
        recyclerViewAdapter = new RecyclerRepositoryAdapter(new Repository(), this.getApplicationContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_repository);

        /* Set layout manager, adapter and others things to work */
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        /* Create a listener for activity to detect swipe down and search for more github repositories */
        activityListener = new ActivityListener(recyclerView, (ImageView) findViewById(R.id.see_more_spinner), (TextView) findViewById(R.id.see_more_text)) {

            @Override
            public void infiniteScrollDetected() {

                gitHubAPI.getRepositories(currentPage);
            }
        };

        gitHubAPI = new GitHubAPI() {

            @Override
            public void pullRequestAPIResult(List<PullRequest> pullRequests, String message) {

            }

            @Override
            public void repositoryAPIResult(Repository result, String message) {

                if (result != null) {

                    if (progressDialog != null) {

                        progressDialog.dismiss();
                        progressDialog = null;
                        menu.getItem(0).setVisible(false);
                    }

                    Repository currentRepository = recyclerViewAdapter.getRepository();
                    currentRepository.getRepositoryItems().addAll(result.getRepositoryItems());

                    recyclerViewAdapter.setRepository(currentRepository);
                    recyclerViewAdapter.setCountItems(currentRepository.getRepositoryItems().size());
                    recyclerViewAdapter.notifyDataSetChanged();

                    activityListener.clearSpinnerAnimation();

                    currentPage++;
                } else {

                    if (progressDialog != null)
                        progressDialog.dismiss();

                    Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_title), Toast.LENGTH_LONG).show();

                    activityListener.clearSpinnerAnimationWithFail();
                }
            }
        };
        gitHubAPI.startAPI();

        /* Add ScrollListener and TouchItem */
        recyclerView.addOnScrollListener(new RecyclerScrollListener(activityListener));

        recyclerView.addOnItemTouchListener(new RecyclerClickListener(this, new RecyclerClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                List<RepositoryItem> repositoryItems = recyclerViewAdapter.getRepository().getRepositoryItems();

                Intent intent = new Intent(MainActivity.this, PullRequestActivity.class);

                intent.putExtra("repositoryName", repositoryItems.get(position).getName());
                intent.putExtra("repositoryOwner", repositoryItems.get(position).getOwner().getLogin());

                startActivity(intent);
            }
        }));

        executeProgressDialog();
    }

    private void executeProgressDialog() {

        progressDialog = ProgressDialog.show(this,
                getResources().getString(R.string.progress_dialog_title),
                getResources().getString(R.string.progress_dialog_subtitle_subtitle),
                true);
        progressDialog.setIndeterminate(true);

        gitHubAPI.getRepositories(currentPage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        this.menu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.main_menu_refresh: {

                if (progressDialog != null)
                    executeProgressDialog();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        return activityListener.detectInfiniteScroll(ev) || super.dispatchTouchEvent(ev);
    }
}
