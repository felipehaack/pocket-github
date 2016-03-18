package br.sp.pocketgithub.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import br.sp.pocketgithub.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }catch (NullPointerException e){

            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        finish();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
}
