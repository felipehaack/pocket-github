package sp.br.githubpocket;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import sp.br.githubpocket.R;
import sp.br.githubpocket.adapters.RecyclerLanguageAdapter;
import sp.br.githubpocket.apis.GithubAPI;
import sp.br.githubpocket.activities.PullRequestActivity;
import sp.br.githubpocket.adapters.RecyclerRepositoryAdapter;
import sp.br.githubpocket.listeners.ActivityListener;
import sp.br.githubpocket.listeners.RecyclerClickListener;
import sp.br.githubpocket.listeners.RecyclerScrollListener;
import sp.br.githubpocket.models.PullRequest;
import sp.br.githubpocket.models.Repository;
import sp.br.githubpocket.models.RepositoryItem;

public class MainActivity extends AppCompatActivity {

    public int currentPage = 1;

    public String currentLanguage = "";
    public String currentSort = "stars";
    private int currentSortPosition = 0;
    public String[] languages;

    private Menu menu;
    private GithubAPI githubAPI;
    private ActivityListener activityListener;
    private RecyclerRepositoryAdapter recyclerRepositoryAdapter;
    private RecyclerLanguageAdapter recyclerLanguageAdapter;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        /* Create ToolBar for future customization */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* Initialize array with languages and values */
        languages = getResources().getStringArray(R.array.languages_array);

        /* Create Adapter for RecyclerView and LinearLayoutManager basic elements */
        recyclerRepositoryAdapter = new RecyclerRepositoryAdapter(new Repository(), this.getApplicationContext());
        recyclerLanguageAdapter = new RecyclerLanguageAdapter(languages);

        LinearLayoutManager layoutManagerRepository = new LinearLayoutManager(this);
        LinearLayoutManager layoutManagerLanguage = new LinearLayoutManager(this);

        final RecyclerView recyclerViewRepository = (RecyclerView) findViewById(R.id.recycler_repository);
        final RecyclerView recyclerViewLanguages = (RecyclerView) findViewById(R.id.recycler_languages);

        /* Set layout manager, adapter and others things to work */
        recyclerViewRepository.setLayoutManager(layoutManagerRepository);
        recyclerViewRepository.setAdapter(recyclerRepositoryAdapter);

        recyclerViewLanguages.setLayoutManager(layoutManagerLanguage);
        recyclerViewLanguages.setAdapter(recyclerLanguageAdapter);

        /* Create a listener for activity to detect swipe down and search for more github repositories */
        activityListener = new ActivityListener(this, recyclerViewRepository, (ImageView) findViewById(R.id.see_more_spinner), (TextView) findViewById(R.id.see_more_text)) {

            @Override
            public void infiniteScrollDetected() {

                githubAPI.getRepositories(currentLanguage, currentPage, currentSort);
            }
        };

        githubAPI = new GithubAPI() {

            @Override
            public void pullRequestAPIResult(List<PullRequest> pullRequests, String message) {

            }

            @Override
            public void repositoryAPIResult(Repository result, String message) {

                if (result != null) {

                    if (progressDialog != null) {

                        progressDialog.dismiss();
                    }

                    Repository currentRepository = recyclerRepositoryAdapter.getRepository();
                    currentRepository.getRepositoryItems().addAll(result.getRepositoryItems());

                    recyclerRepositoryAdapter.setRepository(currentRepository);
                    recyclerRepositoryAdapter.setCountItems(currentRepository.getRepositoryItems().size());
                    recyclerRepositoryAdapter.notifyDataSetChanged();

                    activityListener.clearSpinnerAnimation();

                    menu.findItem(R.id.main_activity_sort_repository).setVisible(true);

                    currentPage++;
                } else {

                    if (progressDialog != null)
                        progressDialog.dismiss();

                    Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_title), Toast.LENGTH_LONG).show();

                    activityListener.clearSpinnerWithFail();
                }
            }
        };
        githubAPI.startAPI();

        /* Add ScrollListener and TouchItem to recycleViewRepository */
        recyclerViewRepository.addOnScrollListener(new RecyclerScrollListener(activityListener));

        recyclerViewRepository.addOnItemTouchListener(new RecyclerClickListener(this, new RecyclerClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(final View view, final int position) {

                if (!activityListener.enableTranslateX) {

                    recyclerViewRepository.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            List<RepositoryItem> repositoryItems = recyclerRepositoryAdapter.getRepository().getRepositoryItems();

                            Intent intent = new Intent(MainActivity.this, PullRequestActivity.class);

                            intent.putExtra("repositoryName", repositoryItems.get(position).getName());
                            intent.putExtra("repositoryOwner", repositoryItems.get(position).getOwner().getLogin());

                            View viewTitle = view.findViewById(R.id.repository_name);

                            if (Build.VERSION.SDK_INT >= 21) {

                                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, viewTitle, "moveTitle");
                                startActivity(intent, options.toBundle());
                            } else {

                                startActivity(intent);
                            }
                        }
                    }, 150);
                }
            }
        }));

        /* Add TouchItem to recycleViewLanguage */
        recyclerViewLanguages.addOnItemTouchListener(new RecyclerClickListener(this, new RecyclerClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {

                if(!activityListener.enableTranslateX) {

                    recyclerViewLanguages.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if (!languages[position].equals(currentLanguage)) {

                                recyclerRepositoryAdapter.clearRepository();

                                currentLanguage = languages[position];
                                currentSort = "stars";
                                currentSortPosition = 0;

                                activityListener.executeEndAnimationX(0f);
                                executeProgressDialog();
                            } else {

                                if (recyclerRepositoryAdapter.getRepository().getRepositoryItems().size() == 0) {

                                    currentSort = "stars";
                                    currentSortPosition = 0;

                                    activityListener.executeEndAnimationX(0f);
                                    executeProgressDialog();
                                }else{

                                    Toast.makeText(MainActivity.this, R.string.main_activity_repository_choose_same, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }, 150);
                }
            }
        }));
    }

    private void executeProgressDialog() {

        progressDialog = ProgressDialog.show(this,
                getResources().getString(R.string.progress_dialog_title),
                getResources().getString(R.string.progress_dialog_subtitle_subtitle),
                true);

        progressDialog.setIndeterminate(true);

        currentPage = 1;

        githubAPI.getRepositories(currentLanguage, currentPage, currentSort);
    }

    public void executeAlertDialogSortItems(){

        Log.e("Test", currentLanguage + " " + currentPage + " " + currentSort);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert_dialog_title)
                .setItems(R.array.sort_array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(currentSortPosition != which) {

                            currentSortPosition = which;

                            if (activityListener.translateAcumX >= activityListener.translateMaxX)
                                activityListener.executeEndAnimationX(0f);

                            switch (which) {

                                case 0: {

                                    currentSort = "stars";

                                    break;
                                }

                                case 1: {

                                    currentSort = "forks";

                                    break;
                                }
                            }

                            recyclerRepositoryAdapter.clearRepository();

                            executeProgressDialog();
                        }else{

                            Toast.makeText(MainActivity.this, R.string.alert_dialog_same_choose, Toast.LENGTH_LONG).show();
                        }
                    }
                });

        builder.create().show();
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

        switch (item.getItemId()){

            case R.id.main_activity_sort_repository: {

                executeAlertDialogSortItems();

                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if(activityListener.translateAcumX == 0f){

            activityListener.executeEndAnimationX(activityListener.translateMaxX);
        }else{

            super.onBackPressed();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);

        activityListener.onConfigurationChanged();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        return activityListener.motionEvent(ev) || super.dispatchTouchEvent(ev);
    }
}
