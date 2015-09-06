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

package vincent.moulin.vocab.helpers;

import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.database.sqlite.SQLiteOpenHelper;
import vincent.moulin.vocab.R;
import vincent.moulin.vocab.constants.ConstantsHM;
import vincent.moulin.vocab.entities.Word;

/**
 * The DatabaseHelper class
 * 
 * @author Vincent MOULIN
 */
public final class DatabaseHelper extends SQLiteOpenHelper
{
    private static DatabaseHelper instance = null;
    
    private static final String DATABASE_NAME = "doctor_vocab";
    private static final int DATABASE_VERSION = 4;
    
    private static final String CREATE_TABLE_LANGUAGE =
        "CREATE TABLE language ("
        +     "id INTEGER PRIMARY KEY,"
        +     "name TEXT"
        + ")";
    
    private static final String CREATE_TABLE_STATUS =
        "CREATE TABLE status ("
        +     "id INTEGER PRIMARY KEY,"
        +     "name TEXT,"
        +     "color TEXT"
        + ")";
    
    private static final String CREATE_TABLE_FREQUENCY =
        "CREATE TABLE frequency ("
        +     "id INTEGER PRIMARY KEY,"
        +     "name TEXT"
        + ")";
    
    private static final String CREATE_TABLE_CARD =
        "CREATE TABLE card ("
        +     "id INTEGER PRIMARY KEY,"
        +     "word_english TEXT,"
        +     "word_french TEXT,"
        +     "is_active_english INTEGER,"
        +     "is_active_french INTEGER,"
        +     "id_status_english INTEGER,"
        +     "id_status_french INTEGER,"
        +     "is_accelerated_english INTEGER,"
        +     "is_accelerated_french INTEGER,"
        +     "primary_indice_english INTEGER,"
        +     "primary_indice_french INTEGER,"
        +     "secondary_indice_english INTEGER,"
        +     "secondary_indice_french INTEGER,"
        +     "timestamp_last_answer_english INTEGER,"
        +     "timestamp_last_answer_french INTEGER,"
        + "FOREIGN KEY(id_status_english) REFERENCES status(id),"
        + "FOREIGN KEY(id_status_french) REFERENCES status(id)"
        + ")";

    private static final String CREATE_TABLE_PACK =
        "CREATE TABLE pack ("
        +     "id INTEGER PRIMARY KEY,"
        +     "id_language INTEGER,"
        +     "indice INTEGER,"
        +     "timestamp_pack INTEGER,"
        +     "timestamp_last_answer INTEGER,"
        + "FOREIGN KEY(id_language) REFERENCES language(id)"
        + ")";
    
    private static final String CREATE_TABLE_STAT_SNAP =
        "CREATE TABLE stat_snap ("
        +     "id INTEGER PRIMARY KEY,"
        +     "id_frequency INTEGER,"
        +     "id_status INTEGER,"
        +     "id_language INTEGER,"
        +     "validity_period INTEGER,"
        +     "nb_words INTEGER,"
        + "FOREIGN KEY(id_frequency) REFERENCES frequency(id),"
        + "FOREIGN KEY(id_status) REFERENCES status(id),"
        + "FOREIGN KEY(id_language) REFERENCES language(id)"
        + ")";
    
