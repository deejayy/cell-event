package com.android.cellevent;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;

import android.widget.TextView;
import android.widget.Button;

import android.view.View;
import android.view.View.OnClickListener;
import android.content.SharedPreferences;

public class CellEvent extends Activity {
	public static final String PREFOBJ = "CellEvent.Preferences";
	SharedPreferences prefs;
	SharedPreferences.Editor editor;

	TextView resultText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		resultText = (TextView) findViewById(R.id.CellId);
		resultText.setText("started...");

        Button button = (Button)findViewById(R.id.Button1);
        button.setOnClickListener(Button1Click);
        button = (Button)findViewById(R.id.Button2);
        button.setOnClickListener(Button2Click);

		prefs = getSharedPreferences(PREFOBJ, 0);
		editor = prefs.edit();

		editor.putInt("16131", 0);
		editor.putInt("16563", 0);
		editor.putInt("16602", 0);
		editor.commit();
	}
	
    private OnClickListener Button1Click = new OnClickListener() {
        public void onClick(View v) {
			debugCE("Startservice");
			startService(new Intent(CellEvent.this, CellEventService.class));
        }
    };
	
    private OnClickListener Button2Click = new OnClickListener() {
        public void onClick(View v) {
			debugCE("Stopservice");
			stopService(new Intent(CellEvent.this, CellEventService.class));
        }
    };
	
	public void debugCE(String str) {
		resultText.setText(resultText.getText() + "\n" + str);
	}
}
