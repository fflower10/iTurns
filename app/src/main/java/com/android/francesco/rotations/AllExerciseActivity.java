package com.android.francesco.rotations;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Francesco on 11/06/2017.
 */

public class AllExerciseActivity extends Activity {

    private static final String TAG = "AllExerciseActivity";
    ListView exerciseListView;
    List<Exercise> exercises = new ArrayList<>();
    ArrayAdapter<Exercise> exerciseAdapter;
    Uri imageUri = Uri.parse("android.resource://com.android.francesco.rotations/drawable/no_user_logo.png");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_exercises);


        Log.i(TAG, "In AllExerciseActivity...");
        Intent intent = new Intent();
        String s = getIntent().getStringExtra("EXTRA_SESSION_ID");
        Toast.makeText(this, "User Id: " + s, Toast.LENGTH_SHORT).show();

        exerciseListView = (ListView) findViewById(R.id.listViewExercise);

        mockData(exercises);
        populateList();
        registerForContextMenu(exerciseListView);

    }

    private void mockData(List<Exercise> exercises) {

        for(int i= 0; i < 3; i++){
            Exercise ex = new Exercise();
            ex.setId(i);
            ex.setName("Distensioni Panca Piana " + i);
            ex.setNumSeries(4);
            ex.setNumRips(10);
            ex.setRestTime(1.30);
            exercises.add(ex);

        }
    }

    private void populateList() {
        exerciseAdapter = new ExerciseListAdapter();
        exerciseListView.setAdapter(exerciseAdapter);

    }

    private class ExerciseListAdapter extends ArrayAdapter<Exercise> {
        public ExerciseListAdapter() {

            super (AllExerciseActivity.this, 0, exercises);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.list_exercise, parent, false);

            Exercise currentExercise = exercises.get(position);

            TextView exName = (TextView) view.findViewById(R.id.ex_name);
            exName.setText(currentExercise.getName());
            TextView exInfo = (TextView) view.findViewById(R.id.ex_info);
            exInfo.setText("Series: " + currentExercise.getNumSeries() +"   Rips: " + currentExercise.getNumRips());
            TextView exRestTime = (TextView) view.findViewById(R.id.ex_rest_time);
            exRestTime.setText("Rest time" + String.valueOf(currentExercise.getRestTime()) + " sec");
            ImageView exerciseImgView = (ImageView) findViewById(R.id.imgExercise);
            //exerciseImgView.setImageURI(imageUri);
            //ImageView ivContactImage = (ImageView) view.findViewById(R.id.ivContactImage);
            //ivContactImage.setImageURI(imageUri);
            //ImageView ivContactImage = (ImageView) view.findViewById(R.id.ivContactImage);
            //ivContactImage.setImageURI(currentUser.getImageURI());

            return view;
        }
    }

}
