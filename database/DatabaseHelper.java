package vincent.moulin.vocab.database;

import org.xmlpull.v1.XmlPullParser;

import vincent.moulin.vocab.Constantes;
import vincent.moulin.vocab.R;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    
    private static final String DATABASE_NAME = "doctor_vocab";
    private static final int DATABASE_VERSION = 2;
    private static final String CREATE_SQL = 
            "CREATE TABLE dico ("
            + "id_dicotuple INTEGER PRIMARY KEY,"
            + "type INTEGER,"
            + "word_english TEXT,"
            + "word_french TEXT,"
            + "mode_english INTEGER,"
            + "mode_french INTEGER,"
            + "is_accelerated_english INTEGER,"
            + "is_accelerated_french INTEGER,"
            + "indice_english_primary INTEGER,"
            + "indice_english_secondary INTEGER,"
            + "indice_french_primary INTEGER,"
            + "indice_french_secondary INTEGER,"
            + "timestamp_last_answer_english INTEGER,"
            + "timestamp_last_answer_french INTEGER"
            + ");";
    private Context context = null;
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SQL);
        
        ContentValues contentValues = new ContentValues();
        
        try {
            XmlPullParser xpp = this.context.getResources().getXml(R.xml.doctor_vocab);
            
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("id_dicotuple")) {
                        contentValues.put("id_dicotuple", xpp.nextText());
                    }
                    if (xpp.getName().equals("type")) {
                        contentValues.put("type", xpp.nextText());
                    }
                    if (xpp.getName().equals("word_english")) {
                        contentValues.put("word_english", xpp.nextText());
                    }
                    if (xpp.getName().equals("word_french")) {
                        contentValues.put("word_french", xpp.nextText());
                    }
                    if (xpp.getName().equals("mode_english")) {
                        contentValues.put("mode_english", xpp.nextText());
                    }
                    if (xpp.getName().equals("mode_french")) {
                        contentValues.put("mode_french", xpp.nextText());
                    }
                    if (xpp.getName().equals("is_accelerated_english")) {
                        contentValues.put("is_accelerated_english", xpp.nextText());
                    }
                    if (xpp.getName().equals("is_accelerated_french")) {
                        contentValues.put("is_accelerated_french", xpp.nextText());
                    }
                    if (xpp.getName().equals("indice_english_primary")) {
                        contentValues.put("indice_english_primary", xpp.nextText());
                    }
                    if (xpp.getName().equals("indice_english_secondary")) {
                        contentValues.put("indice_english_secondary", xpp.nextText());
                    }
                    if (xpp.getName().equals("indice_french_primary")) {
                        contentValues.put("indice_french_primary", xpp.nextText());
                    }
                    if (xpp.getName().equals("indice_french_secondary")) {
                        contentValues.put("indice_french_secondary", xpp.nextText());
                    }
                    if (xpp.getName().equals("timestamp_last_answer_english")) {
                        contentValues.put("timestamp_last_answer_english", xpp.nextText());
                    }
                    if (xpp.getName().equals("timestamp_last_answer_french")) {
                        contentValues.put("timestamp_last_answer_french", xpp.nextText());
                    }
                }
                
                if (xpp.getEventType() == XmlPullParser.END_TAG) {
                    if (xpp.getName().equals("dicotuple")) {
                        db.insert("dico", "type", contentValues);
                    }
                }
                
                xpp.next();
            }
        } catch (Throwable t) {
            
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query;
        Cursor cursor;
        ContentValues contentValues;
        int [][] userData = new int [7050][11];
        int i = 0;
        
        if ((oldVersion == 1) && (newVersion == 2)) {
            query = "UPDATE dico "
                    + "SET timestamp_last_answer_english = 0 "
                    + "WHERE mode_english = " + Constantes.MODE_NEVER_ANSWERED;
            db.execSQL(query);
            
            query = "UPDATE dico "
                    + "SET timestamp_last_answer_french = 0 "
                    + "WHERE mode_french = " + Constantes.MODE_NEVER_ANSWERED;
            db.execSQL(query);
            
            query = "SELECT "
                        + "id_dicotuple, " //0
                        + "mode_english, " //1
                        + "mode_french, " //2
                        + "is_accelerated_english, " //3
                        + "is_accelerated_french, " //4
                        + "indice_english_primary, " //5
                        + "indice_english_secondary, " //6
                        + "indice_french_primary, " //7
                        + "indice_french_secondary, " //8
                        + "timestamp_last_answer_english, " //9
                        + "timestamp_last_answer_french " //10
                    + "FROM dico";
            cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                for (int j = 0; j <= 10; j++) {
                    userData[i][j] = cursor.getInt(j);
                }
                
                i++;
            }
            cursor.close();
            
            db.execSQL("DROP TABLE if exists dico");
            onCreate(db);
            
            for (int [] dicotupleUserData : userData) {
                contentValues = new ContentValues();
                
                contentValues.put("mode_english", dicotupleUserData[1]);
                contentValues.put("mode_french", dicotupleUserData[2]);
                contentValues.put("is_accelerated_english", dicotupleUserData[3]);
                contentValues.put("is_accelerated_french", dicotupleUserData[4]);
                contentValues.put("indice_english_primary", dicotupleUserData[5]);
                contentValues.put("indice_english_secondary", dicotupleUserData[6]);
                contentValues.put("indice_french_primary", dicotupleUserData[7]);
                contentValues.put("indice_french_secondary", dicotupleUserData[8]);
                contentValues.put("timestamp_last_answer_english", dicotupleUserData[9]);
                contentValues.put("timestamp_last_answer_french", dicotupleUserData[10]);
                            
                db.update("dico", contentValues, "id_dicotuple = ?", new String[]{String.valueOf(dicotupleUserData[0])});
            }
        }
    }

}
