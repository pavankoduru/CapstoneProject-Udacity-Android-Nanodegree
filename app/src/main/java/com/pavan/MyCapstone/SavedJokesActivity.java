package com.pavan.MyCapstone;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SavedJokesActivity extends AppCompatActivity {
    @BindView(R.id.recyclerViewsaved)
    RecyclerView recyclerView;
    @BindView(R.id.progressbarsaved)
    ProgressBar progressBar;
    JokesAdapter jokesAdapter;

    List<JokeModel> jokeModelList;
    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_jokes);
        ButterKnife.bind(this);
        progressBar.setVisibility(View.VISIBLE);
        databaseReference=firebaseDatabase.getReference(getString(R.string.jokespath));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        JokeModel jokeModel = dataSnapshot1.getValue(JokeModel.class);
                        jokeModelList.add(jokeModel);
                    }
                    jokesAdapter.notifyDataSetChanged();
                }

                    progressBar.setVisibility(View.GONE);




            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SavedJokesActivity.this, getString(R.string.nosaveddata), Toast.LENGTH_SHORT).show();

            }
        });
        jokeModelList=new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        jokesAdapter=new JokesAdapter(this,jokeModelList);
        recyclerView.setAdapter(jokesAdapter);



    }
}
