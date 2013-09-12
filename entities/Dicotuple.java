package vincent.moulin.vocab.entities;

import android.content.ContentValues;
import android.database.Cursor;
import vincent.moulin.vocab.Constantes;
import vincent.moulin.vocab.database.DatabaseHelper;

public class Dicotuple {
    private int idDicotuple;
    private int type;
    private WordEnglish wordEnglish;
    private WordFrench wordFrench;
    
    public Dicotuple(int idDicotuple, int type, WordEnglish wordEnglish, WordFrench wordFrench) {
        this.idDicotuple = idDicotuple;
        this.type = type;
        this.wordEnglish = wordEnglish;
        this.wordFrench = wordFrench;
    }
    
    public int getIdDicotuple() {
        return idDicotuple;
    }
    public void setIdDicotuple(int idDicotuple) {
        this.idDicotuple = idDicotuple;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public WordEnglish getWordEnglish() {
        return wordEnglish;
    }
    public void setWordEnglish(WordEnglish wordEnglish) {
        this.wordEnglish = wordEnglish;
    }
    public WordFrench getWordFrench() {
        return wordFrench;
    }
    public void setWordFrench(WordFrench wordFrench) {
        this.wordFrench = wordFrench;
    }
    
    /**
     * Manage the answer of the user.
     * Update the database only (not the object).
     * @param languageOfWordToTranslate the language of the word to translate
     * @param answerIsOk true if the answer is ok and false otherwise
     * @param dbh the connection to the database
     */
    public void manageAnswer(String languageOfWordToTranslate, boolean answerIsOk, DatabaseHelper dbh) {
        String query;
        Word wordToTranslate;
        int newMode, newIndicePrimary, newIndiceSecondary;
        
        if (languageOfWordToTranslate.equals("english")) {
            wordToTranslate = this.wordEnglish;
        } else {
            wordToTranslate = this.wordFrench;
        }
        
        //Calcul du nouveau mode, du nouvel indice primaire et du nouvel indice secondaire
        if (wordToTranslate.getMode() == Constantes.MODE_NEVER_ANSWERED) {
            if (answerIsOk) {
                newMode = Constantes.MODE_KNOWN;
                newIndicePrimary = 2;
                newIndiceSecondary = 0;
            } else {
                newMode = Constantes.MODE_LEARNING_IN_PROGRESS;
                newIndicePrimary = 0;
                newIndiceSecondary = 0;
            }
        } else if (wordToTranslate.getMode() == Constantes.MODE_KNOWN) {
            if (answerIsOk) {
                newMode = Constantes.MODE_KNOWN;
                if (wordToTranslate.getIndicePrimary() == Constantes.MAX_INDICE_PRIMARY) {
                    newIndicePrimary = Constantes.MAX_INDICE_PRIMARY;
                } else {
                    if (wordToTranslate.isAccelerated()) {
                        if (wordToTranslate.getIndicePrimary() == 2) {
                            newIndicePrimary = 5;
                        } else {
                            newIndicePrimary = Constantes.MAX_INDICE_PRIMARY;
                        }
                    } else {
                        newIndicePrimary = wordToTranslate.getIndicePrimary() + 1;
                    }
                }
                newIndiceSecondary = 0;
            } else {
                newMode = Constantes.MODE_LEARNING_IN_PROGRESS;
                if (wordToTranslate.getIndicePrimary() == 0) {
                    newIndicePrimary = 0;
                } else {
                    newIndicePrimary = wordToTranslate.getIndicePrimary() - 1;
                }
                newIndiceSecondary = 0;
            }
        } else {
            if (answerIsOk) {
                newIndiceSecondary = wordToTranslate.getIndiceSecondary() + 1;
                if (newIndiceSecondary == Constantes.MAX_INDICE_SECONDARY) {
                    newMode = Constantes.MODE_KNOWN;
                    newIndicePrimary = wordToTranslate.getIndicePrimary() + 1;
                    newIndiceSecondary = 0;
                } else {
                    newMode = Constantes.MODE_LEARNING_IN_PROGRESS;
                    newIndicePrimary = wordToTranslate.getIndicePrimary();
                    newIndiceSecondary = wordToTranslate.getIndiceSecondary() + 1;
                }
            } else {
                newMode = Constantes.MODE_LEARNING_IN_PROGRESS;
                newIndicePrimary = wordToTranslate.getIndicePrimary();
                newIndiceSecondary = 0;
            }
        }
        //--------------------------------------------------------------------
        
        //Construction de la requête
        query = "UPDATE dico "
                + "SET timestamp_last_answer_" + languageOfWordToTranslate + " = strftime('%s', 'now'), ";
        
        if ( ! answerIsOk) {
            query += "is_accelerated_" + languageOfWordToTranslate + " = 0, ";
        }
        
        query += "mode_" + languageOfWordToTranslate + " = " + newMode + ", ";
        query += "indice_" + languageOfWordToTranslate + "_primary" + " = " + newIndicePrimary + ", ";
        query += "indice_" + languageOfWordToTranslate + "_secondary" + " = " + newIndiceSecondary;
        
        query += " WHERE id_dicotuple = " + this.idDicotuple;
        //--------------------------------------------------------------
        
        dbh.getWritableDatabase().execSQL(query);
    }
    
    /**
     * Retrieve from the database the full dicotuple object whose id is idDicotuple.
     * @param idDicotuple the id of the dicotuple we want to retrieve
     * @param dbh the connection to the database
     * @return the full dicotuple object whose id is idDicotuple
     */
    public static Dicotuple retrieveDicotupleFromDatabase(int idDicotuple, DatabaseHelper dbh) {
        String query;
        Cursor cursor;
        Dicotuple retour;
        
        query =
            "SELECT "
                + "type, " //0
                + "word_english, " //1
                + "word_french, " //2
                + "mode_english, " //3
                + "mode_french, " //4
                + "is_accelerated_english, " //5
                + "is_accelerated_french, " //6
                + "indice_english_primary, " //7
                + "indice_english_secondary, " //8
                + "indice_french_primary, " //9
                + "indice_french_secondary, " //10
                + "timestamp_last_answer_english, " //11
                + "timestamp_last_answer_french " //12
            + "FROM dico "
            + "WHERE id_dicotuple = " + idDicotuple;
        
        cursor = dbh.getReadableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        retour =
            new Dicotuple(
                idDicotuple,
                cursor.getInt(0),
                new WordEnglish(
                    cursor.getString(1),
                    cursor.getInt(3),
                    (cursor.getInt(5) != 0),
                    cursor.getInt(7),
                    cursor.getInt(8),
                    cursor.getInt(11)
                ),
                new WordFrench(
                    cursor.getString(2),
                    cursor.getInt(4),
                    (cursor.getInt(6) != 0),
                    cursor.getInt(9),
                    cursor.getInt(10),
                    cursor.getInt(12)
                )
            );
        
        cursor.close();
        
        return retour;
    }
    
    /**
     * Update the current dicotuple in the database.
     * @param dbh the connection to the database
     * @return the number of rows affected
     */
    public int updateDicotupleInDatabase(DatabaseHelper dbh) {
        ContentValues contentValues = new ContentValues();
        
        contentValues.put("type", this.type);
        contentValues.put("word_english", this.wordEnglish.getContent());
        contentValues.put("word_french", this.wordFrench.getContent());
        contentValues.put("mode_english", this.wordEnglish.getMode());
        contentValues.put("mode_french", this.wordFrench.getMode());
        if(this.wordEnglish.isAccelerated()) {
            contentValues.put("is_accelerated_english", 1);
        } else {
            contentValues.put("is_accelerated_english", 0);
        }
        if(this.wordFrench.isAccelerated()) {
            contentValues.put("is_accelerated_french", 1);
        } else {
            contentValues.put("is_accelerated_french", 0);
        }
        contentValues.put("indice_english_primary", this.wordEnglish.getIndicePrimary());
        contentValues.put("indice_english_secondary", this.wordEnglish.getIndiceSecondary());
        contentValues.put("indice_french_primary", this.wordFrench.getIndicePrimary());
        contentValues.put("indice_french_secondary", this.wordFrench.getIndiceSecondary());
        contentValues.put("timestamp_last_answer_english", this.wordEnglish.getTimestampLastAnswer());
        contentValues.put("timestamp_last_answer_french", this.wordFrench.getTimestampLastAnswer());
        
        return dbh.getWritableDatabase().update("dico", contentValues, "id_dicotuple = ?", new String[]{String.valueOf(this.idDicotuple)});
    }

}