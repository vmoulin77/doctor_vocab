/**
 * Copyright 2013, 2015 Vincent MOULIN
 * 
 * This file is part of Doctor Vocab.
 * 
 * Doctor Vocab is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package vincent.moulin.vocab.entities;

import android.database.Cursor;
import vincent.moulin.vocab.MyApplication;
import vincent.moulin.vocab.constants.ConstantsHM;
import vincent.moulin.vocab.helpers.DatabaseHelper;
import vincent.moulin.vocab.utilities.EnumDataItem;

/**
 * The Status class represents a word status.
 * 
 * @author Vincent MOULIN
 */
public final class Status extends EnumDataItem implements Cloneable
{
    private String color;
    
    public Status(int id, String name, String color) {
        super(id, name);
        this.color = color;
    }

    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    
    public Status clone() throws CloneNotSupportedException {
        return (Status) super.clone();
    }
    
    /**
     * Get from the database the Status object whose id is "id".
     * @param id the id of the Status we want to get
     * @return the Status object whose id is "id"
     */
    public static Status getById(int id) {
        DatabaseHelper dbh = DatabaseHelper.getInstance(MyApplication.getContext());
        String query;
        Cursor cursor;
        Status retour;
        
        query = "SELECT name, color "
              + "FROM status "
              + "WHERE id = " + id;
        
        cursor = dbh.getReadableDatabase().rawQuery(query, null);
        
        if (cursor.getCount() == 0) {
            retour = null;
        } else {
            cursor.moveToFirst();
            
            retour = new Status(
                id,
                cursor.getString(0),
                cursor.getString(1)
            );
        }

        cursor.close();

        return retour;
    }
    
    public static Status getByName(String name) {
        return getById(ConstantsHM.STATUSES.getId(name));
    }
}
