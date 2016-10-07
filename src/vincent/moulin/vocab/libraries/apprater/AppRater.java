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

package vincent.moulin.vocab.libraries.apprater;

import vincent.moulin.vocab.MyApplication;
import vincent.moulin.vocab.R;
import vincent.moulin.vocab.entities.Status;
import vincent.moulin.vocab.helpers.DatabaseHelper;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

/**
 * The AppRater class manages the display of the popup inviting the user to rate the application.
 * 
 * @author Vincent MOULIN
 */
public abstract class AppRater
{
    private final static int NB_KNOWN_WORDS_UNTIL_DISPLAY = 1000;
    
    /**
     * Change the value of the "app_rating_popup_already_answered" key in the "misc" table, so that the app rating popup will not be displayed again.
     */
    private static void doNotDisplayAgain() {
        DatabaseHelper dbh = DatabaseHelper.getInstance(MyApplication.getContext());
        ContentValues contentValues = new ContentValues();
        
        contentValues.put("misc_value", 1);
        dbh.getWritableDatabase().update("misc", contentValues, "misc_key = 'app_rating_popup_already_answered'", null);
    }
    
    /**
     * Check if the app rating popup has to be displayed or not.
     * @return true if the app rating popup has to be displayed and false otherwise
     */
    private static boolean check() {
        DatabaseHelper dbh = DatabaseHelper.getInstance(MyApplication.getContext());
        String query;
        Cursor cursor;
        boolean alreadyAnswered;
        int nbKnownWords;
        
        cursor = dbh.getReadableDatabase().query("misc", new String[]{"misc_value"}, "misc_key = 'app_rating_popup_already_answered'", null, null, null, null);
        cursor.moveToFirst();
        alreadyAnswered = (cursor.getInt(0) != 0);
        cursor.close();
        
        if ( ! alreadyAnswered) {
            query = "SELECT COUNT(*) "
                  + "FROM card "
                  + "WHERE is_active_english = 1 "
                  + "AND id_status_english = " + Status.findId("known");
            cursor = dbh.getReadableDatabase().rawQuery(query, null);
            cursor.moveToFirst();
            nbKnownWords = cursor.getInt(0);
            cursor.close();
            
            query = "SELECT COUNT(*) "
                  + "FROM card "
                  + "WHERE is_active_french = 1 "
                  + "AND id_status_french = " + Status.findId("known");
            cursor = dbh.getReadableDatabase().rawQuery(query, null);
            cursor.moveToFirst();
            nbKnownWords += cursor.getInt(0);
            cursor.close();
            
            if (nbKnownWords >= NB_KNOWN_WORDS_UNTIL_DISPLAY) {
                return true;
            } else {
                return false;
            }
        }
        
        return false;
    }
    
    /**
     * Display the app rating popup.
     * @param context the context
     */
    private static void display(final Context context) {
        AlertDialog.Builder mainPopup;
        
        mainPopup = new AlertDialog.Builder(context);
        mainPopup.setMessage(R.string.app_rating_main_popup_content);
        mainPopup.setNegativeButton(R.string.app_rating_main_popup_button_nok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                doNotDisplayAgain();
            }
        });
        mainPopup.setNeutralButton(R.string.app_rating_main_popup_button_later, null);
        mainPopup.setPositiveButton(R.string.app_rating_main_popup_button_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder errorPopup;
                
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
                } catch (android.content.ActivityNotFoundException e) {
                    errorPopup = new AlertDialog.Builder(context);
                    errorPopup.setTitle(R.string.error_popup_title)
                              .setMessage(R.string.app_rating_error_popup_content)
                              .setNeutralButton(R.string.closure_button_content, null)
                              .show();
                }
                
                doNotDisplayAgain();
            }
        });
        mainPopup.show();
    }
    
    /**
     * Check if the app rating popup has to be displayed and if yes, display it.
     * @param context the context
     */
    public static void process(Context context) {
        if (check()) {
            display(context);
        }
    }
}
