package com.pavan.MyCapstone;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class JokesAdapter extends RecyclerView.Adapter<JokesAdapter.JokeHolder> {
    Context context;
    List<JokeModel> jokeModelList;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final String SPSTRING="jokestring";

    public JokesAdapter(DisplayActivity displayActivity, List<JokeModel> jokeModelList) {
        context=displayActivity;

        this.jokeModelList=jokeModelList;
    }

    public JokesAdapter(SavedJokesActivity savedJokesActivity, List<JokeModel> jokeModelList) {
        context=savedJokesActivity;
        this.jokeModelList=jokeModelList;
    }

    @NonNull
    @Override
    public JokeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v=LayoutInflater.from(context).inflate(R.layout.eachjokedesign,viewGroup,false);
        sharedPreferences=context.getSharedPreferences(context.getPackageName(),MODE_PRIVATE);
        editor=sharedPreferences.edit();


        return new JokeHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull JokeHolder jokeHolder, final int i) {
        jokeHolder.textView.setText(jokeModelList.get(i).getJoke());
        jokeHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString(SPSTRING,jokeModelList.get(i).getJoke());
                editor.commit();
                Intent intent=new Intent(context,JokeWidget.class);
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                int[] ids= AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context,JokeWidget.class));
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
                Toast.makeText(context, context.getString(R.string.wdigetadd), Toast.LENGTH_SHORT).show();
                context.sendBroadcast(intent);



            }
        });


    }

    @Override
    public int getItemCount() {
        return jokeModelList.size();
    }

    public class JokeHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public JokeHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.jokecontent);
        }
    }
}
