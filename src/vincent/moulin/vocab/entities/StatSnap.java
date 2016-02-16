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

package vincent.moulin.vocab.entities;

import vincent.moulin.vocab.MyApplication;
import vincent.moulin.vocab.constants.Constants;
import vincent.moulin.vocab.helpers.DatabaseHelper;
import vincent.moulin.vocab.helpers.TimeHelper;
import vincent.moulin.vocab.utilities.TimestampNow;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.SparseArray;
import android.util.SparseIntArray;

/**
 * The StatSnap class represents a snapshot of one statistic.
 * 
 * @author Vincent MOULIN
 */
public class StatSnap
{
    private int id;
    private Frequency frequency;
    private Status status;
    private Language language;
    private long validityPeriod;
    private int nbWords;
    
    public StatSnap(int id, Frequency frequency, Status status, Language language, long validityPeriod, int nbWords) {
        this.id = id;
        this.frequency = frequency;
        this.status = status;
        this.language = language;
        this.validityPeriod = validityPeriod;
        this.nbWords = nbWords;
    }
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    public Frequency getFrequency() {
        return frequency;
    }
    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }
    public void setFrequency(int idFrequency) {
        this.frequency = Frequency.getById(idFrequency);
    }
    public void setFrequency(String frequencyName) {
        this.frequency = Frequency.getByName(frequencyName);
    }
    
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    public void setStatus(int idStatus) {
        this.status = Status.getById(idStatus);
    }
    public void setStatus(String statusName) {
        this.status = Status.getByName(statusName);
    }
    
    public Language getLanguage() {
        return language;
    }
    public void setLanguage(Language language) {
        this.language = language;
    }
    public void setLanguage(int idLanguage) {
        this.language = Language.getById(idLanguage);
    }
    public void setLanguage(String languageName) {
        this.language = Language.getByName(languageName);
    }
    
    public long getValidityPeriod() {
        return validityPeriod;
    }
    public void setValidityPeriod(long validityPeriod) {
        this.validityPeriod = validityPeriod;
    }
    
    public int getNbWords() {
        return nbWords;
    }
    public void setNbWords(int nbWords) {
        this.nbWords = nbWords;
    }
    
    /**
     * Update the current StatSnap in the database.
     * @return the number of rows affected
     */
    public int updateStatSnapInDatabase() {
        DatabaseHelper dbh = DatabaseHelper.getInstance(MyApplication.getContext());
        ContentValues contentValues = new ContentValues();
        
        contentValues.put("id_frequency", this.frequency.getId());
        contentValues.put("id_status", this.status.getId());
        contentValues.put("id_language", this.language.getId());
        contentValues.put("validity_period", this.validityPeriod);
        contentValues.put("nb_words", this.nbWords);
        
        return dbh.getWritableDatabase().update("stat_snap", contentValues, "id = ?", new String[]{String.valueOf(this.id)});
    }
    
    /**
     * Get all the StatSnaps for the given "langName".
     * @param langName the language name for which the StatSnaps are got
     * @return the StatSnaps
     */
    public static SparseArray<SparseIntArray> getAllStatSnapsForLangName(String langName) {
        DatabaseHelper dbh = DatabaseHelper.getInstance(MyApplication.getContext());
        String query;
        Cursor cursor;
        SparseArray<SparseIntArray> retour = new SparseArray<SparseIntArray>();

        query = "SELECT "
              +     "id_frequency, " //0
              +     "id_status, " //1
              +     "nb_words " //2
              + "FROM stat_snap "
              + "WHERE id_language = " + Language.getIdOf(langName);
        
        cursor = dbh.getReadableDatabase().rawQuery(query, null);

        for (Frequency frequency : Frequency.all()) {
            retour.put(frequency.getId(), new SparseIntArray());
        }

        while (cursor.moveToNext()) {
            retour.get(cursor.getInt(0)).put(cursor.getInt(1), cursor.getInt(2));
        }
        cursor.close();

        return retour;
    }
    
    /**
     * Update all the StatSnaps in the database.
     */
    public static void updateAllStatSnaps() {
        DatabaseHelper dbh = DatabaseHelper.getInstance(MyApplication.getContext());
        String query, query2, langName;
        Cursor cursor, cursor2;
        int idStatSnap, idFrequency, idStatus, nbWords;
        long validityPeriod, currentPeriod = 0,
            offsettedTimestampNow = TimestampNow.getInstance().getValue(Constants.TIMESTAMP_OFFSETTED_VALUE),
            daystamp = TimeHelper.convertTimestamp(offsettedTimestampNow, Constants.DAYSTAMP),
            weekstamp = TimeHelper.convertTimestamp(offsettedTimestampNow, Constants.WEEKSTAMP),
            monthstamp = TimeHelper.convertTimestamp(offsettedTimestampNow, Constants.MONTHSTAMP);
        ContentValues contentValues;
        
        query = "SELECT "
              +     "id, " //0
              +     "id_frequency, " //1
              +     "id_status, " //2
              +     "id_language, " //3
              +     "validity_period " //4
              + "FROM stat_snap";
        
        cursor = dbh.getReadableDatabase().rawQuery(query, null);
        
        while (cursor.moveToNext()) {
            idStatSnap      = cursor.getInt(0);
            idFrequency     = cursor.getInt(1);
            idStatus        = cursor.getInt(2);
            langName        = Language.getNameOf(cursor.getInt(3));
            validityPeriod  = cursor.getLong(4);

            if (idFrequency == Frequency.getIdOf("daily")) {
                currentPeriod = daystamp;
            } else if (idFrequency == Frequency.getIdOf("weekly")) {
                currentPeriod = weekstamp;
            } else {
                currentPeriod = monthstamp;
            }
            
            if (validityPeriod != currentPeriod) {
                query2 = "SELECT COUNT(*) "
                       + "FROM card "
                       + "WHERE is_active_" + langName + " = 1 "
                       + "AND id_status_" + langName + " = " + idStatus;
                cursor2 = dbh.getReadableDatabase().rawQuery(query2, null);
                cursor2.moveToFirst();
                nbWords = cursor2.getInt(0);
                cursor2.close();
                
                contentValues = new ContentValues();
                contentValues.put("validity_period", currentPeriod);
                contentValues.put("nb_words", nbWords);
                dbh.getWritableDatabase().update("stat_snap", contentValues, "id = ?", new String[]{String.valueOf(idStatSnap)});
            }
        }
        cursor.close();
    }
}
