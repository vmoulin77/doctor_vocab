/**
 * Copyright 2013, 2015 Vincent MOULIN
 * 
 * This file is part of Doctor Vocab.
 * 
 * Doctor Vocab is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package vincent.moulin.vocab.activities;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import vincent.moulin.vocab.R;
import vincent.moulin.vocab.entities.Dictionary;
import vincent.moulin.vocab.entities.Frequency;
import vincent.moulin.vocab.entities.StatSnap;
import vincent.moulin.vocab.entities.Status;
import vincent.moulin.vocab.utilities.TimestampNow;
import vincent.moulin.vocab.menus.DefaultMenuManager;

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
        TimestampNow.getInstance().reinitialize();
        
        int[] statistics;
        int[][] statSnaps;
        TextView textView;
        Status initialStatus   = Status.getByName("initial"),
               learningStatus  = Status.getByName("learning"),
               knownStatus     = Status.getByName("known");
        int initialStatusColor   = Color.parseColor(initialStatus.getColor()),
            learningStatusColor  = Color.parseColor(learningStatus.getColor()),
            knownStatusColor     = Color.parseColor(knownStatus.getColor());
        Frequency dailyFrequency    = Frequency.getByName("daily"),
                  weeklyFrequency   = Frequency.getByName("weekly"),
                  monthlyFrequency  = Frequency.getByName("monthly");
        
        SharedPreferences sharedPreferences = this.getSharedPreferences("vincent.moulin.vocab", MODE_PRIVATE);
        
        this.startingLangName = sharedPreferences.getString("STARTING_LANG_NAME", null);
        
        StatSnap.updateAllStatSnaps();
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stat_activity);
        
        // The statistics part
        statistics = Dictionary.calcStatForLangName(this.startingLangName);
        
        textView = (TextView) findViewById(R.id.stat_initial_data);
        textView.setText(Integer.toString(statistics[initialStatus.getId()]));
        textView.setTextColor(initialStatusColor);
        
        textView = (TextView) findViewById(R.id.stat_learning_data);
        textView.setText(Integer.toString(statistics[learningStatus.getId()]));
        textView.setTextColor(learningStatusColor);
        
        textView = (TextView) findViewById(R.id.stat_known_data);
        textView.setText(Integer.toString(statistics[knownStatus.getId()]));
        textView.setTextColor(knownStatusColor);
        
        textView = (TextView) findViewById(R.id.stat_total_data);
        textView.setText(
            Integer.toString(
                statistics[initialStatus.getId()]
                + statistics[learningStatus.getId()]
                + statistics[knownStatus.getId()]
            )
        );
        //END: The statistics part
        
        // The statSnaps part
        statSnaps = StatSnap.getAllStatSnapsForLangName(this.startingLangName);
        
        textView = (TextView) findViewById(R.id.initial_daily_delta_data);
        textView.setText(convertDeltaToString(statistics[initialStatus.getId()] - statSnaps[dailyFrequency.getId()][initialStatus.getId()]));
        textView.setTextColor(initialStatusColor);
        textView = (TextView) findViewById(R.id.learning_daily_delta_data);
        textView.setText(convertDeltaToString(statistics[learningStatus.getId()] - statSnaps[dailyFrequency.getId()][learningStatus.getId()]));
        textView.setTextColor(learningStatusColor);
        textView = (TextView) findViewById(R.id.known_daily_delta_data);
        textView.setText(convertDeltaToString(statistics[knownStatus.getId()] - statSnaps[dailyFrequency.getId()][knownStatus.getId()]));
        textView.setTextColor(knownStatusColor);
        
        textView = (TextView) findViewById(R.id.initial_weekly_delta_data);
        textView.setText(convertDeltaToString(statistics[initialStatus.getId()] - statSnaps[weeklyFrequency.getId()][initialStatus.getId()]));
        textView.setTextColor(initialStatusColor);
        textView = (TextView) findViewById(R.id.learning_weekly_delta_data);
        textView.setText(convertDeltaToString(statistics[learningStatus.getId()] - statSnaps[weeklyFrequency.getId()][learningStatus.getId()]));
        textView.setTextColor(learningStatusColor);
        textView = (TextView) findViewById(R.id.known_weekly_delta_data);
        textView.setText(convertDeltaToString(statistics[knownStatus.getId()] - statSnaps[weeklyFrequency.getId()][knownStatus.getId()]));
        textView.setTextColor(knownStatusColor);
        
        textView = (TextView) findViewById(R.id.initial_monthly_delta_data);
        textView.setText(convertDeltaToString(statistics[initialStatus.getId()] - statSnaps[monthlyFrequency.getId()][initialStatus.getId()]));
        textView.setTextColor(initialStatusColor);
        textView = (TextView) findViewById(R.id.learning_monthly_delta_data);
        textView.setText(convertDeltaToString(statistics[learningStatus.getId()] - statSnaps[monthlyFrequency.getId()][learningStatus.getId()]));
        textView.setTextColor(learningStatusColor);
        textView = (TextView) findViewById(R.id.known_monthly_delta_data);
        textView.setText(convertDeltaToString(statistics[knownStatus.getId()] - statSnaps[monthlyFrequency.getId()][knownStatus.getId()]));
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
        
        return sign + Integer.toString(delta);
    }
}
