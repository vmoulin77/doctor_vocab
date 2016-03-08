/**
 * Copyright 2013, 2016 Vincent MOULIN
 * 
 * This file is part of Doctor Vocab.
 * 
 * Doctor Vocab is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package vincent.moulin.vocab.activities;

import vincent.moulin.vocab.R;
import vincent.moulin.vocab.entities.Deck;
import vincent.moulin.vocab.entities.Frequency;
import vincent.moulin.vocab.entities.StatSnap;
import vincent.moulin.vocab.entities.Status;
import vincent.moulin.vocab.menus.DefaultMenuManager;
import vincent.moulin.vocab.utilities.CalendarNow;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * The StatActivity class
 * 
 * @author Vincent MOULIN
 */
public class StatActivity extends Activity
{
    private String startingLangName;
    
    public String getStartingLangName() {
        return startingLangName;
    }
    public void setStartingLangName(String startingLangName) {
        this.startingLangName = startingLangName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CalendarNow.getInstance().reinitialize();
        
        SparseIntArray statistics;
        SparseArray<SparseIntArray> statSnaps;
        TextView textView;
        int initialStatusColor   = Color.parseColor(Status.getColorOf("initial")),
            learningStatusColor  = Color.parseColor(Status.getColorOf("learning")),
            knownStatusColor     = Color.parseColor(Status.getColorOf("known"));
        
        SharedPreferences sharedPreferences = this.getSharedPreferences("vincent.moulin.vocab", MODE_PRIVATE);
        
        this.startingLangName = sharedPreferences.getString("STARTING_LANG_NAME", null);
        
        StatSnap.updateAllStatSnaps();
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stat_activity);
        
        // The statistics part
        statistics = Deck.calcStatForLangName(this.startingLangName);
        
        textView = (TextView) findViewById(R.id.stat_initial_data);
        textView.setText(String.valueOf(statistics.get(Status.getIdOf("initial"))));
        textView.setTextColor(initialStatusColor);
        
        textView = (TextView) findViewById(R.id.stat_learning_data);
        textView.setText(String.valueOf(statistics.get(Status.getIdOf("learning"))));
        textView.setTextColor(learningStatusColor);
        
        textView = (TextView) findViewById(R.id.stat_known_data);
        textView.setText(String.valueOf(statistics.get(Status.getIdOf("known"))));
        textView.setTextColor(knownStatusColor);
        
        textView = (TextView) findViewById(R.id.stat_total_data);
        textView.setText(
            String.valueOf(
                statistics.get(Status.getIdOf("initial"))
                + statistics.get(Status.getIdOf("learning"))
                + statistics.get(Status.getIdOf("known"))
            )
        );
        //END: The statistics part
        
        // The statSnaps part
        statSnaps = StatSnap.getAllStatSnapsForLangName(this.startingLangName);
        
        textView = (TextView) findViewById(R.id.initial_daily_delta_data);
        textView.setText(convertDeltaToString(statistics.get(Status.getIdOf("initial")) - statSnaps.get(Frequency.getIdOf("daily")).get(Status.getIdOf("initial"))));
        textView.setTextColor(initialStatusColor);
        textView = (TextView) findViewById(R.id.learning_daily_delta_data);
        textView.setText(convertDeltaToString(statistics.get(Status.getIdOf("learning")) - statSnaps.get(Frequency.getIdOf("daily")).get(Status.getIdOf("learning"))));
        textView.setTextColor(learningStatusColor);
        textView = (TextView) findViewById(R.id.known_daily_delta_data);
        textView.setText(convertDeltaToString(statistics.get(Status.getIdOf("known")) - statSnaps.get(Frequency.getIdOf("daily")).get(Status.getIdOf("known"))));
        textView.setTextColor(knownStatusColor);
        
        textView = (TextView) findViewById(R.id.initial_weekly_delta_data);
        textView.setText(convertDeltaToString(statistics.get(Status.getIdOf("initial")) - statSnaps.get(Frequency.getIdOf("weekly")).get(Status.getIdOf("initial"))));
        textView.setTextColor(initialStatusColor);
        textView = (TextView) findViewById(R.id.learning_weekly_delta_data);
        textView.setText(convertDeltaToString(statistics.get(Status.getIdOf("learning")) - statSnaps.get(Frequency.getIdOf("weekly")).get(Status.getIdOf("learning"))));
        textView.setTextColor(learningStatusColor);
        textView = (TextView) findViewById(R.id.known_weekly_delta_data);
        textView.setText(convertDeltaToString(statistics.get(Status.getIdOf("known")) - statSnaps.get(Frequency.getIdOf("weekly")).get(Status.getIdOf("known"))));
        textView.setTextColor(knownStatusColor);
        
        textView = (TextView) findViewById(R.id.initial_monthly_delta_data);
        textView.setText(convertDeltaToString(statistics.get(Status.getIdOf("initial")) - statSnaps.get(Frequency.getIdOf("monthly")).get(Status.getIdOf("initial"))));
        textView.setTextColor(initialStatusColor);
        textView = (TextView) findViewById(R.id.learning_monthly_delta_data);
        textView.setText(convertDeltaToString(statistics.get(Status.getIdOf("learning")) - statSnaps.get(Frequency.getIdOf("monthly")).get(Status.getIdOf("learning"))));
        textView.setTextColor(learningStatusColor);
        textView = (TextView) findViewById(R.id.known_monthly_delta_data);
        textView.setText(convertDeltaToString(statistics.get(Status.getIdOf("known")) - statSnaps.get(Frequency.getIdOf("monthly")).get(Status.getIdOf("known"))));
        textView.setTextColor(knownStatusColor);
        //END: The statSnaps part
        
        // The caption part
        textView = (TextView) findViewById(R.id.caption_learning_label);
        textView.setTextColor(learningStatusColor);
        
        textView = (TextView) findViewById(R.id.caption_known_label);
        textView.setTextColor(knownStatusColor);
        //END: The caption part
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.basic, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return DefaultMenuManager.onOptionsItemSelected(item, this);
    }
    
    /**
     * Convert the given delta to String.
     * The result will always include a sign except for the number zero.
     * @return the delta converted to String
     */
    private String convertDeltaToString(int delta) {
        String sign = "";
        
        if (delta > 0) {
            sign = "+";
        }
        
        return sign + String.valueOf(delta);
    }
}
