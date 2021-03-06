/**
 * Copyright (c) 2013-2016 Vincent MOULIN
 * 
 * This file is part of Doctor Vocab.
 * 
 * Doctor Vocab is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package vincent.moulin.vocab.activities;

import vincent.moulin.vocab.MyApplication;
import vincent.moulin.vocab.R;
import vincent.moulin.vocab.helpers.DatabaseHelper;
import vincent.moulin.vocab.libraries.apprater.AppRater;
import vincent.moulin.vocab.menus.DefaultMenuManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * The HomeActivity class
 * 
 * @author Vincent MOULIN
 */
public class HomeActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Trigger the initialization of the database
        DatabaseHelper.getInstance(MyApplication.getContext());
        //-----------------------------------------------------
        
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.home_activity);
        
        AppRater.process(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.basic, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return DefaultMenuManager.onOptionsItemSelected(item, this);
    }
    
    public void startTrainingFrToEn(View v) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("vincent.moulin.vocab", MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        
        editor.putString("STARTING_LANG_NAME", "french");
        editor.commit();
        
        this.startActivity(new Intent(this, TrainingActivity.class));
    }
    
    public void startTrainingEnToFr(View v) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("vincent.moulin.vocab", MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        
        editor.putString("STARTING_LANG_NAME", "english");
        editor.commit();
        
        this.startActivity(new Intent(this, TrainingActivity.class));
    }
}
