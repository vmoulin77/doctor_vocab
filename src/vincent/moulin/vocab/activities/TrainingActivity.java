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
import vincent.moulin.vocab.entities.Card;
import vincent.moulin.vocab.entities.Deck;
import vincent.moulin.vocab.entities.Language;
import vincent.moulin.vocab.entities.Pack;
import vincent.moulin.vocab.entities.StatSnap;
import vincent.moulin.vocab.entities.Word;
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
    private Card currentCard, prevCardBeforeAnswering, prevCardAfterAnswering;
    private Pack prevPackBeforeAnswering;
    private long timestampLastAnswer;
    private String startingLangName;
    private boolean cancellationOptionIsEnabled;

    public Card getCurrentCard() {
        return currentCard;
    }
    public void setCurrentCard(Card currentCard) {
        this.currentCard = currentCard;
    }

    public Card getPrevCardBeforeAnswering() {
        return prevCardBeforeAnswering;
    }
    public void setPrevCardBeforeAnswering(Card prevCardBeforeAnswering) {
        this.prevCardBeforeAnswering = prevCardBeforeAnswering;
    }
    
    public Card getPrevCardAfterAnswering() {
        return prevCardAfterAnswering;
    }
    public void setPrevCardAfterAnswering(Card prevCardAfterAnswering) {
        this.prevCardAfterAnswering = prevCardAfterAnswering;
    }
    
    public Pack getPrevPackBeforeAnswering() {
        return prevPackBeforeAnswering;
    }
    public void setPrevPackBeforeAnswering(Pack prevPackBeforeAnswering) {
        this.prevPackBeforeAnswering = prevPackBeforeAnswering;
    }
    
    public long getTimestampLastAnswer() {
        return timestampLastAnswer;
    }
    public void setTimestampLastAnswer(long timestampLastAnswer) {
        this.timestampLastAnswer = timestampLastAnswer;
    }

    public String getStartingLangName() {
        return startingLangName;
    }
    public void setStartingLangName(String startingLangName) {
        this.startingLangName = startingLangName;
    }
    
    public boolean getCancellationOptionIsEnabled() {
        return cancellationOptionIsEnabled;
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
        setContentView(R.layout.training_activity);
        
        textView = (TextView) findViewById(R.id.translation_direction);
        if (this.startingLangName.equals("english")) {
            textView.setText(R.string.translation_direction_en_to_fr);
        } else {
            textView.setText(R.string.translation_direction_fr_to_en);
        }
        
        this.currentCard = Deck.algoSelectWord(this.startingLangName);
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
        getMenuInflater().inflate(R.menu.basic, menu);
        getMenuInflater().inflate(R.menu.training_extras, menu);
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
        Word wordToTranslate = this.currentCard.getWordByLangName(this.startingLangName);
        
        textView = (TextView) findViewById(R.id.training_header);
        textView.setTextColor(Color.parseColor(wordToTranslate.getStatus().getColor()));
        
        textView = (TextView) findViewById(R.id.word_to_translate);
        textView.setText(Html.fromHtml(wordToTranslate.getContent()));
    }
    
    public void statAccess(View v) {
        startActivity(new Intent(this, StatActivity.class));
    }
    
    private void answerProcess(boolean answerIsOk) {
        Now.getInstance().reset();
        
        StatSnap.updateAll();
        
        this.cancellationOptionIsEnabled = true;
        
        this.timestampLastAnswer = Now.getInstance().getRawTimestamp();
        
        this.prevPackBeforeAnswering = null;

        if (answerIsOk) {
            Word wordToTranslate = this.currentCard.getWordByLangName(this.startingLangName);

            if (wordToTranslate.getStatus().getName().equals("learning")) {
                if (wordToTranslate.getSecondaryIndice() == Word.MAX_SECONDARY_INDICE) {
                    Toast
                        .makeText(this, R.string.congratulation_toast_content, Toast.LENGTH_SHORT)
                        .show();
                } else {
                    this.prevPackBeforeAnswering = Pack.findByIdLangAndIndice(
                        Language.findId(this.startingLangName),
                        wordToTranslate.getSecondaryIndice() + 1
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

        this.currentCard = Deck.algoSelectWord(this.startingLangName);
        this.displayWord();
    }
    
    public void processOk(View v) {
        answerProcess(true);
    }
    
    public void processNok(View v) {
        answerProcess(false);
    }
    
    public void showTraduction(View v) {
        Word solution;
        
        if (this.startingLangName.equals("english")) {
            solution = this.currentCard.getWordFrench();
        } else {
            solution = this.currentCard.getWordEnglish();
        }

        new AlertDialog.Builder(this)
            .setMessage(Html.fromHtml(solution.getContent()))
            .setNeutralButton(R.string.closure_button_content, null)
            .show();
    }
}
