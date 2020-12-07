package com.pavan.MyCapstone;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.numberOfJokes)
    EditText numberOfJokes;
    public   static final String NUMJOKESEXTRA="numjokesExtra";
    String url="http://api.icndb.com/jokes/random/";
    @BindView(R.id.adView)
    AdView adView;
    public static String MENUNAME="menuname";
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        MobileAds.initialize(getApplicationContext());


        AdRequest adRequest=new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        firebaseAuth=FirebaseAuth.getInstance();

    }

    public void getTheResult(View view) {
        String number=numberOfJokes.getText().toString();
        String totalUrl=url+number;
        if((!url.equals(totalUrl))&&(Integer.parseInt(number)<=100)&&Integer.parseInt(number)>=1)
        {

            if(getNetWorkStatus())
            {
                Intent goToNextActivity=new Intent(getApplicationContext(),DisplayActivity.class);
                goToNextActivity.putExtra(NUMJOKESEXTRA,numberOfJokes.getText().toString());
                startActivity(goToNextActivity);
            }
            else
            {
                AlertDialog.Builder builder=new AlertDialog.Builder(this);

                builder.setTitle(R.string.nointernetconn);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(Intent.ACTION_MAIN);
                        intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
                        startActivity(intent);
                    }
                }).setCancelable(false).show();
            }

        }
        else
        {
            AlertDialog.Builder builder=new AlertDialog.Builder(this);

            builder.setTitle(R.string.datanotvalid);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }).setCancelable(false).show();
        }


    }
    public  boolean getNetWorkStatus()
    {
        ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        return (connectivityManager.getActiveNetworkInfo()!=null)&&(connectivityManager.getActiveNetworkInfo().isConnected());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.settings)
        {
            Intent intent=new Intent(this,SettingsActivity.class);
            intent.putExtra(MENUNAME,item.getTitle());
            startActivity(intent);
        }
        else if(item.getItemId()==R.id.logout)
        {
            firebaseAuth.signOut();
            Toast.makeText(this, getString(R.string.signedout), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
        else if(item.getItemId()==R.id.viewsaved)
        {
            startActivity(new Intent(this,SavedJokesActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
