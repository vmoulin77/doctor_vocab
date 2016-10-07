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
import android.content.ContentValues;
import android.database.Cursor;

/**
 * The Card class represents a card.
 * One side of the card represents the English part and the other side represents the French part.
 * Some additional information attached to the card is mainly used in order to implement the Spaced Repetition System.
 * 
 * @author Vincent MOULIN
 */
public class Card implements Cloneable
{
    protected int id;
    protected EnglishSide englishSide;
    protected FrenchSide frenchSide;
    
    public Card(int id, EnglishSide englishSide, FrenchSide frenchSide) {
        this.id = id;
        this.englishSide = englishSide;
        this.frenchSide = frenchSide;
    }
    
    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    public EnglishSide getEnglishSide() {
        return this.englishSide;
    }
    public void setEnglishSide(EnglishSide englishSide) {
        this.englishSide = englishSide;
    }
    
    public FrenchSide getFrenchSide() {
        return this.frenchSide;
    }
    public void setFrenchSide(FrenchSide frenchSide) {
        this.frenchSide = frenchSide;
    }
    
    public Card clone() throws CloneNotSupportedException {
        Card retour = (Card) super.clone();

        retour.englishSide = (EnglishSide) this.englishSide.clone();
        retour.frenchSide = (FrenchSide) this.frenchSide.clone();
        
        return retour;
    }
    
    /**
     * Get the Side object of the current Card according to the given language name "langName".
     * @param langName the language name of the returned Side object
     * @return the Side object
     */
    public Side getSideByLangName(String langName) {
        if (langName.equals("english")) {
            return this.englishSide;
        } else {
            return this.frenchSide;
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
                new EnglishSide(
                    cursor.getString(0),
                    (cursor.getInt(2) != 0),
                    Status.find(cursor.getInt(4)),
                    (cursor.getInt(6) != 0),
                    cursor.getInt(8),
                    cursor.getInt(10),
                    cursor.getLong(12)
                ),
                new FrenchSide(
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
        
        contentValues.put("word_english", this.englishSide.getWord());
        contentValues.put("word_french", this.frenchSide.getWord());
        if (this.englishSide.getIsActive()) {
            contentValues.put("is_active_english", 1);
        } else {
            contentValues.put("is_active_english", 0);
        }
        if (this.frenchSide.getIsActive()) {
            contentValues.put("is_active_french", 1);
        } else {
            contentValues.put("is_active_french", 0);
        }
        contentValues.put("id_status_english", this.englishSide.getStatus().getId());
        contentValues.put("id_status_french", this.frenchSide.getStatus().getId());
        if (this.englishSide.getIsAccelerated()) {
            contentValues.put("is_accelerated_english", 1);
        } else {
            contentValues.put("is_accelerated_english", 0);
        }
        if (this.frenchSide.getIsAccelerated()) {
            contentValues.put("is_accelerated_french", 1);
        } else {
            contentValues.put("is_accelerated_french", 0);
        }
        contentValues.put("primary_indice_english", this.englishSide.getPrimaryIndice());
        contentValues.put("primary_indice_french", this.frenchSide.getPrimaryIndice());
        contentValues.put("secondary_indice_english", this.englishSide.getSecondaryIndice());
        contentValues.put("secondary_indice_french", this.frenchSide.getSecondaryIndice());
        contentValues.put("timestamp_last_answer_english", this.englishSide.getTimestampLastAnswer());
        contentValues.put("timestamp_last_answer_french", this.frenchSide.getTimestampLastAnswer());
        
        return dbh.getWritableDatabase().update("card", contentValues, "id = ?", new String[]{String.valueOf(this.id)});
    }
}
