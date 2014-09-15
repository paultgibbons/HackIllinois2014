package com.hack;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
 
@SuppressLint("NewApi")
public class MainActivity extends Activity {
 
	private MainActivity a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
        a=this;
        ActionBar actionBar = getActionBar();
        // add the custom view to the action bar
        actionBar.setCustomView(R.layout.actionbar_view);
        EditText search = (EditText) actionBar.getCustomView().findViewById(
                R.id.searchfield);
        search.setOnEditorActionListener(new OnEditorActionListener() {
 
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                    KeyEvent event) {
            	/*
            	SearchResults sr = Search.getInfo(v.getText().toString());
                Toast.makeText(MainActivity.this, v.getText() + "Search triggered",
                        Toast.LENGTH_LONG).show();// */
	String s = v.getText().toString();
	new DownloadWebpageTask().execute(s);
                return false;
            }
        });
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
                | ActionBar.DISPLAY_SHOW_HOME);
        
        TextView mTxtOutput;
        mTxtOutput = (TextView)findViewById(R.id.textView1);
        mTxtOutput.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
        // params comes from the execute() call: params[0] is the url.
		SearchResults sr = Search.getInfo(urls[0]);
		String[] titles = { "Basic Info", "Usage", "Missed Dosage", "Storage", "Side Effects", "Brand Names"};
		String content = "";
		
		for (int i = 0; i < titles.length; i++)
			titles[i] = titles[i].toUpperCase();

		content += sr.name.toUpperCase() + "\n\n";
		content += titles[0] + "\n";
		content += sr.data.get(0) + "\n";
		content += titles[1] + "\n";
		content += sr.data.get(5) + "\n";
		content += titles[2] + "\n";
		content += sr.data.get(1) + "\n";
		content += titles[3] + "\n";
		content += sr.data.get(4) + "\n";
		content += titles[4] + "\n";
		content += sr.data.get(2) + "\n";
		content += titles[5] + "\n";
		content += sr.data.get(3) + "\n";

		// use, miss a dose, side effects, brand names, storage, best taken
		return content;
   }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
		TextView t = (TextView) findViewById(R.id.textView1);
		t.setText(result);
    }
}
 
}