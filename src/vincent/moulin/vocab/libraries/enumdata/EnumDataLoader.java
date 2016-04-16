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

package vincent.moulin.vocab.libraries.enumdata;

import java.util.HashMap;

import vincent.moulin.vocab.MyApplication;
import vincent.moulin.vocab.helpers.DatabaseHelper;
import android.database.Cursor;
import android.text.TextUtils;

/**
 * The EnumDataLoader class is an accessor to the enumerated data and implements the singleton pattern.
 * If the enumerated data asked for access have not already been loaded from the database, it will first load them before returning them.
 * 
 * @author Vincent MOULIN
 */
public final class EnumDataLoader
{
    private static EnumDataLoader instance = null;
    
    private HashMap<String, EnumDataItem[]> enumData = new HashMap<String, EnumDataItem[]>();
    
    private EnumDataLoader() {}
    
    public static EnumDataLoader getInstance() {
        if (instance == null) {
            instance = new EnumDataLoader();
        }
        
        return instance;
    }
    
    /**
     * Load the enumerated data stored in the table "tableName" (unless they have already been loaded).
     * @param tableName the name of the table
     * @param fields the fields of the table "tableName"
     */
    private void loadEnumData(String tableName, String[] fields) {
        DatabaseHelper dbh = DatabaseHelper.getInstance(MyApplication.getContext());
        String query;
        Cursor cursor;
        EnumDataItem[] enumDataItems;
        int enumDataItemsCounter = 0;

        if ( ! enumData.containsKey(tableName)) {
            query = "SELECT " + TextUtils.join(",", fields) + " "
                  + "FROM " + tableName;
            
            cursor = dbh.getReadableDatabase().rawQuery(query, null);
            enumDataItems = new EnumDataItem[cursor.getCount()];
            while (cursor.moveToNext()) {
                enumDataItems[enumDataItemsCounter] = EnumDataFactory.create(tableName, cursor);
                enumDataItemsCounter++;
            }
            cursor.close();
            
            enumData.put(tableName, enumDataItems);
        }
    }
    
    /**
     * Get the EnumDataItem object corresponding to the row of the table "tableName" where the id field equals "id".
     * @param tableName the name of the table
     * @param fields the fields of the table "tableName"
     * @param id the id of the EnumDataItem object
     * @return the EnumDataItem object corresponding to the row of the table "tableName" where the id field equals "id"
     */
    public EnumDataItem getById(String tableName, String[] fields, int id) {
        loadEnumData(tableName, fields);
        
        for (EnumDataItem enumDataItem : enumData.get(tableName)) {
            if (enumDataItem.getId() == id) {
                return enumDataItem;
            }
        }

        return null;
    }
    
    /**
     * Get the EnumDataItem object corresponding to the row of the table "tableName" where the name field equals "name".
     * @param tableName the name of the table
     * @param fields the fields of the table "tableName"
     * @param name the name of the EnumDataItem object
     * @return the EnumDataItem object corresponding to the row of the table "tableName" where the name field equals "name"
     */
    public EnumDataItem getByName(String tableName, String[] fields, String name) {
        loadEnumData(tableName, fields);
        
        for (EnumDataItem enumDataItem : enumData.get(tableName)) {
            if (enumDataItem.getName().equals(name)) {
                return enumDataItem;
            }
        }
        
        return null;
    }
    
    /**
     * Get all the EnumDataItem objects corresponding to the table "tableName".
     * @param tableName the name of the table
     * @param fields the fields of the table "tableName"
     * @return all the EnumDataItem objects corresponding to the table "tableName"
     */
    public EnumDataItem[] getAll(String tableName, String[] fields) {
        loadEnumData(tableName, fields);
        
        return enumData.get(tableName);
    }
}
