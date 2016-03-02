package sp.br.concretesolution.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

import sp.br.concretesolution.apis.GitHubAPI;
import sp.br.concretesolution.adapters.RecyclerPullRequestAdapter;
import sp.br.concretesolution.listeners.RecyclerClickListener;
import sp.br.concretesolution.models.PullRequest;
import sp.br.concretesolution.models.Repository;
import sp.br.concretesolution.R;

public class PullRequestActivity extends AppCompatActivity {

    private Menu menu;
    private String repositoryName;
    private String repositoryOwner;

    private GitHubAPI gitHubAPI;
    private RecyclerPullRequestAdapter recyclerViewAdapter;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pull_request);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* get Repository name and owner from Bundle of things */
        Bundle extra = getIntent().getExtras();
        repositoryName = extra.getString("repositoryName");
        repositoryOwner = extra.getString("repositoryOwner");

        adjust();

        /* Create Adapter for RecyclerView and LinearLayoutManager basic elements */
        recyclerViewAdapter = new RecyclerPullRequestAdapter(this, new ArrayList<PullRequest>());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_repository);

        /* Set layout manager, adapter and others things to work */
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        /* Instanced GitHub API */
        gitHubAPI = new GitHubAPI() {
            @Override
            public void pullRequestAPIResult(List<PullRequest> result, String message) {

                if (result != null) {

                    if (progressDialog != null) {

                        progressDialog.dismiss();
                        progressDialog = null;
                        menu.getItem(0).setVisible(false);
                    }

                    int countOpened = 0, countClosed = 0;

                    for (int i = 0; i < result.size(); ++i)
                        if (result.get(i).getStatus().contains("open"))
                            countOpened++;
                        else
                            countClosed++;

                    ((TextView) findViewById(R.id.pull_request_opened)).setText(String.valueOf(countOpened));
                    ((TextView) findViewById(R.id.pull_request_closed)).setText(String.valueOf(countClosed));

                    recyclerViewAdapter.setPullRequestList(result);
                    recyclerViewAdapter.notifyDataSetChanged();
                } else {

                    if (progressDialog != null)
                        progressDialog.dismiss();

                    Toast.makeText(PullRequestActivity.this, getResources().getString(R.string.toast_title), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void repositoryAPIResult(Repository repository, String message) {

            }
        };
        gitHubAPI.startAPI();

        /* Add ScrollListener and TouchItem */
        recyclerView.addOnItemTouchListener(new RecyclerClickListener(this, new RecyclerClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(recyclerViewAdapter.getPullRequestList().get(position).getHtmlUrl()));
                startActivity(i);
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

        gitHubAPI.getPullRequests(repositoryOwner, repositoryName);
    }

    private void adjust() {

        /* adjust Title activity to max 10 caracters with ellipse */
        String titleIntent = repositoryName.replaceAll("[^a-zA-Z0-9]+", " ");
        titleIntent = titleIntent.length() > 15 ? titleIntent.substring(0, 15) + "..." : titleIntent.substring(0, titleIntent.length());
        setTitle(WordUtils.capitalize(titleIntent.toLowerCase()));

        try {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {

            System.out.println(e.getStackTrace());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pullrequest_menu, menu);

        this.menu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home: {

                finish();
            }

            case R.id.pull_request_menu_refresh: {

                if (progressDialog != null)
                    executeProgressDialog();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        return super.dispatchTouchEvent(ev);
    }
}
