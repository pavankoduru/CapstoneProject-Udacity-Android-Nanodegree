package com.pavan.MyCapstone;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DisplayActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
   @BindView(R.id.recyclerViewId)
    RecyclerView recyclerView;
    String RandomJokesUrl = "http://api.icndb.com/jokes/random/";
    String JokesnumCount;
    List<JokeModel> jokeModelList;
    private static final int LoaderId = 123;
@BindView(R.id.progressbar)
    ProgressBar progressBar;
    private InterstitialAd interstitialAd;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        Snackbar.make(findViewById(R.id.llayout), R.string.displaysnackbar,Snackbar.LENGTH_LONG).show();
        ButterKnife.bind(this);
        MobileAds.initialize(this);
        interstitialAd=new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.addunitid));
        interstitialAd.loadAd(new AdRequest.Builder().build());

        JokesnumCount = getIntent().getStringExtra(MainActivity.NUMJOKESEXTRA);
        jokeModelList = new ArrayList<>();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference(getString(R.string.jokespath));

        if (getNetWorkStatus()) {
            getSupportLoaderManager().initLoader(LoaderId, null, this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            JokesAdapter jokesAdapter=new JokesAdapter(this, jokeModelList);
            recyclerView.setAdapter(jokesAdapter);


        } else {
            AlertDialog.Builder builder=new AlertDialog.Builder(DisplayActivity.this);

            builder.setTitle(R.string.nointernetconn);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
                }
            }).setCancelable(false).show();

        }
        if(interstitialAd.isLoaded())
        {
            interstitialAd.show();
        }





    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new AsyncTaskLoader<String>(this) {
            @Nullable
            @Override
            public String loadInBackground() {
                try {
                    URL url = new URL(RandomJokesUrl + JokesnumCount);

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.connect();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while((line=bufferedReader.readLine())!=null)
                    {
                        return line;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
                if(getNetWorkStatus())
                progressBar.setVisibility(View.VISIBLE);
                else
                {
                    AlertDialog.Builder builder=new AlertDialog.Builder(DisplayActivity.this);

                    builder.setTitle(R.string.nointernetconn);
                    builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                        }
                    }).setCancelable(false).show();
                }

            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String s) {
        try {
                if(s!=null) {
                    JSONObject rootElement = new JSONObject(s);
                    JSONArray valueArray = rootElement.getJSONArray(getString(R.string.valuestring));
                    for (int num = 0; num < valueArray.length(); num++) {
                        JSONObject jokeObject = valueArray.getJSONObject(num);
                        String jokestring = jokeObject.optString(getString(R.string.jokestring));

                        jokeModelList.add(new JokeModel(jokestring));
                    }

                    //recyclerView.setAdapter(new JokesAdapter(this, jokeModelList));
                    progressBar.setVisibility(View.GONE);
                }
                else return;

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.displaymenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.addtooffline)
        {

            for(int i=0;i<jokeModelList.size();i++) {
                String insertKey=databaseReference.push().getKey();
                databaseReference.child(insertKey).setValue(jokeModelList.get(i));
            }
            Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
            finish();


        }
        return super.onOptionsItemSelected(item);
    }

    public boolean getNetWorkStatus() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        return (connectivityManager.getActiveNetworkInfo() != null) && (connectivityManager.getActiveNetworkInfo().isConnected());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(interstitialAd.isLoaded())
        {
            interstitialAd.show();
        }


    }
}


