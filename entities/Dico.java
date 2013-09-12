package vincent.moulin.vocab.entities;

import vincent.moulin.vocab.Constantes;
import vincent.moulin.vocab.database.DatabaseHelper;
import android.database.Cursor;
import java.util.Random;
import java.util.Calendar;
import java.util.HashMap;

public class Dico {
    private Dicotuple dicotuples[];

    public Dicotuple[] getDicotuples() {
        return dicotuples;
    }

    public void setDicotuples(Dicotuple[] dicotuples) {
        this.dicotuples = dicotuples;
    }
    
    /**
     * Calculate the statistics.
     * @param dbh the connection to the database
     * @return the HashMap containing the statistics
     */
    public static HashMap<String, int[]> calcStatistiques(DatabaseHelper dbh) {
        String query;
        int[] partStatFrToEn = {0, 0, 0};
        int[] partStatEnToFr = {0, 0, 0};
        Cursor cursor;
        HashMap<String, int[]> retour = new HashMap<String, int[]>();
        
        //Statistiques FRA -> ENG
        query = "SELECT mode_french, COUNT(*)"
                + " FROM dico"
                + " WHERE type <> " + Constantes.TYPE_ONLY_ENGLISH
                + " GROUP BY mode_french";
        
        cursor = dbh.getReadableDatabase().rawQuery(query, null);
        
        while (cursor.moveToNext()) {
            partStatFrToEn[cursor.getInt(0)] = cursor.getInt(1);
        }
        cursor.close();
        retour.put("FrToEn", partStatFrToEn);
        //-------------------------------------------------------------------
        
        //Statistiques ENG -> FRA
        query = "SELECT mode_english, COUNT(*)"
                + " FROM dico"
                + " WHERE type <> " + Constantes.TYPE_ONLY_FRENCH
                + " GROUP BY mode_english";
        
        cursor = dbh.getReadableDatabase().rawQuery(query, null);
        
        while (cursor.moveToNext()) {
            partStatEnToFr[cursor.getInt(0)] = cursor.getInt(1);
        }
        cursor.close();
        retour.put("EnToFr", partStatEnToFr);
        //-------------------------------------------------------------------
        
        return retour;
    }
    