    private Context context = null;
    
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    
    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        
        return instance;
    }
    
    private void displayInitializationErrorMsg() {
        new AlertDialog.Builder(this.context)
            .setMessage(R.string.initialization_error_msg)
            .setNeutralButton(R.string.closure_button_content, null)
            .show();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        int idPack, idStatSnap = 1;
        String query;
        SQLiteStatement sqliteStatement;
        
        db.execSQL(CREATE_TABLE_LANGUAGE);
        db.execSQL(CREATE_TABLE_STATUS);
        db.execSQL(CREATE_TABLE_FREQUENCY);
        db.execSQL(CREATE_TABLE_CARD);
        db.execSQL(CREATE_TABLE_PACK);
        db.execSQL(CREATE_TABLE_STAT_SNAP);
        
        // Filling the "language" table
        try {
            XmlPullParser xpp = this.context.getResources().getXml(R.xml.db_language);
            
            query = "INSERT INTO language ("
                  +     "id,"
                  +     "name"
                  + ") VALUES ("
                  +     "?,"
                  +     "?"
                  + ")";
            sqliteStatement = db.compileStatement(query);
            
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("id")) {
                        sqliteStatement.bindString(1, xpp.nextText());
                    } else if (xpp.getName().equals("name")) {
                        sqliteStatement.bindString(2, xpp.nextText());
                    }
                }
                
                if ((xpp.getEventType() == XmlPullParser.END_TAG)
                    && (xpp.getName().equals("language"))
                ) {
                    sqliteStatement.executeInsert();
                }
                
                xpp.next();
            }
            
            sqliteStatement.close();
            xpp = null;
        } catch (Exception e) {
            this.displayInitializationErrorMsg();
        }
        //END: Filling the "language" table
        
        // Filling the "status" table
        try {
            XmlPullParser xpp = this.context.getResources().getXml(R.xml.db_status);
            
            query = "INSERT INTO status ("
                  +     "id,"
                  +     "name,"
                  +     "color"
                  + ") VALUES ("
                  +     "?,"
                  +     "?,"
                  +     "?"
                  + ")";
            sqliteStatement = db.compileStatement(query);
            
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("id")) {
                        sqliteStatement.bindString(1, xpp.nextText());
                    } else if (xpp.getName().equals("name")) {
                        sqliteStatement.bindString(2, xpp.nextText());
                    } else if (xpp.getName().equals("color")) {
                        sqliteStatement.bindString(3, xpp.nextText());
                    }
                }
                
                if ((xpp.getEventType() == XmlPullParser.END_TAG)
                    && (xpp.getName().equals("status"))
                ) {
                    sqliteStatement.executeInsert();
                }
                
                xpp.next();
            }
            
            sqliteStatement.close();
            xpp = null;
        } catch (Exception e) {
            this.displayInitializationErrorMsg();
        }
        //END: Filling the "status" table
        
        // Filling the "frequency" table
        try {
            XmlPullParser xpp = this.context.getResources().getXml(R.xml.db_frequency);
            
            query = "INSERT INTO frequency ("
                  +     "id,"
                  +     "name"
                  + ") VALUES ("
                  +     "?,"
                  +     "?"
                  + ")";
            sqliteStatement = db.compileStatement(query);
            
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("id")) {
                        sqliteStatement.bindString(1, xpp.nextText());
                    } else if (xpp.getName().equals("name")) {
                        sqliteStatement.bindString(2, xpp.nextText());
                    }
                }
                
                if ((xpp.getEventType() == XmlPullParser.END_TAG)
                    && (xpp.getName().equals("frequency"))
                ) {
                    sqliteStatement.executeInsert();
                }
                
                xpp.next();
            }
            
            sqliteStatement.close();
            xpp = null;
        } catch (Exception e) {
            this.displayInitializationErrorMsg();
        }
        //END: Filling the "frequency" table
        
        // Filling the "card" table
        try {
            XmlPullParser xpp = this.context.getResources().getXml(R.xml.db_card);
            
            query = "INSERT INTO card ("
                  +     "id,"
                  +     "word_english,"
                  +     "word_french,"
                  +     "is_active_english,"
                  +     "is_active_french,"
                  +     "id_status_english,"
                  +     "id_status_french,"
                  +     "is_accelerated_english,"
                  +     "is_accelerated_french,"
                  +     "primary_indice_english,"
                  +     "primary_indice_french,"
                  +     "secondary_indice_english,"
                  +     "secondary_indice_french,"
                  +     "timestamp_last_answer_english,"
                  +     "timestamp_last_answer_french"
                  + ") VALUES ("
                  +     "?,"
                  +     "?,"
                  +     "?,"
                  +     "?,"
                  +     "?,"
                  +     ConstantsHM.STATUSES.getId("initial") + ","
                  +     ConstantsHM.STATUSES.getId("initial") + ","
                  +     "1,"
                  +     "1,"
                  +     "1,"
                  +     "1,"
                  +     "1,"
                  +     "1,"
                  +     "0,"
                  +     "0"
                  + ")";
            sqliteStatement = db.compileStatement(query);
            
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("e1")) {
                        sqliteStatement.bindString(1, xpp.nextText());
                    } else if (xpp.getName().equals("e2")) {
                        sqliteStatement.bindString(2, xpp.nextText());
                    } else if (xpp.getName().equals("e3")) {
                        sqliteStatement.bindString(3, xpp.nextText());
                    } else if (xpp.getName().equals("e4")) {
                        sqliteStatement.bindString(4, xpp.nextText());
                    } else if (xpp.getName().equals("e5")) {
                        sqliteStatement.bindString(5, xpp.nextText());
                    }
                }
                
                if ((xpp.getEventType() == XmlPullParser.END_TAG)
                    && (xpp.getName().equals("card"))
                ) {
                    sqliteStatement.executeInsert();
                }
                
                xpp.next();
            }
            
            sqliteStatement.close();
            xpp = null;
        } catch (Exception e) {
            this.displayInitializationErrorMsg();
        }
        //END: Filling the "card" table

        // Filling the "pack" table
        for (Map.Entry<String, Integer> languageEntry : ConstantsHM.LANGUAGES.entrySet()) {
            for (int i = 2; i <= Word.MAX_SECONDARY_INDICE; i++) {
                idPack = (languageEntry.getValue() * Word.MAX_SECONDARY_INDICE) + i - 1 - languageEntry.getValue();
                
                contentValues = new ContentValues();
                contentValues.put("id", idPack);
                contentValues.put("id_language", languageEntry.getValue());
                contentValues.put("indice", i);
                contentValues.put("timestamp_pack", 0);
                contentValues.put("timestamp_last_answer", 0);
                db.insert("pack", null, contentValues);
            }
        }
        //END: Filling the "pack" table
        
        // Filling the "stat_snap" table
        for (Map.Entry<String, Integer> frequencyEntry : ConstantsHM.FREQUENCIES.entrySet()) {
            for (Map.Entry<String, Integer> statusEntry : ConstantsHM.STATUSES.entrySet()) {
                for (Map.Entry<String, Integer> languageEntry : ConstantsHM.LANGUAGES.entrySet()) {
                    contentValues = new ContentValues();
                    contentValues.put("id", idStatSnap);
                    contentValues.put("id_frequency", frequencyEntry.getValue());
                    contentValues.put("id_status", statusEntry.getValue());
                    contentValues.put("id_language", languageEntry.getValue());
                    contentValues.put("validity_period", 0);
                    contentValues.put("nb_words", 0);
                    db.insert("stat_snap", null, contentValues);
                    
                    idStatSnap++;
                }
            }
        }
        //END: Filling the "stat_snap" table
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query;
        Cursor cursor;
        int cursorPosition;
        ContentValues contentValues;
        
        if (oldVersion == 2) {
            db.execSQL("DROP TABLE IF EXISTS dico;");
            onCreate(db);
        } else if (oldVersion == 3) {
            String[][] userdata = new String[5476][11];
            
            // Save the user data
            query = "SELECT "
                  +     "id, " //0
                  +     "id_status_english, " //1
                  +     "id_status_french, " //2
                  +     "is_accelerated_english, " //3
                  +     "is_accelerated_french, " //4
                  +     "primary_indice_english, " //5
                  +     "primary_indice_french, " //6
                  +     "secondary_indice_english, " //7
                  +     "secondary_indice_french, " //8
                  +     "timestamp_last_answer_english, " //9
                  +     "timestamp_last_answer_french " //10
                  + "FROM dicotuple "
                  + "WHERE timestamp_last_answer_english <> 0 "
                  + "OR timestamp_last_answer_french <> 0";
            
            cursor = db.rawQuery(query, null);
            
            while (cursor.moveToNext()) {
                cursorPosition = cursor.getPosition();
                for (int i = 0; i <= 10; i++) {
                    userdata[cursorPosition][i] = cursor.getString(i);
                }
            }
            cursor.close();
            //END: Save the user data
            
            // Recreate the database
            db.execSQL("DROP TABLE IF EXISTS dicotuple;");
            onCreate(db);
            //END: Recreate the database
            
            // Inject the user data in the database
            for (String[] userdataItem : userdata) {
                if (userdataItem[0] != null) {
                    contentValues = new ContentValues();
                    
                    contentValues.put("id_status_english",              userdataItem[1]);
                    contentValues.put("id_status_french",               userdataItem[2]);
                    contentValues.put("is_accelerated_english",         userdataItem[3]);
                    contentValues.put("is_accelerated_french",          userdataItem[4]);
                    contentValues.put("primary_indice_english",         userdataItem[5]);
                    contentValues.put("primary_indice_french",          userdataItem[6]);
                    contentValues.put("secondary_indice_english",       userdataItem[7]);
                    contentValues.put("secondary_indice_french",        userdataItem[8]);
                    contentValues.put("timestamp_last_answer_english",  userdataItem[9]);
                    contentValues.put("timestamp_last_answer_french",   userdataItem[10]);
                    
                    db.update("card", contentValues, "id = ?", new String[]{userdataItem[0]});
                }
            }
            //END: Inject the user data in the database
        }
    }
}
