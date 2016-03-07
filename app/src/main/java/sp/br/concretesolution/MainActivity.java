package sp.br.concretesolution;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import sp.br.concretesolution.adapters.RecyclerLanguageAdapter;
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

    public String currentLanguage = "";
    public String currentSort = "";
    public String[] languages;

    private Menu menu;
    private GitHubAPI gitHubAPI;
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

                gitHubAPI.getRepositories(currentLanguage, currentPage);
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
                    }

                    Repository currentRepository = recyclerRepositoryAdapter.getRepository();
                    currentRepository.getRepositoryItems().addAll(result.getRepositoryItems());

                    recyclerRepositoryAdapter.setRepository(currentRepository);
                    recyclerRepositoryAdapter.setCountItems(currentRepository.getRepositoryItems().size());
                    recyclerRepositoryAdapter.notifyDataSetChanged();

                    activityListener.clearSpinnerAnimation();

                    currentPage++;
                } else {

                    if (progressDialog != null)
                        progressDialog.dismiss();

                    Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_title), Toast.LENGTH_LONG).show();

                    activityListener.clearSpinnerWithFail();
                }
            }
        };
        gitHubAPI.startAPI();

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

                                activityListener.executeEndAnimationX(0f);
                                executeProgressDialog();
                            } else {

                                if (recyclerRepositoryAdapter.getRepository().getRepositoryItems().size() == 0) {

                                    activityListener.executeEndAnimationX(0f);
                                    executeProgressDialog();
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

        gitHubAPI.getRepositories(currentLanguage, currentPage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        this.menu = menu;

        return true;
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
