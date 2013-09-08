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
     * Calcule les statistiques.
     * @param dbh la connexion à la base de données
     * @return le HashMap contenant les statistiques
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
     * L'algorithme qui sélectionne le mot à traduire.
     * @param languageOfWordToTranslate la langue du mot à traduire
     * @param dbh la connexion à la base de données
     * @return l'objet complet dicotuple sélectionné
     */
    public static Dicotuple algoSelectWord(String languageOfWordToTranslate, DatabaseHelper dbh) {
        String query;
        Random random = new Random();
        int randomNb, combinedIndice, nbResults;
        int maxCombinedIndice = 0;
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
        
        cursorScan:
        while (cursor.moveToNext()) {
            timestampDiff = currentTimestamp - cursor.getLong(2);
            
            switch (cursor.getInt(1)) {
                case 0:
                    if (timestampDiff > 5) {
                        retour = Dicotuple.retrieveDicotupleFromDatabase(cursor.getInt(0), dbh);
                        break cursorScan;
                    }
                    break;
                case 1:
                    if (timestampDiff > 20) {
                        retour = Dicotuple.retrieveDicotupleFromDatabase(cursor.getInt(0), dbh);
                        break cursorScan;
                    }
                    break;
                case 2:
                    if (timestampDiff > 60) {
                        retour = Dicotuple.retrieveDicotupleFromDatabase(cursor.getInt(0), dbh);
                        break cursorScan;
                    }
                    break;
                case 3:
                    if (timestampDiff > 240) {
                        retour = Dicotuple.retrieveDicotupleFromDatabase(cursor.getInt(0), dbh);
                        break cursorScan;
                    }
                    break;
                case 4:
                    if (timestampDiff > 900) {
                        retour = Dicotuple.retrieveDicotupleFromDatabase(cursor.getInt(0), dbh);
                        break cursorScan;
                    }
                    break;
                case 5:
                    if (timestampDiff > 3600) {
                        retour = Dicotuple.retrieveDicotupleFromDatabase(cursor.getInt(0), dbh);
                        break cursorScan;
                    }
                    break;
                case 6:
                    if (timestampDiff > 14400) {
                        retour = Dicotuple.retrieveDicotupleFromDatabase(cursor.getInt(0), dbh);
                        break cursorScan;
                    }
                    break;
                case 7:
                    if (timestampDiff > 43200) {
                        retour = Dicotuple.retrieveDicotupleFromDatabase(cursor.getInt(0), dbh);
                        break cursorScan;
                    }
                    break;
                case 8:
                    if (timestampDiff > 86400) {
                        retour = Dicotuple.retrieveDicotupleFromDatabase(cursor.getInt(0), dbh);
                        break cursorScan;
                    }
                    break;
                case 9:
                    if (timestampDiff > 172800) {
                        retour = Dicotuple.retrieveDicotupleFromDatabase(cursor.getInt(0), dbh);
                        break cursorScan;
                    }
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
        randomNb = random.nextInt(11) + 11;
        if (randomNb != 21) {
            // Construction de la query
            query =
                "SELECT id_dicotuple, indice_" + languageOfWordToTranslate + "_primary, "
                + "timestamp_last_answer_" + languageOfWordToTranslate
                + " FROM dico"
                + " WHERE type <> ";
            
            if (languageOfWordToTranslate.equals("english")) {
                query += Constantes.TYPE_ONLY_FRENCH;
            } else {
                query += Constantes.TYPE_ONLY_ENGLISH;
            }
            
            query +=
                " AND mode_" + languageOfWordToTranslate + " = " + Constantes.MODE_KNOWN;
            
            cursor = dbh.getReadableDatabase().rawQuery(query, null);
            
            while (cursor.moveToNext()) {
                combinedIndice = Constantes.MAX_INDICE_PRIMARY + 1 - cursor.getInt(1);
                timestampDiff = currentTimestamp - cursor.getLong(2);
                if (timestampDiff <         172800) {
                    
                } else if (timestampDiff <  345600) {
                    combinedIndice += 1;
                } else if (timestampDiff <  604800) {
                    combinedIndice += 2;
                } else if (timestampDiff <  864000) {
                    combinedIndice += 3;
                } else if (timestampDiff < 1209600) {
                    combinedIndice += 4;
                } else if (timestampDiff < 1814400) {
                    combinedIndice += 5;
                } else if (timestampDiff < 2592000) {
                    combinedIndice += 6;
                } else if (timestampDiff < 3888000) {
                    combinedIndice += 7;
                } else if (timestampDiff < 5184000) {
                    combinedIndice += 8;
                } else if (timestampDiff < 7776000) {
                    combinedIndice += 9;
                } else {
                    combinedIndice += 10;
                }
                
                if ((combinedIndice >= randomNb) && ((retour == null) || (combinedIndice > maxCombinedIndice))) {
                    retour = Dicotuple.retrieveDicotupleFromDatabase(cursor.getInt(0), dbh);
                    maxCombinedIndice = combinedIndice;
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
