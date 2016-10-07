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

import vincent.moulin.vocab.R;
import vincent.moulin.vocab.entities.TrainingCard;
import vincent.moulin.vocab.entities.Deck;
import vincent.moulin.vocab.entities.Language;
import vincent.moulin.vocab.entities.Pack;
import vincent.moulin.vocab.entities.StatSnap;
import vincent.moulin.vocab.entities.Side;
import vincent.moulin.vocab.menus.TrainingMenuManager;
import vincent.moulin.vocab.utilities.Now;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The TrainingActivity class
 * 
 * @author Vincent MOULIN
 */
public class TrainingActivity extends Activity
{
    private TrainingCard currentCard, prevCardBeforeAnswering, prevCardAfterAnswering;
    private Pack prevPackBeforeAnswering;
    private long timestampLastAnswer;
    private String startingLangName;
    private boolean cancellationOptionIsEnabled;

    public TrainingCard getCurrentCard() {
        return this.currentCard;
    }
    public void setCurrentCard(TrainingCard currentCard) {
        this.currentCard = currentCard;
    }

    public TrainingCard getPrevCardBeforeAnswering() {
        return this.prevCardBeforeAnswering;
    }
    public void setPrevCardBeforeAnswering(TrainingCard prevCardBeforeAnswering) {
        this.prevCardBeforeAnswering = prevCardBeforeAnswering;
    }
    
    public TrainingCard getPrevCardAfterAnswering() {
        return this.prevCardAfterAnswering;
    }
    public void setPrevCardAfterAnswering(TrainingCard prevCardAfterAnswering) {
        this.prevCardAfterAnswering = prevCardAfterAnswering;
    }
    
    public Pack getPrevPackBeforeAnswering() {
        return this.prevPackBeforeAnswering;
    }
    public void setPrevPackBeforeAnswering(Pack prevPackBeforeAnswering) {
        this.prevPackBeforeAnswering = prevPackBeforeAnswering;
    }
    
    public long getTimestampLastAnswer() {
        return this.timestampLastAnswer;
    }
    public void setTimestampLastAnswer(long timestampLastAnswer) {
        this.timestampLastAnswer = timestampLastAnswer;
    }

    public String getStartingLangName() {
        return this.startingLangName;
    }
    public void setStartingLangName(String startingLangName) {
        this.startingLangName = startingLangName;
    }
    
    public boolean getCancellationOptionIsEnabled() {
        return this.cancellationOptionIsEnabled;
    }
    public void setCancellationOptionIsEnabled(boolean cancellationOptionIsEnabled) {
        this.cancellationOptionIsEnabled = cancellationOptionIsEnabled;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TextView textView;
        
        this.disableCancellationOption();
        
        Now.getInstance().reset();

        SharedPreferences sharedPreferences = this.getSharedPreferences("vincent.moulin.vocab", MODE_PRIVATE);
        
        this.startingLangName = sharedPreferences.getString("STARTING_LANG_NAME", null);
        
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.training_activity);
        
        textView = (TextView) this.findViewById(R.id.translation_direction);
        if (this.startingLangName.equals("english")) {
            textView.setText(R.string.translation_direction_en_to_fr);
        } else {
            textView.setText(R.string.translation_direction_fr_to_en);
        }
        
        this.currentCard = Deck.algoSRS(this.startingLangName);
        this.displayWord();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.cancellation_option).setEnabled(this.cancellationOptionIsEnabled);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.basic, menu);
        this.getMenuInflater().inflate(R.menu.training_extras, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return TrainingMenuManager.onOptionsItemSelected(item, this);
    }
    
    public void disableCancellationOption() {
        this.prevCardBeforeAnswering = null;
        this.prevCardAfterAnswering = null;
        this.prevPackBeforeAnswering = null;
        this.timestampLastAnswer = 0;
        this.cancellationOptionIsEnabled = false;
    }
    
    public void displayWord() {
        TextView textView;
        
        textView = (TextView) this.findViewById(R.id.training_header);
        textView.setTextColor(Color.parseColor(this.currentCard.getQuestionSide().getStatus().getColor()));
        
        textView = (TextView) this.findViewById(R.id.question_word);
        textView.setText(Html.fromHtml(this.currentCard.getQuestionSide().getWord()));
    }
    
    public void statAccess(View v) {
        this.startActivity(new Intent(this, StatActivity.class));
    }
    
    private void answerProcess(boolean answerIsOk) {
        Now.getInstance().reset();
        
        StatSnap.updateAll();
        
        this.cancellationOptionIsEnabled = true;
        
        this.timestampLastAnswer = Now.getInstance().getRawTimestamp();
        
        this.prevPackBeforeAnswering = null;

        if (answerIsOk) {
            if (this.currentCard.getQuestionSide().getStatus().getName().equals("learning")) {
                if (this.currentCard.getQuestionSide().getSecondaryIndice() == Side.MAX_SECONDARY_INDICE) {
                    Toast
                        .makeText(this, R.string.congratulation_toast_content, Toast.LENGTH_SHORT)
                        .show();
                } else {
                    this.prevPackBeforeAnswering = Pack.findByIdLangAndIndice(
                        Language.findId(this.startingLangName),
                        this.currentCard.getQuestionSide().getSecondaryIndice() + 1
                    );
                }
            }
        }

        try {
            this.prevCardBeforeAnswering = this.currentCard.clone();
        } catch (CloneNotSupportedException e) {
            this.disableCancellationOption();
        }

        this.currentCard.manageAnswer(this.startingLangName, answerIsOk);
        
        this.prevCardAfterAnswering = this.currentCard;

        this.currentCard = Deck.algoSRS(this.startingLangName);
        this.displayWord();
    }
    
    public void processOk(View v) {
        this.answerProcess(true);
    }
    
    public void processNok(View v) {
        this.answerProcess(false);
    }
    
    public void showTraduction(View v) {
        new AlertDialog.Builder(this)
            .setMessage(Html.fromHtml(this.currentCard.getSolutionSide().getWord()))
            .setNeutralButton(R.string.closure_button_content, null)
            .show();
    }
}
