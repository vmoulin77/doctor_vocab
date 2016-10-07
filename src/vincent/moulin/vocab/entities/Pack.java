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
 * The Pack class represents a pack of sides.
 * The goal of this class is to avoid to display some series of words always in the same order.
 * 
 * @author Vincent MOULIN
 */
public class Pack
{
    private int id;
    private Language language;
    private int indice;
    private long timestampPack;
    private long timestampLastAnswer;
    
    private Side[] sides;
    
    public Pack(int id, Language language, int indice, long timestampPack, long timestampLastAnswer) {
        this.id = id;
        this.language = language;
        this.indice = indice;
        this.timestampPack = timestampPack;
        this.timestampLastAnswer = timestampLastAnswer;
    }
    
    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    public Language getLanguage() {
        return this.language;
    }
    public void setLanguage(Language language) {
        this.language = language;
    }
    public void setLanguage(int idLanguage) {
        this.language = Language.find(idLanguage);
    }
    public void setLanguage(String languageName) {
        this.language = Language.findByName(languageName);
    }
    
    public int getIndice() {
        return this.indice;
    }
    public void setIndice(int indice) {
        this.indice = indice;
    }
    
    public long getTimestampPack() {
        return this.timestampPack;
    }
    public void setTimestampPack(long timestampPack) {
        this.timestampPack = timestampPack;
    }
    
    public long getTimestampLastAnswer() {
        return this.timestampLastAnswer;
    }
    public void setTimestampLastAnswer(long timestampLastAnswer) {
        this.timestampLastAnswer = timestampLastAnswer;
    }
    
    public Side[] getSides() {
        return this.sides;
    }
    public void setSides(Side[] sides) {
        this.sides = sides;
    }
    
    /**
     * Find the Pack whose Language id is "idLang" and indice is "indice".
     * @param idLang the Language id
     * @param indice the indice
     * @return the Pack whose Language id is "idLang" and indice is "indice"
     */
    public static Pack findByIdLangAndIndice(int idLang, int indice) {
        DatabaseHelper dbh = DatabaseHelper.getInstance(MyApplication.getContext());
        String query;
        Cursor cursor;
        Pack retour;
        
        query = "SELECT "
              +     "id, " //0
              +     "timestamp_pack, " //1
              +     "timestamp_last_answer " //2
              + "FROM pack "
              + "WHERE id_language = " + idLang + " "
              + "AND indice = " + indice;
        
        cursor = dbh.getReadableDatabase().rawQuery(query, null);
        
        if (cursor.getCount() == 0) {
            retour = null;
        } else {
            cursor.moveToFirst();
            
            retour = new Pack(
                cursor.getInt(0),
                Language.find(idLang),
                indice,
                cursor.getLong(1),
                cursor.getLong(2)
            );
        }
        
        cursor.close();
        
        return retour;
    }
    
    /**
     * Update the current Pack in the database.
     * @return the number of rows affected
     */
    public int save() {
        DatabaseHelper dbh = DatabaseHelper.getInstance(MyApplication.getContext());
        ContentValues contentValues = new ContentValues();
        
        contentValues.put("id_language", this.language.getId());
        contentValues.put("indice", this.indice);
        contentValues.put("timestamp_pack", this.timestampPack);
        contentValues.put("timestamp_last_answer", this.timestampLastAnswer);
        
        return dbh.getWritableDatabase().update("pack", contentValues, "id = ?", new String[]{String.valueOf(this.id)});
    }
}
