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

package vincent.moulin.vocab.entities;

import java.util.ArrayList;
import java.util.Random;
import android.database.Cursor;
import vincent.moulin.vocab.MyApplication;
import vincent.moulin.vocab.constants.Constants;
import vincent.moulin.vocab.helpers.DatabaseHelper;
import vincent.moulin.vocab.utilities.TimestampNow;

/**
 * The Dictionary class represents a dictionary.
 * 
 * @author Vincent MOULIN
 */
public class Dictionary
{
    private Dicotuple dicotuples[];

    public Dicotuple[] getDicotuples() {
        return dicotuples;
    }
    public void setDicotuples(Dicotuple[] dicotuples) {
        this.dicotuples = dicotuples;
    }
    
    /**
     * Calculate the statistics for the given language name "langName".
     * @param langName the language name for which the statistics are calculated
     * @return the statistics
     */
    public static int[] calcStatForLangName(String langName) {
        DatabaseHelper dbh = DatabaseHelper.getInstance(MyApplication.getContext());
        String query;
        Cursor cursor;
        int[] retour = new int[3];
        
        query = "SELECT "
              +     "id_status_" + langName + ", "
              +     "COUNT(*) "
              + "FROM dicotuple "
              + "WHERE is_active_" + langName + " = 1 "
              + "GROUP BY id_status_" + langName;
        
        cursor = dbh.getReadableDatabase().rawQuery(query, null);
        
        while (cursor.moveToNext()) {
            retour[cursor.getInt(0)] = cursor.getInt(1);
        }
        cursor.close();

        return retour;
    }

