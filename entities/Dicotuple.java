package vincent.moulin.vocab.entities;

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
     * Gère la réponse de l'utilisateur.
     * Effectue la mise à jour de la base de données uniquement (pas de l'objet).
     * @param languageOfWordToTranslate la langue du mot à traduire
     * @param answerIsOk true si la réponse est ok et false sinon
     * @param dbh la connexion à la base de données
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
     * Récupère de la base de données l'objet complet dicotuple dont l'id est idDicotuple.
     * @param idDicotuple l'id du dicotuple qu'on veut récupérer
     * @param dbh la connexion à la base de données
     * @return l'objet complet dicotuple dont l'id est idDicotuple
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

}
