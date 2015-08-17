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
 * The Language class represents a language.
 * 
 * @author Vincent MOULIN
 */
public final class Language extends EnumDataItem
{
    public Language(int id, String name) {
        super(id, name);
    }

    /**
     * Get from the database the Language object whose id is "id".
     * @param id the id of the Language we want to get
     * @return the Language object whose id is "id"
     */
    public static Language getById(int id) {
        DatabaseHelper dbh = DatabaseHelper.getInstance(MyApplication.getContext());
        String query;
        Cursor cursor;
        Language retour;
        
        query = "SELECT name "
              + "FROM language "
              + "WHERE id = " + id;
        
        cursor = dbh.getReadableDatabase().rawQuery(query, null);
        
        if (cursor.getCount() == 0) {
            retour = null;
        } else {
            cursor.moveToFirst();
            
            retour = new Language(
                id,
                cursor.getString(0)
            );
        }

        cursor.close();

        return retour;
    }
    
    public static Language getByName(String name) {
        return getById(Constants.LANGUAGES.getId(name));
    }
}