    /**
     * The algorithm which selects the word to translate.
     * @param startingLangName the starting language name
     * @return the selected Dicotuple object
     */
    public static Dicotuple algoSelectWord(String startingLangName) {
        // Common variables
        DatabaseHelper dbh = DatabaseHelper.getInstance(MyApplication.getContext());
        String query;
        Cursor cursor;
        Random random = new Random();
        int nbInitialWords;
        long rawTimestampNow = TimestampNow.getInstance().getValue(Constants.TIMESTAMP_RAW_VALUE);
        Dicotuple retour = null;
        
        // Variables for the phase 1
        int p1_secondaryIndice = 0;
        long p1_timestampDiff, p1_timestampLastAnswer = 0;
        ArrayList<Integer> p1_eligibleIds = new ArrayList<Integer>();
        
        // Variables for the phase 2
        int p2_threshold, p2_combinedIndice, p2_weightOfLearningWords = 0;
        long p2_timestampDiff;
        ArrayList<Integer> p2_eligibleIds = new ArrayList<Integer>();
        
        // Variables for the phase 3
        int p3_randomPosition;
        //END: Declaration of variables

        // 1st phase of the algorithm :
        // We check if a Word with status "learning" is eligible
        query = "SELECT "
                +     "id, "
                +     "secondary_indice_" + startingLangName + ", "
                +     "timestamp_last_answer_" + startingLangName + " "
                + "FROM dicotuple "
                + "WHERE is_active_" + startingLangName + " = 1 "
                + "AND id_status_" + startingLangName + " = " + Constants.STATUSES.getId("learning") + " "
                + "ORDER BY secondary_indice_" + startingLangName + ", timestamp_last_answer_" + startingLangName;
          
        cursor = dbh.getReadableDatabase().rawQuery(query, null);
          
        while (cursor.moveToNext()) {
            if (( ! p1_eligibleIds.isEmpty())
                && ((cursor.getInt(1) > p1_secondaryIndice)
                    || (cursor.getLong(2) > p1_timestampLastAnswer))
            ) {
                break;
            }
            
            p1_timestampDiff = rawTimestampNow - cursor.getLong(2);
            if (Word.learningWordIsEligible(cursor.getInt(1), p1_timestampDiff)) {
                p1_eligibleIds.add(cursor.getInt(0));
                p1_secondaryIndice = cursor.getInt(1);
                p1_timestampLastAnswer = cursor.getLong(2);
            }
        }
        cursor.close();
        
        if ( ! p1_eligibleIds.isEmpty()) {
            return Dicotuple.getById(p1_eligibleIds.get(random.nextInt(p1_eligibleIds.size())));
        }
        //END: 1st phase of the algorithm -------------------------------------

        // 2nd phase of the algorithm :
        // If no eligible Word at phase 1, we check if a Word with status "known" is eligible
        // (if there is no more Word with status "initial", we check if a Word with status "known" exists without considering the eligibility)
        //
        // We set the threshold "p2_threshold"
        query = "SELECT COUNT(*) "
              + "FROM dicotuple "
              + "WHERE is_active_" + startingLangName + " = 1 "
              + "AND id_status_" + startingLangName + " = " + Constants.STATUSES.getId("initial");

        cursor = dbh.getReadableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        nbInitialWords = cursor.getInt(0);
        cursor.close();

        if (nbInitialWords == 0) {
            // We set p2_threshold to the maximum of a combined indice
            p2_threshold = Constants.MAX_COMBINED_INDICE;
        } else if (random.nextInt(10) == 0) {
            // We set p2_threshold to the maximum of a combined indice of an eligible word
            // The goal is to guarantee that all the eligible known words will be reviewed within a reasonable time
            p2_threshold = Constants.MAX_COMBINED_INDICE_ELIGIBLE_WORD;
        } else {
            // The higher the weight of words in learning is, the higher the probability to select a known word (ie with the "known" status) is
            // The lower the weight of words in learning is, the higher the probability to select a new word (ie with the "initial" status) is
            // The goal is to avoid to have too many words in learning at the same time
            query = "SELECT "
                  +     "secondary_indice_" + startingLangName + ", "
                  +     "COUNT(*) "
                  + "FROM dicotuple "
                  + "WHERE is_active_" + startingLangName + " = 1 "
                  + "AND id_status_" + startingLangName + " = " + Constants.STATUSES.getId("learning") + " "
                  + "GROUP BY secondary_indice_" + startingLangName;
    
            cursor = dbh.getReadableDatabase().rawQuery(query, null);
            while (cursor.moveToNext()) {
                p2_weightOfLearningWords += cursor.getInt(1) * (11 - cursor.getInt(0));
                
                if (p2_weightOfLearningWords >= 99) {
                    p2_weightOfLearningWords = 99;
                    break;
                }
            }
            cursor.close();

            if ((p2_weightOfLearningWords < 99)
                && (random.nextInt(10) == 0)
            ) {
                // The phase 2 is skipped
                // The goal is to select sometimes a new word even when there are a lot of eligible known words
                // (unless there are already a lot of words in learning)
                p2_threshold = 0;
            } else {
                p2_threshold = random.nextInt((p2_weightOfLearningWords / 10) + 1) + 1;
            }
        }
        //END: Setting of the threshold "p2_threshold"

        if (p2_threshold != 0) {
            query = "SELECT "
                  +     "id, "
                  +     "primary_indice_" + startingLangName + ", "
                  +     "timestamp_last_answer_" + startingLangName + " "
                  + "FROM dicotuple "
                  + "WHERE is_active_" + startingLangName + " = 1 "
                  + "AND id_status_" + startingLangName + " = " + Constants.STATUSES.getId("known");
              
            cursor = dbh.getReadableDatabase().rawQuery(query, null);
              
            while (cursor.moveToNext()) {
                p2_timestampDiff = rawTimestampNow - cursor.getLong(2);
                p2_combinedIndice = Word.calcCombinedIndice(cursor.getInt(1), p2_timestampDiff);
                  
                if (p2_combinedIndice == p2_threshold) {
                    p2_eligibleIds.add(cursor.getInt(0));
                } else if (p2_combinedIndice < p2_threshold) {
                    p2_eligibleIds = new ArrayList<Integer>();
                    p2_eligibleIds.add(cursor.getInt(0));
                    p2_threshold = p2_combinedIndice;
                }
            }
            cursor.close();
            
            if ( ! p2_eligibleIds.isEmpty()) {
                return Dicotuple.getById(p2_eligibleIds.get(random.nextInt(p2_eligibleIds.size())));
            }
        }
        //END: 2nd phase of the algorithm -------------------------------------

        // 3rd phase of the algorithm :
        // If no eligible Word at phase 2 or phase 2 has been skipped
        // and if there is at least one Word with status "initial" left in the base,
        // we randomly select one of them
        if (nbInitialWords > 0) {
            p3_randomPosition = random.nextInt(nbInitialWords);

            query = "SELECT id "
                  + "FROM dicotuple "
                  + "WHERE is_active_" + startingLangName + " = 1 "
                  + "AND id_status_" + startingLangName + " = " + Constants.STATUSES.getId("initial") + " "
                  + "LIMIT " + p3_randomPosition + ", 1";

            cursor = dbh.getReadableDatabase().rawQuery(query, null);
            cursor.moveToFirst();
            retour = Dicotuple.getById(cursor.getInt(0));
            cursor.close();

            return retour;
        }
        //END: 3rd phase of the algorithm -------------------------------------

        // 4th phase of the algorithm :
        // If no eligible Word at phase 3, we select the oldest studied Word (which necessarily has the status "learning")
        query = "SELECT id "
              + "FROM dicotuple "
              + "WHERE is_active_" + startingLangName + " = 1 "
              + "AND timestamp_last_answer_" + startingLangName + " = ("
              +     "SELECT MIN(timestamp_last_answer_" + startingLangName + ") "
              +     "FROM dicotuple "
              +     "WHERE is_active_" + startingLangName + " = 1"
              + ")";

        cursor = dbh.getReadableDatabase().rawQuery(query, null);

        if (cursor.getCount() > 0) {
            cursor.moveToPosition(random.nextInt(cursor.getCount()));
            retour = Dicotuple.getById(cursor.getInt(0));
        }

        cursor.close();

        return retour;
        //END: 4th phase of the algorithm -------------------------------------
    }
}
