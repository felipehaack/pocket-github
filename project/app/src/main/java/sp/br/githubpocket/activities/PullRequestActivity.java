package sp.br.githubpocket.activities;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
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

import sp.br.githubpocket.apis.GithubAPI;
import sp.br.githubpocket.adapters.RecyclerPullRequestAdapter;
import sp.br.githubpocket.listeners.RecyclerClickListener;
import sp.br.githubpocket.models.PullRequest;
import sp.br.githubpocket.models.Repository;
import sp.br.githubpocket.R;

public class PullRequestActivity extends AppCompatActivity {

    private Menu menu;
    private String repositoryName;
    private String repositoryOwner;

    private GithubAPI githubAPI;
    private RecyclerPullRequestAdapter recyclerViewAdapter;

    private ProgressDialog progressDialog;

    private TextView actionBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pull_request);

        /* get Repository name and owner from Bundle of things */
        Bundle extra = getIntent().getExtras();
        repositoryName = extra.getString("repositoryName");
        repositoryOwner = extra.getString("repositoryOwner");

        actionBarTitle = (TextView) findViewById(R.id.activity_pull_request_title);
        actionBarTitle.setText(WordUtils.capitalize(repositoryName.replaceAll("[^a-zA-Z0-9]+", " ").toLowerCase()));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Adjust things
        adjust();
        animateChangeActionBarTitleColor(Color.parseColor("#3977b0"), Color.parseColor("#FFFFFF"), 500, 1500);

        /* Create Adapter for RecyclerView and LinearLayoutManager basic elements */
        recyclerViewAdapter = new RecyclerPullRequestAdapter(this, new ArrayList<PullRequest>());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_repository);

        /* Set layout manager, adapter and others things to work */
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        /* Instanced GitHub API */
        githubAPI = new GithubAPI() {
            @Override
            public void pullRequestAPIResult(List<PullRequest> result, String message) {

                if (result != null) {

                    if (progressDialog != null) {

                        progressDialog.dismiss();
                        progressDialog = null;
                        menu.getItem(0).setVisible(false);
                    }

                    int countOpened = 0;

                    for (int i = 0; i < result.size(); ++i)
                        if (result.get(i).getStatus().contains("open"))
                            countOpened++;

                    ((TextView) findViewById(R.id.pull_request_opened)).setText(String.valueOf(countOpened));

                    recyclerViewAdapter.setPullRequestList(result);
                    recyclerViewAdapter.notifyDataSetChanged();
                } else {

                    if (progressDialog != null)
                        progressDialog.dismiss();

                    Toast.makeText(PullRequestActivity.this, getResources().getString(R.string.main_pullrequest_activity_toast_title), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void repositoryAPIResult(Repository repository, String message) {

            }
        };
        githubAPI.startAPI();

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
                getResources().getString(R.string.main_activity_progress_dialog_title),
                getResources().getString(R.string.main_activity_progress_dialog_subtitle),
                true);
        progressDialog.setIndeterminate(true);

        githubAPI.getPullRequests(repositoryOwner, repositoryName);
    }

    private void animateChangeActionBarTitleColor(int colorStart, int colorEnd, int delay, int duration){

        ArgbEvaluator evaluator = new ArgbEvaluator();
        ValueAnimator animator = new ValueAnimator();

        animator.setIntValues(colorStart, colorEnd);
        animator.setStartDelay(delay);
        animator.setDuration(duration);
        animator.setEvaluator(evaluator);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                actionBarTitle.setTextColor((Integer) animation.getAnimatedValue());
            }
        });

        animator.start();
    }

    private void adjust() {

        try {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {

            e.printStackTrace();
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

                actionBarTitle.setTextColor(Color.parseColor("#3977b0"));

                supportFinishAfterTransition();
            }

            case R.id.pull_request_menu_refresh: {

                if (progressDialog != null)
                    executeProgressDialog();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

        actionBarTitle.setTextColor(Color.parseColor("#3977b0"));

        supportFinishAfterTransition();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        return super.dispatchTouchEvent(ev);
    }
}
