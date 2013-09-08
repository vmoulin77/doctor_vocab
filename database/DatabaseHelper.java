package vincent.moulin.vocab.database;

import org.xmlpull.v1.XmlPullParser;
import vincent.moulin.vocab.R;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    
    private static final String DATABASE_NAME = "doctor_vocab";
    private static final int DATABASE_VERSION = 1;
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
        
        ContentValues cv = new ContentValues();
        
        try {
            XmlPullParser xpp = this.context.getResources().getXml(R.xml.doctor_vocab);
            
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("id_dicotuple")) {
                        cv.put("id_dicotuple", xpp.nextText());
                    }
                    if (xpp.getName().equals("type")) {
                        cv.put("type", xpp.nextText());
                    }
                    if (xpp.getName().equals("word_english")) {
                        cv.put("word_english", xpp.nextText());
                    }
                    if (xpp.getName().equals("word_french")) {
                        cv.put("word_french", xpp.nextText());
                    }
                    if (xpp.getName().equals("mode_english")) {
                        cv.put("mode_english", xpp.nextText());
                    }
                    if (xpp.getName().equals("mode_french")) {
                        cv.put("mode_french", xpp.nextText());
                    }
                    if (xpp.getName().equals("is_accelerated_english")) {
                        cv.put("is_accelerated_english", xpp.nextText());
                    }
                    if (xpp.getName().equals("is_accelerated_french")) {
                        cv.put("is_accelerated_french", xpp.nextText());
                    }
                    if (xpp.getName().equals("indice_english_primary")) {
                        cv.put("indice_english_primary", xpp.nextText());
                    }
                    if (xpp.getName().equals("indice_english_secondary")) {
                        cv.put("indice_english_secondary", xpp.nextText());
                    }
                    if (xpp.getName().equals("indice_french_primary")) {
                        cv.put("indice_french_primary", xpp.nextText());
                    }
                    if (xpp.getName().equals("indice_french_secondary")) {
                        cv.put("indice_french_secondary", xpp.nextText());
                    }
                    if (xpp.getName().equals("timestamp_last_answer_english")) {
                        cv.put("timestamp_last_answer_english", xpp.nextText());
                    }
                    if (xpp.getName().equals("timestamp_last_answer_french")) {
                        cv.put("timestamp_last_answer_french", xpp.nextText());
                    }
                }
                
                if (xpp.getEventType() == XmlPullParser.END_TAG) {
                    if (xpp.getName().equals("dicotuple")) {
                        db.insert("dico", "type", cv);
                    }
                }
                
                xpp.next();
            }
        } catch (Throwable t) {
            
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
