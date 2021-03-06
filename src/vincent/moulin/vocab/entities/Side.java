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
import vincent.moulin.vocab.utilities.Now;
import android.database.Cursor;

/**
 * The Side class represents a side of a card.
 * 
 * @author Vincent MOULIN
 */
public abstract class Side implements Cloneable
{
    public static final int MAX_PRIMARY_INDICE = 10;
    public static final int MAX_SECONDARY_INDICE = 10;
    public static final int MAX_COMBINED_INDICE = 20;
    public static final int MAX_COMBINED_INDICE_ELIGIBLE_WORD = 10;
    
    private String word;
    private boolean isActive;
    private Status status;
    private boolean isAccelerated;
    private int primaryIndice;
    private int secondaryIndice;
    private long timestampLastAnswer;
    
    public Side(
        String word,
        boolean isActive,
        Status status,
        boolean isAccelerated,
        int primaryIndice,
        int secondaryIndice,
        long timestampLastAnswer
    ) {
        this.word = word;
        this.isActive = isActive;
        this.status = status;
        this.isAccelerated = isAccelerated;
        this.primaryIndice = primaryIndice;
        this.secondaryIndice = secondaryIndice;
        this.timestampLastAnswer = timestampLastAnswer;
    }
    
    public String getWord() {
        return this.word;
    }
    public void setWord(String word) {
        this.word = word;
    }
    
    public boolean getIsActive() {
        return this.isActive;
    }
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
    
    public Status getStatus() {
        return this.status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    public void setStatus(int idStatus) {
        this.status = Status.find(idStatus);
    }
    public void setStatus(String statusName) {
        this.status = Status.findByName(statusName);
    }
    
    public boolean getIsAccelerated() {
        return this.isAccelerated;
    }
    public void setIsAccelerated(boolean isAccelerated) {
        this.isAccelerated = isAccelerated;
    }
    
    public int getPrimaryIndice() {
        return this.primaryIndice;
    }
    public void setPrimaryIndice(int primaryIndice) {
        this.primaryIndice = primaryIndice;
    }
    
    public int getSecondaryIndice() {
        return this.secondaryIndice;
    }
    public void setSecondaryIndice(int secondaryIndice) {
        this.secondaryIndice = secondaryIndice;
    }
    
    public long getTimestampLastAnswer() {
        return this.timestampLastAnswer;
    }
    public void setTimestampLastAnswer(long timestampLastAnswer) {
        this.timestampLastAnswer = timestampLastAnswer;
    }

    public Side clone() throws CloneNotSupportedException {
        Side retour = (Side) super.clone();

        retour.status = (Status) this.status.clone();
        
        return retour;
    }
    
    /**
     * Get the Language object corresponding to the current Side.
     * @return the Language object corresponding to the current Side
     */
    public abstract Language getLanguage();
    
    /**
     * Test the eligibility of the considered Word in learning.
     * @param secondaryIndice the secondary indice of the considered Word
     * @param timestampDiff the number of elapsed seconds since the last time the considered Word has been studied
     * @return true if the considered Word in learning is eligible and false otherwise
     */
    public static boolean learningWordIsEligible(int secondaryIndice, long timestampDiff) {
        int[] levels = {
                   5,
                  20,
                  60,
                4*60,
               15*60,
               60*60,
             4*60*60,
            12*60*60,
            24*60*60,
            48*60*60
        };
        
        if (timestampDiff > levels[secondaryIndice - 1]) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Calculate the combined indice of the considered Word.
     * @param primaryIndice the primary indice of the considered Word
     * @param timestampDiff the number of elapsed seconds since the last time the considered Word has been studied
     * @return the combined indice of the considered Word
     */
    public static int calcCombinedIndice(int primaryIndice, long timestampDiff) {
        int[] levels = {
            180*24*60*60,
             90*24*60*60,
             60*24*60*60,
             45*24*60*60,
             30*24*60*60,
             21*24*60*60,
             14*24*60*60,
             10*24*60*60,
              7*24*60*60,
              4*24*60*60,
        };

        for (int i = 0; i < levels.length; i++) {
            if (timestampDiff > levels[i]) {
                return primaryIndice + i;
            }
        }
        
        return primaryIndice + levels.length;
    }
    
    /**
     * Test the eligibility of the considered known Word.
     * @param primaryIndice the primary indice of the considered Word
     * @param timestampDiff the number of elapsed seconds since the last time the considered Word has been studied
     * @return true if the considered known Word is eligible and false otherwise
     */
    public static boolean knownWordIsEligible(int primaryIndice, long timestampDiff) {
        if (calcCombinedIndice(primaryIndice, timestampDiff) <= MAX_COMBINED_INDICE_ELIGIBLE_WORD) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Retrieve the Pack object corresponding to the current Side (but this side doesn't necessarily belong to this Pack).
     * @return the Pack object corresponding to the current Side
     */
    public Pack retrievePack() {
        if (this.status.getName().equals("learning")) {
            return Pack.findByIdLangAndIndice(this.getLanguage().getId(), this.secondaryIndice);
        } else {
            return null;
        }
    }

    /**
     * Test if the current Side belongs to the corresponding Pack.
     * @return true if the current Side belongs to the corresponding Pack and false otherwise
     */
    public boolean belongsToPack() {
        DatabaseHelper dbh = DatabaseHelper.getInstance(MyApplication.getContext());
        String query;
        Cursor cursor;
        long timestampPack, timestampLastAnswer,
            rawTimestampNow = Now.getInstance().getRawTimestamp();
        int[] levelsForLastAnswer = {
              4,
              5,
             10,
             20,
             40,
             60,
             80,
            100,
            120
        };
        int[] levelsForPack = {
                4,
               10,
               30,
               60,
             4*60,
            15*60,
            30*60,
            45*60,
            60*60
        };
        
        query = "SELECT timestamp_pack, timestamp_last_answer "
              + "FROM pack "
              + "WHERE id_language = " + this.getLanguage().getId() + " "
              + "AND indice = " + this.secondaryIndice;
        
        cursor = dbh.getReadableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        timestampPack = cursor.getLong(0);
        timestampLastAnswer = cursor.getLong(1);
        
        if (((rawTimestampNow - timestampLastAnswer) <= levelsForLastAnswer[this.secondaryIndice - 2])
            && ((rawTimestampNow - timestampPack) <= levelsForPack[this.secondaryIndice - 2])
        ) {
            return true;
        } else {
            return false;
        }
    }
}