    /**
     * The algorithm which selects the word to translate.
     * @param languageOfWordToTranslate the language of the word to translate
     * @param dbh the connection to the database
     * @return the selected full dicotuple object
     */
    public static Dicotuple algoSelectWord(String languageOfWordToTranslate, DatabaseHelper dbh) {
        String query;
        Random random = new Random();
        int randomNb, threshold, nbResults, combinedIndice, weightOfWordsInLearning;
        Cursor cursor;
        Calendar currentCalendar;
        long currentTimestamp, timestampDiff;
        Dicotuple retour = null;
        
        currentCalendar = Calendar.getInstance();
        currentTimestamp = currentCalendar.getTimeInMillis() / 1000;
        
        // 1ère phase de l'algorithme : on check si un mot "En cours d'apprentissage" est éligible
        // Construction de la query
        query =
            "SELECT id_dicotuple, indice_" + languageOfWordToTranslate + "_secondary, "
            + "timestamp_last_answer_" + languageOfWordToTranslate
            + " FROM dico"
            + " WHERE type <> ";
        
        if (languageOfWordToTranslate.equals("english")) {
            query += Constantes.TYPE_ONLY_FRENCH;
        } else {
            query += Constantes.TYPE_ONLY_ENGLISH;
        }
        
        query +=
            " AND mode_" + languageOfWordToTranslate + " = " + Constantes.MODE_LEARNING_IN_PROGRESS
            + " ORDER BY indice_" + languageOfWordToTranslate + "_secondary";
        
        cursor = dbh.getReadableDatabase().rawQuery(query, null);
        
        while (cursor.moveToNext()) {
            timestampDiff = currentTimestamp - cursor.getLong(2);
            if (Word.wordInLearningIsEligible(cursor.getInt(1), timestampDiff)) {
                retour = Dicotuple.retrieveDicotupleFromDatabase(cursor.getInt(0), dbh);
                break;
            }
        }
        cursor.close();
        
        if (retour != null) {
            return retour;
        }
        // FIN 1ère phase de l'algorithme -------------------------------------
        
        // 2ème phase de l'algorithme :
        // si aucun mot éligible à la phase 1, on check si un mot "Appris" est éligible
        query =
            "SELECT indice_" + languageOfWordToTranslate + "_secondary, COUNT(*) "
            + "FROM dico "
            + "WHERE type <> ";
        
        if (languageOfWordToTranslate.equals("english")) {
            query += Constantes.TYPE_ONLY_FRENCH + " ";
        } else {
            query += Constantes.TYPE_ONLY_ENGLISH + " ";
        }
        
        query +=
            "AND mode_" + languageOfWordToTranslate + " = " + Constantes.MODE_LEARNING_IN_PROGRESS + " "
            + "GROUP BY indice_" + languageOfWordToTranslate + "_secondary";
        
        cursor = dbh.getReadableDatabase().rawQuery(query, null);
        weightOfWordsInLearning = 0;
        while (cursor.moveToNext()) {
            weightOfWordsInLearning += cursor.getInt(1) * (10 - cursor.getInt(0));
        }
        cursor.close();
        
        if (weightOfWordsInLearning > 100) {
            weightOfWordsInLearning = 100;
        }
        threshold = random.nextInt(11 - (int)(weightOfWordsInLearning / 10)) + 11;
        
        if (threshold != 21) {
            // Construction de la query
            query =
                "SELECT id_dicotuple, indice_" + languageOfWordToTranslate + "_primary, "
                + "timestamp_last_answer_" + languageOfWordToTranslate + " "
                + "FROM dico "
                + "WHERE type <> ";
            
            if (languageOfWordToTranslate.equals("english")) {
                query += Constantes.TYPE_ONLY_FRENCH;
            } else {
                query += Constantes.TYPE_ONLY_ENGLISH;
            }
            
            query +=
                " AND mode_" + languageOfWordToTranslate + " = " + Constantes.MODE_KNOWN;
            
            cursor = dbh.getReadableDatabase().rawQuery(query, null);
            
            while (cursor.moveToNext()) {
                timestampDiff = currentTimestamp - cursor.getLong(2);
                combinedIndice = Word.calcCombinedIndice(cursor.getInt(1), timestampDiff);
                
                if (combinedIndice >= threshold) {
                    retour = Dicotuple.retrieveDicotupleFromDatabase(cursor.getInt(0), dbh);
                    threshold = combinedIndice + 1;
                    if (threshold > 20) {
                        break;
                    }
                }
            }
            cursor.close();
            
            if (retour != null) {
                return retour;
            }
        }
        // FIN 2ème phase de l'algorithme -------------------------------------
        
        // 3ème phase de l'algorithme :
        // si aucun mot éligible à la phase 2, on check s'il reste un mot "jamais étudié" dans la base
        query =
            "SELECT COUNT(*)"
            + " FROM dico"
            + " WHERE type <> ";
        
        if (languageOfWordToTranslate.equals("english")) {
            query += Constantes.TYPE_ONLY_FRENCH;
        } else {
            query += Constantes.TYPE_ONLY_ENGLISH;
        }
        
        query +=
            " AND mode_" + languageOfWordToTranslate + " = " + Constantes.MODE_NEVER_ANSWERED;
        
        cursor = dbh.getReadableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        nbResults = cursor.getInt(0);
        cursor.close();
        
        if (nbResults > 0) {
            randomNb = random.nextInt(nbResults);
            
            // Construction de la query
            query =
                "SELECT id_dicotuple"
                + " FROM dico"
                + " WHERE type <> ";
            
            if (languageOfWordToTranslate.equals("english")) {
                query += Constantes.TYPE_ONLY_FRENCH;
            } else {
                query += Constantes.TYPE_ONLY_ENGLISH;
            }
            
            query +=
                " AND mode_" + languageOfWordToTranslate + " = " + Constantes.MODE_NEVER_ANSWERED
                + " LIMIT " + randomNb + ", 1";
            
            cursor = dbh.getReadableDatabase().rawQuery(query, null);
            cursor.moveToFirst();
            retour = Dicotuple.retrieveDicotupleFromDatabase(cursor.getInt(0), dbh);
            cursor.close();
            
            return retour;
        }
        // FIN 3ème phase de l'algorithme -------------------------------------
        
        // 4ème phase de l'algorithme :
        // si aucun mot éligible à la phase 3, on sélectionne le mot "Appris" le plus anciennement vu
        // Construction de la query
        query =
            "SELECT id_dicotuple"
            + " FROM dico"
            + " WHERE type <> ";
        
        if (languageOfWordToTranslate.equals("english")) {
            query += Constantes.TYPE_ONLY_FRENCH;
        } else {
            query += Constantes.TYPE_ONLY_ENGLISH;
        }
        
        query +=
            " AND mode_" + languageOfWordToTranslate + " = " + Constantes.MODE_KNOWN
            + " ORDER BY timestamp_last_answer_" + languageOfWordToTranslate
            + " LIMIT 1";

        cursor = dbh.getReadableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        retour = Dicotuple.retrieveDicotupleFromDatabase(cursor.getInt(0), dbh);
        cursor.close();
        
        if (retour != null) {
            return retour;
        }
        // FIN 4ème phase de l'algorithme -------------------------------------
            
        // 5ème phase de l'algorithme :
        // si aucun mot éligible à la phase 4, on sélectionne le mot "En cours d'apprentissage" le plus anciennement vu
        // Construction de la query
        query =
            "SELECT id_dicotuple"
            + " FROM dico"
            + " WHERE type <> ";
        
        if (languageOfWordToTranslate.equals("english")) {
            query += Constantes.TYPE_ONLY_FRENCH;
        } else {
            query += Constantes.TYPE_ONLY_ENGLISH;
        }
        
        query +=
            " AND mode_" + languageOfWordToTranslate + " = " + Constantes.MODE_LEARNING_IN_PROGRESS
            + " ORDER BY timestamp_last_answer_" + languageOfWordToTranslate
            + " LIMIT 1";

        cursor = dbh.getReadableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        retour = Dicotuple.retrieveDicotupleFromDatabase(cursor.getInt(0), dbh);
        cursor.close();
        
        return retour;
        // FIN 5ème phase de l'algorithme -------------------------------------
    }

}
