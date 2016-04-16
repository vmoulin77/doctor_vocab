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

package vincent.moulin.vocab.entities;

import vincent.moulin.vocab.MyApplication;
import vincent.moulin.vocab.helpers.DatabaseHelper;
import vincent.moulin.vocab.utilities.CalendarNow;
import android.content.ContentValues;
import android.database.Cursor;

/**
 * The Card class represents a word.
 * One side of the card represents the English form and the other side represents the French form.
 * Some additional information attached to the card is mainly used in order to implement the Spaced Repetition System.
 * 
 * @author Vincent MOULIN
 */
public class Card implements Cloneable
{
    private int id;
    private WordEnglish wordEnglish;
    private WordFrench wordFrench;
    
    public Card(int id, WordEnglish wordEnglish, WordFrench wordFrench) {
        this.id = id;
        this.wordEnglish = wordEnglish;
        this.wordFrench = wordFrench;
    }
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    public WordEnglish getWordEnglish() {
        return wordEnglish;
    }
    public void setWordEnglish(WordEnglish wordEnglish) {
        this.wordEnglish = wordEnglish;
    }
    
    public WordFrench getWordFrench() {
        return wordFrench;
    }
    public void setWordFrench(WordFrench wordFrench) {
        this.wordFrench = wordFrench;
    }
    
    public Card clone() throws CloneNotSupportedException {
        Card retour = (Card) super.clone();

        retour.wordEnglish = (WordEnglish) this.wordEnglish.clone();
        retour.wordFrench = (WordFrench) this.wordFrench.clone();
        
        return retour;
    }
    
    /**
     * Get the Word object of the current Card according to the given language name "langName".
     * @param langName the language name of the returned Word object
     * @return the Word object
     */
    public Word getWordByLangName(String langName) {
        if (langName.equals("english")) {
            return this.wordEnglish;
        } else {
            return this.wordFrench;
        }
    }
    
    /**
     * Find the Card whose id is "id".
     * @param id the id
     * @return the Card whose id is "id"
     */
    public static Card find(int id) {
        DatabaseHelper dbh = DatabaseHelper.getInstance(MyApplication.getContext());
        String query;
        Cursor cursor;
        Card retour;
        
        query = "SELECT "
              +     "word_english, " //0
              +     "word_french, " //1
              +     "is_active_english, " //2
              +     "is_active_french, " //3
              +     "id_status_english, " //4
              +     "id_status_french, " //5
              +     "is_accelerated_english, " //6
              +     "is_accelerated_french, " //7
              +     "primary_indice_english, " //8
              +     "primary_indice_french, " //9
              +     "secondary_indice_english, " //10
              +     "secondary_indice_french, " //11
              +     "timestamp_last_answer_english, " //12
              +     "timestamp_last_answer_french " //13
              + "FROM card "
              + "WHERE id = " + id;
        
        cursor = dbh.getReadableDatabase().rawQuery(query, null);
        
        if (cursor.getCount() == 0) {
            retour = null;
        } else {
            cursor.moveToFirst();
            
            retour = new Card(
                id,
                new WordEnglish(
                    cursor.getString(0),
                    (cursor.getInt(2) != 0),
                    Status.find(cursor.getInt(4)),
                    (cursor.getInt(6) != 0),
                    cursor.getInt(8),
                    cursor.getInt(10),
                    cursor.getLong(12)
                ),
                new WordFrench(
                    cursor.getString(1),
                    (cursor.getInt(3) != 0),
                    Status.find(cursor.getInt(5)),
                    (cursor.getInt(7) != 0),
                    cursor.getInt(9),
                    cursor.getInt(11),
                    cursor.getLong(13)
                )
            );
        }

        cursor.close();

        return retour;
    }
    
