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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import vincent.moulin.vocab.R;
import vincent.moulin.vocab.entities.Dicotuple;
import vincent.moulin.vocab.menus.DefaultMenuManager;

/**
 * The HomeActivity class
 * 
 * @author Vincent MOULIN
 */
public class HomeActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Pour forcer l'initialisation de la base de données sur la page d'accueil
        Dicotuple.getById(0);
        //-----------------------------------------------------
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
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
    
    public void startTrainingFrToEn(View v) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("vincent.moulin.vocab", MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("STARTING_LANG_NAME", "french");
        editor.commit();
        
        startActivity(new Intent(this, TrainingActivity.class));
    }
    
    public void startTrainingEnToFr(View v) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("vincent.moulin.vocab", MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("STARTING_LANG_NAME", "english");
        editor.commit();
        
        startActivity(new Intent(this, TrainingActivity.class));
    }
}
