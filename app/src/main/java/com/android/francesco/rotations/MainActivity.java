package com.android.francesco.rotations;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private TextView welcomeText;
    private Button button;
    private EditText helloText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcomeText = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.button_home);
        helloText = (EditText) findViewById(R.id.editText);

        button.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        String name = helloText.getText().toString();
        Log.d(TAG, "onClicked with name" + name);
        String welcomeString = "What's happening " + name;
        welcomeText.setText(welcomeString);

    }
}
