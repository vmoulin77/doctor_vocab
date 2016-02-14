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

package vincent.moulin.vocab.menus;

import vincent.moulin.vocab.R;
import vincent.moulin.vocab.activities.PhoneticRulesActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.MenuItem;

/**
 * The DefaultMenuManager class
 * 
 * @author Vincent MOULIN
 */
public class DefaultMenuManager
{
    public static boolean onOptionsItemSelected(MenuItem item, Activity activity) {
        switch (item.getItemId()) {
            case R.id.phonetic_rules_option:
                activity.startActivity(new Intent(activity, PhoneticRulesActivity.class));
                return true;
                
            case R.id.about_app_option:
                new AlertDialog.Builder(activity)
                    .setMessage(R.string.about_app_content)
                    .setNeutralButton(R.string.closure_button_content, null)
                    .show();
                return true;
                
            default:
                break;
        }
        
        return false;
    }
}