    /**
     * Update the current Card in the database.
     * @return the number of rows affected
     */
    public int save() {
        DatabaseHelper dbh = DatabaseHelper.getInstance(MyApplication.getContext());
        ContentValues contentValues = new ContentValues();
        
        contentValues.put("word_english", this.wordEnglish.getContent());
        contentValues.put("word_french", this.wordFrench.getContent());
        if (this.wordEnglish.getIsActive()) {
            contentValues.put("is_active_english", 1);
        } else {
            contentValues.put("is_active_english", 0);
        }
        if (this.wordFrench.getIsActive()) {
            contentValues.put("is_active_french", 1);
        } else {
            contentValues.put("is_active_french", 0);
        }
        contentValues.put("id_status_english", this.wordEnglish.getStatus().getId());
        contentValues.put("id_status_french", this.wordFrench.getStatus().getId());
        if (this.wordEnglish.getIsAccelerated()) {
            contentValues.put("is_accelerated_english", 1);
        } else {
            contentValues.put("is_accelerated_english", 0);
        }
        if (this.wordFrench.getIsAccelerated()) {
            contentValues.put("is_accelerated_french", 1);
        } else {
            contentValues.put("is_accelerated_french", 0);
        }
        contentValues.put("primary_indice_english", this.wordEnglish.getPrimaryIndice());
        contentValues.put("primary_indice_french", this.wordFrench.getPrimaryIndice());
        contentValues.put("secondary_indice_english", this.wordEnglish.getSecondaryIndice());
        contentValues.put("secondary_indice_french", this.wordFrench.getSecondaryIndice());
        contentValues.put("timestamp_last_answer_english", this.wordEnglish.getTimestampLastAnswer());
        contentValues.put("timestamp_last_answer_french", this.wordFrench.getTimestampLastAnswer());
        
        return dbh.getWritableDatabase().update("card", contentValues, "id = ?", new String[]{String.valueOf(this.id)});
    }
    
    /**
     * Manage the answer of the user.
     * Update the current Card object and the database.
     * @param startingLangName the starting language name
     * @param answerIsOk true if the answer is ok and false otherwise
     */
    public void manageAnswer(String startingLangName, boolean answerIsOk) {
        Pack linkedPack;
        long rawTimestampNow = CalendarNow.getInstance().getRawTimestamp();
        Word wordToTranslate = this.getWordByLangName(startingLangName);
        
        // Calculation of the new status, the new primary indice and the new secondary indice
        if (wordToTranslate.getStatus().getName().equals("initial")) {
            if (answerIsOk) {
                wordToTranslate.setStatus("known");
                wordToTranslate.setPrimaryIndice(3);
            } else {
                wordToTranslate.setStatus("learning");
                wordToTranslate.setPrimaryIndice(1);
            }
            wordToTranslate.setSecondaryIndice(1);
        } else if (wordToTranslate.getStatus().getName().equals("known")) {
            if (answerIsOk) {
                if (wordToTranslate.getPrimaryIndice() != Word.MAX_PRIMARY_INDICE) {
                    if (wordToTranslate.getIsAccelerated()) {
                        wordToTranslate.setPrimaryIndice(Word.MAX_PRIMARY_INDICE);
                    } else {
                        wordToTranslate.setPrimaryIndice(wordToTranslate.getPrimaryIndice() + 1);
                    }
                }
            } else {
                wordToTranslate.setStatus("learning");
                if (wordToTranslate.getPrimaryIndice() <= 3) {
                    wordToTranslate.setPrimaryIndice(1);
                } else if (wordToTranslate.getPrimaryIndice() <= 6) {
                    wordToTranslate.setPrimaryIndice(2);
                } else {
                    wordToTranslate.setPrimaryIndice(3);
                }
                wordToTranslate.setSecondaryIndice(1);
            }
        } else {
            if (answerIsOk) {
                if (wordToTranslate.getSecondaryIndice() == Word.MAX_SECONDARY_INDICE) {
                    wordToTranslate.setStatus("known");
                    wordToTranslate.setSecondaryIndice(1);
                } else {
                    wordToTranslate.setSecondaryIndice(wordToTranslate.getSecondaryIndice() + 1);
                }
            } else {
                wordToTranslate.setSecondaryIndice(1);
            }
        }
        //--------------------------------------------------------------------
        
        // We set if the "wordToTranslate" is accelerated or not
        if ( ! answerIsOk) {
            wordToTranslate.setIsAccelerated(false);
        }
        //--------------------------------------------------------------------
        
        // Calculation of the new "timestampLastAnswer" and management of the Pack
        if (answerIsOk && wordToTranslate.getStatus().getName().equals("learning")) {
            linkedPack = wordToTranslate.retrievePack();
            
            if (wordToTranslate.belongsToPack()) {
                wordToTranslate.setTimestampLastAnswer(linkedPack.getTimestampPack());
            } else {
                wordToTranslate.setTimestampLastAnswer(rawTimestampNow);
                linkedPack.setTimestampPack(rawTimestampNow);
            }
            linkedPack.setTimestampLastAnswer(rawTimestampNow);
            
            linkedPack.save();
        } else {
            wordToTranslate.setTimestampLastAnswer(rawTimestampNow);
        }
        //--------------------------------------------------------------------

        this.save();
    }
}
