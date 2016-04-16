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

package vincent.moulin.vocab.helpers;

import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;

import vincent.moulin.vocab.R;
import vincent.moulin.vocab.entities.Word;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

/**
 * The DatabaseHelper class
 * 
 * @author Vincent MOULIN
 */
public final class DatabaseHelper extends SQLiteOpenHelper
{
    private static DatabaseHelper instance = null;
    
    private static final String DATABASE_NAME = "doctor_vocab";
    private static final int DATABASE_VERSION = 8;
    
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
        int idPack = 1, idStatSnap = 1;
        String query, id = "", name = "";
        SQLiteStatement sqliteStatement;
        HashMap<String, String>
            languages    = new HashMap<String, String>(),
            statuses     = new HashMap<String, String>(),
            frequencies  = new HashMap<String, String>();
        
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
                        id = xpp.nextText();
                        sqliteStatement.bindString(1, id);
                    } else if (xpp.getName().equals("name")) {
                        name = xpp.nextText();
                        sqliteStatement.bindString(2, name);
                    }
                }
                
                if ((xpp.getEventType() == XmlPullParser.END_TAG)
                    && (xpp.getName().equals("language"))
                ) {
                    sqliteStatement.executeInsert();
                    languages.put(name, id);
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
                        id = xpp.nextText();
                        sqliteStatement.bindString(1, id);
                    } else if (xpp.getName().equals("name")) {
                        name = xpp.nextText();
                        sqliteStatement.bindString(2, name);
                    } else if (xpp.getName().equals("color")) {
                        sqliteStatement.bindString(3, xpp.nextText());
                    }
                }
                
                if ((xpp.getEventType() == XmlPullParser.END_TAG)
                    && (xpp.getName().equals("status"))
                ) {
                    sqliteStatement.executeInsert();
                    statuses.put(name, id);
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
                        id = xpp.nextText();
                        sqliteStatement.bindString(1, id);
                    } else if (xpp.getName().equals("name")) {
                        name = xpp.nextText();
                        sqliteStatement.bindString(2, name);
                    }
                }
                
                if ((xpp.getEventType() == XmlPullParser.END_TAG)
                    && (xpp.getName().equals("frequency"))
                ) {
                    sqliteStatement.executeInsert();
                    frequencies.put(name, id);
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
                  +     statuses.get("initial") + ","
                  +     statuses.get("initial") + ","
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
        for (String idLanguage : languages.values()) {
            for (int i = 2; i <= Word.MAX_SECONDARY_INDICE; i++) {
                contentValues = new ContentValues();
                contentValues.put("id", idPack);
                contentValues.put("id_language", idLanguage);
                contentValues.put("indice", i);
                contentValues.put("timestamp_pack", 0);
                contentValues.put("timestamp_last_answer", 0);
                db.insert("pack", null, contentValues);
                
                idPack++;
            }
        }
        //END: Filling the "pack" table
        
        // Filling the "stat_snap" table
        for (String idFrequency : frequencies.values()) {
            for (String idStatus : statuses.values()) {
                for (String idLanguage : languages.values()) {
                    contentValues = new ContentValues();
                    contentValues.put("id", idStatSnap);
                    contentValues.put("id_frequency", idFrequency);
                    contentValues.put("id_status", idStatus);
                    contentValues.put("id_language", idLanguage);
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
        if (oldVersion < 7) {
            db.execSQL("DROP TABLE IF EXISTS dico;");
            db.execSQL("DROP TABLE IF EXISTS stat_snap;");
            db.execSQL("DROP TABLE IF EXISTS pack;");
            db.execSQL("DROP TABLE IF EXISTS dicotuple;");
            db.execSQL("DROP TABLE IF EXISTS card;");
            db.execSQL("DROP TABLE IF EXISTS frequency;");
            db.execSQL("DROP TABLE IF EXISTS status;");
            db.execSQL("DROP TABLE IF EXISTS language;");
            
            onCreate(db);
        } else {
            ContentValues contentValues;
            String idCard = "";
            
            //--------------------------------------------------------------------
            
            contentValues = new ContentValues();
            contentValues.put("is_active_english",  0);
            contentValues.put("is_active_french",   0);
            db.update("card", contentValues, null, null);
            
            //--------------------------------------------------------------------

            try {
                XmlPullParser xpp = this.context.getResources().getXml(R.xml.db_card);

                contentValues = new ContentValues();
                while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                    if (xpp.getEventType() == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("e1")) {
                            idCard = xpp.nextText();
                        } else if (xpp.getName().equals("e2")) {
                            contentValues.put("word_english", xpp.nextText());
                        } else if (xpp.getName().equals("e3")) {
                            contentValues.put("word_french", xpp.nextText());
                        } else if (xpp.getName().equals("e4")) {
                            contentValues.put("is_active_english", xpp.nextText());
                        } else if (xpp.getName().equals("e5")) {
                            contentValues.put("is_active_french", xpp.nextText());
                        }
                    }
                    
                    if ((xpp.getEventType() == XmlPullParser.END_TAG)
                        && (xpp.getName().equals("card"))
                    ) {
                        if (db.update("card", contentValues, "id = ?", new String[]{idCard}) == 0) {
                            contentValues.put("id",                             idCard);
                            contentValues.put("id_status_english",              1);
                            contentValues.put("id_status_french",               1);
                            contentValues.put("is_accelerated_english",         1);
                            contentValues.put("is_accelerated_french",          1);
                            contentValues.put("primary_indice_english",         1);
                            contentValues.put("primary_indice_french",          1);
                            contentValues.put("secondary_indice_english",       1);
                            contentValues.put("secondary_indice_french",        1);
                            contentValues.put("timestamp_last_answer_english",  0);
                            contentValues.put("timestamp_last_answer_french",   0);
                            
                            db.insert("card", null, contentValues);
                        }

                        contentValues = new ContentValues();
                    }
                    
                    xpp.next();
                }
                
                xpp = null;
            } catch (Exception e) {
                this.displayInitializationErrorMsg();
            }
            
            //--------------------------------------------------------------------

            db.delete("card", "is_active_english = 0 AND is_active_french = 0", null);
        }
    }
}
