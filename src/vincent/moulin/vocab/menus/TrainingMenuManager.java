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

package vincent.moulin.vocab.menus;

import vincent.moulin.vocab.MyApplication;
import vincent.moulin.vocab.R;
import vincent.moulin.vocab.activities.TrainingActivity;
import vincent.moulin.vocab.entities.Language;
import vincent.moulin.vocab.entities.Status;
import vincent.moulin.vocab.entities.Word;
import vincent.moulin.vocab.helpers.DatabaseHelper;
import vincent.moulin.vocab.utilities.CalendarNow;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.text.Html;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * The TrainingMenuManager class
 * 
 * @author Vincent MOULIN
 */
public class TrainingMenuManager
{
    public static boolean onOptionsItemSelected(MenuItem item, Activity activity) {
        TrainingActivity trainingActivity = (TrainingActivity) activity;
        String startingLangName = trainingActivity.getStartingLangName();
        DatabaseHelper dbh = DatabaseHelper.getInstance(MyApplication.getContext());
        String query;
        Cursor cursor;
        
        if (DefaultMenuManager.onOptionsItemSelected(item, activity)) {
            return true;
        } else {
            switch (item.getItemId()) {
                case R.id.advice_option:
                    CalendarNow.getInstance().reinitialize();
                    
                    String adviceContent;
                    long timestampDiff;
                    int nbEligibleLearningWords = 0, nbEligibleKnownWords = 0;
                    String[] grammar, singular = {"word", "has"}, plural = {"words", "have"};
                    long rawTimestampNow = CalendarNow.getInstance().getRawTimestamp();
                    
                    //the number of eligible words in learning
                    query = "SELECT "
                          +     "secondary_indice_" + startingLangName + ", "
                          +     "timestamp_last_answer_" + startingLangName + " "
                          + "FROM card "
                          + "WHERE is_active_" + startingLangName + " = 1 "
                          + "AND id_status_" + startingLangName + " = " + Status.findId("learning");
                    
                    cursor = dbh.getReadableDatabase().rawQuery(query, null);
                    while (cursor.moveToNext()) {
                        timestampDiff = rawTimestampNow - cursor.getLong(1);
                        if (Word.learningWordIsEligible(cursor.getInt(0), timestampDiff)) {
                            nbEligibleLearningWords++;
                        }
                    }
                    cursor.close();
                    //-------------------------------------------------------------------
                    
                    //the number of eligible known words
                    query = "SELECT "
                          +     "primary_indice_" + startingLangName + ", "
                          +     "timestamp_last_answer_" + startingLangName + " "
                          + "FROM card "
                          + "WHERE is_active_" + startingLangName + " = 1 "
                          + "AND id_status_" + startingLangName + " = " + Status.findId("known");
                        
                    cursor = dbh.getReadableDatabase().rawQuery(query, null);
                    while (cursor.moveToNext()) {
                        timestampDiff = rawTimestampNow - cursor.getLong(1);
                        if (Word.knownWordIsEligible(cursor.getInt(0), timestampDiff)) {
                            nbEligibleKnownWords++;
                        }
                    }
                    cursor.close();
                    //-------------------------------------------------------------------
                    
                    if ((nbEligibleLearningWords == 0) && (nbEligibleKnownWords == 0)) {
                        adviceContent = "<b>IT'S ALL GOOD !</b><br /><br />";
                        adviceContent += "Concerning the words that have already been studied, the current state of your base doesn't require work.";
                    } else {
                        adviceContent = "<b>IT'S TIME TO WORK !</b><br /><br />";
                        
                        adviceContent += "<b>" + String.valueOf(nbEligibleLearningWords) + "</b> ";
                        if (nbEligibleLearningWords <= 1) {
                            grammar = singular;
                        } else {
                            grammar = plural;
                        }
                        adviceContent +=
                            grammar[0] + " whose status is \"Learning\" "
                            + grammar[1] + " to be quickly reviewed in order to make the learning process efficient.";
                        
                        adviceContent += "<br /><br />";
                        
                        adviceContent += "<b>" + String.valueOf(nbEligibleKnownWords) + "</b> ";
                        if (nbEligibleKnownWords <= 1) {
                            grammar = singular;
                        } else {
                            grammar = plural;
                        }
                        adviceContent +=
                            grammar[0] + " whose status is \"Known\" "
                            + grammar[1] + " to be quickly reviewed in order to consolidate the acquired knowledge.";
                    }
                    
                    new AlertDialog.Builder(activity)
                        .setMessage(Html.fromHtml(adviceContent))
                        .setNeutralButton(R.string.closure_button_content, null)
                        .show();
                    
                    return true;
                    
                case R.id.cancellation_option:
                    int statusIdBeforeAnswering = trainingActivity.getPrevCardBeforeAnswering().getWordByLangName(startingLangName).getStatus().getId(),
                        statusIdAfterAnswering = trainingActivity.getPrevCardAfterAnswering().getWordByLangName(startingLangName).getStatus().getId(),
                        newNbWords;
                    long timestampLastAnswer = trainingActivity.getTimestampLastAnswer();
                    ContentValues contentValues;

                    if (statusIdAfterAnswering != statusIdBeforeAnswering) {
                        query = "SELECT "
                              +     "id, " //0
                              +     "id_frequency, " //1
                              +     "id_status, " //2
                              +     "nb_words, " //3
                              +     "timestamp_last_update " //4
                              + "FROM stat_snap "
                              + "WHERE id_language = " + Language.findId(startingLangName);
    
                        cursor = dbh.getReadableDatabase().rawQuery(query, null);

                        while (cursor.moveToNext()) {
                            if (cursor.getLong(4) > timestampLastAnswer) {
                                contentValues = new ContentValues();
                                
                                newNbWords = cursor.getInt(3);
                                if (cursor.getInt(2) == statusIdAfterAnswering) {
                                    newNbWords -= 1;
                                }
                                if (cursor.getInt(2) == statusIdBeforeAnswering) {
                                    newNbWords += 1;
                                }
                                contentValues.put("nb_words", newNbWords);

                                dbh.getWritableDatabase().update("stat_snap", contentValues, "id = ?", new String[]{cursor.getString(0)});
                            }
                        }
                        cursor.close();
                    }

                    trainingActivity.getPrevCardBeforeAnswering().save();
                    if (trainingActivity.getPrevPackBeforeAnswering() != null) {
                        trainingActivity.getPrevPackBeforeAnswering().save();
                    }
                    trainingActivity.setCurrentCard(trainingActivity.getPrevCardBeforeAnswering());
                    
                    trainingActivity.disableCancellationOption();
                    
                    trainingActivity.displayWord();
                    
                    Toast
                        .makeText(trainingActivity, R.string.cancellation_toast_content, Toast.LENGTH_SHORT)
                        .show();
                    
                    return true;
                    
                default:
                    break;
            }
        }
        
        return false;
    }
}
