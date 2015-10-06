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

package vincent.moulin.vocab.libraries.enumdata;

import vincent.moulin.vocab.entities.Frequency;
import vincent.moulin.vocab.entities.Language;
import vincent.moulin.vocab.entities.Status;
import android.database.Cursor;

/**
 * The EnumDataFactory class represents the factory for the enumerated data.
 * 
 * @author Vincent MOULIN
 */
public abstract class EnumDataFactory
{
    /**
     * Create the EnumDataItem object according to the given parameters.
     * @param tableName the table name corresponding to the EnumDataItem object that has to be created
     * @param cursor the result set needed to create the EnumDataItem object
     * @return the EnumDataItem object
     */
    public static EnumDataItem create(String tableName, Cursor cursor) {
        if (tableName.equals("language")) {
            return new Language(cursor.getInt(0), cursor.getString(1));
        } else if (tableName.equals("frequency")) {
            return new Frequency(cursor.getInt(0), cursor.getString(1));
        } else if (tableName.equals("status")) {
            return new Status(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
        }
        
        return null;
    }
}
