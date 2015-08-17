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
import vincent.moulin.vocab.constants.Constants;
import vincent.moulin.vocab.helpers.DatabaseHelper;
import vincent.moulin.vocab.utilities.EnumDataItem;

/**
 * The Frequency class represents a frequency.
 * 
 * @author Vincent MOULIN
 */
public final class Frequency extends EnumDataItem
{
    public Frequency(int id, String name) {
        super(id, name);
    }

    /**
     * Get from the database the Frequency object whose id is "id".
     * @param id the id of the Frequency we want to get
     * @return the Frequency object whose id is "id"
     */
    public static Frequency getById(int id) {
        DatabaseHelper dbh = DatabaseHelper.getInstance(MyApplication.getContext());
        String query;
        Cursor cursor;
        Frequency retour;
        
        query = "SELECT name "
              + "FROM frequency "
              + "WHERE id = " + id;
        
        cursor = dbh.getReadableDatabase().rawQuery(query, null);
        
        if (cursor.getCount() == 0) {
            retour = null;
        } else {
            cursor.moveToFirst();
            
            retour = new Frequency(
                id,
                cursor.getString(0)
            );
        }

        cursor.close();

        return retour;
    }
    
    public static Frequency getByName(String name) {
        return getById(Constants.FREQUENCIES.getId(name));
    }
}
