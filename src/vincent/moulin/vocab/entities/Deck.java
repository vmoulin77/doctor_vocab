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

package vincent.moulin.vocab.entities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import vincent.moulin.vocab.MyApplication;
import vincent.moulin.vocab.helpers.DatabaseHelper;
import vincent.moulin.vocab.utilities.Now;
import android.database.Cursor;
import android.util.SparseIntArray;

/**
 * The Deck class represents a set of cards.
 * 
 * @author Vincent MOULIN
 */
public class Deck
{
    private Card[] cards;
    
    public Deck(Card[] cards) {
        this.cards = cards;
    }

    public Card[] getCards() {
        return this.cards;
    }
    public void setCards(Card[] cards) {
        this.cards = cards;
    }
    
    /**
     * Calculate the statistics for the given language name "langName".
     * @param langName the language name for which the statistics are calculated
     * @return the statistics
     */
    public static SparseIntArray calcStatForLangName(String langName) {
        DatabaseHelper dbh = DatabaseHelper.getInstance(MyApplication.getContext());
        String query;
        Cursor cursor;
        SparseIntArray retour = new SparseIntArray();
        
        query = "SELECT "
              +     "id_status_" + langName + ", "
              +     "COUNT(*) "
              + "FROM card "
              + "WHERE is_active_" + langName + " = 1 "
              + "GROUP BY id_status_" + langName;
        
        cursor = dbh.getReadableDatabase().rawQuery(query, null);
        
        while (cursor.moveToNext()) {
            retour.put(cursor.getInt(0), cursor.getInt(1));
        }
        cursor.close();

        return retour;
    }

    /**
     * The algorithm which selects the word to translate.
     * @param startingLangName the starting language name
     * @return the selected TrainingCard object
     */
    public static TrainingCard algoSRS(String startingLangName) {
        // Common variables
        DatabaseHelper dbh = DatabaseHelper.getInstance(MyApplication.getContext());
        String query;
        Cursor cursor;
        Random random = new Random(Calendar.getInstance().getTimeInMillis());
        int nbInitialWords;
        long rawTimestampNow = Now.getInstance().getRawTimestamp();
        TrainingCard retour = null;
        
        // Variables for the phase 1
        int p1_secondaryIndice = 0;
        long p1_timestampLastAnswer = 0;
        ArrayList<Integer> p1_preselectedIds = new ArrayList<Integer>();
        
        // Variables for the phase 2
        int p2_combinedIndice, p2_threshold = Side.MAX_COMBINED_INDICE_ELIGIBLE_WORD, p2_weightOfLearningWords = 0;
        long p2_timestampDiff;
        ArrayList<Integer> p2_preselectedIds = new ArrayList<Integer>();
        
        // Variables for the phase 3
        int p3_randomPosition;
        //END: Declaration of variables

        // 1st phase of the algorithm :
        // We check if a Word with status "learning" is eligible
        query = "SELECT "
                +     "id, "
                +     "secondary_indice_" + startingLangName + ", "
                +     "timestamp_last_answer_" + startingLangName + " "
                + "FROM card "
                + "WHERE is_active_" + startingLangName + " = 1 "
                + "AND id_status_" + startingLangName + " = " + Status.findId("learning") + " "
                + "ORDER BY secondary_indice_" + startingLangName + ", timestamp_last_answer_" + startingLangName;
          
        cursor = dbh.getReadableDatabase().rawQuery(query, null);
          
        while (cursor.moveToNext()) {
            if (( ! p1_preselectedIds.isEmpty())
                && ((cursor.getInt(1) > p1_secondaryIndice)
                    || (cursor.getLong(2) > p1_timestampLastAnswer))
            ) {
                break;
            }
            
            if (Side.learningWordIsEligible(cursor.getInt(1), rawTimestampNow - cursor.getLong(2))) {
                p1_preselectedIds.add(cursor.getInt(0));
                p1_secondaryIndice = cursor.getInt(1);
                p1_timestampLastAnswer = cursor.getLong(2);
            }
        }
        cursor.close();
        
        if ( ! p1_preselectedIds.isEmpty()) {
            return TrainingCard.find(p1_preselectedIds.get(random.nextInt(p1_preselectedIds.size())), startingLangName, true);
        }
        //END: 1st phase of the algorithm -------------------------------------
        
        // 2nd phase of the algorithm :
        // If no eligible Word at phase 1 and if phase 2 is not skipped, we check if a Word with status "known" is eligible
        //
        // The higher the weight of words in learning is, the higher the probability to select a known word (ie with the "known" status) is
        // The lower the weight of words in learning is, the higher the probability to select a new word (ie with the "initial" status) is
        // The goal is to avoid to have too many words in learning at the same time
        query = "SELECT "
              +     "secondary_indice_" + startingLangName + ", "
              +     "COUNT(*) "
              + "FROM card "
              + "WHERE is_active_" + startingLangName + " = 1 "
              + "AND id_status_" + startingLangName + " = " + Status.findId("learning") + " "
              + "GROUP BY secondary_indice_" + startingLangName;

        cursor = dbh.getReadableDatabase().rawQuery(query, null);
        while (cursor.moveToNext()) {
            p2_weightOfLearningWords += cursor.getInt(1) * (11 - cursor.getInt(0));
            
            if (p2_weightOfLearningWords >= 500) {
                p2_weightOfLearningWords = 500;
                break;
            }
        }
        cursor.close();

        if (random.nextInt(1000) < (500 + p2_weightOfLearningWords)) {
            query = "SELECT "
                  +     "id, "
                  +     "primary_indice_" + startingLangName + ", "
                  +     "timestamp_last_answer_" + startingLangName + " "
                  + "FROM card "
                  + "WHERE is_active_" + startingLangName + " = 1 "
                  + "AND id_status_" + startingLangName + " = " + Status.findId("known");
                
            cursor = dbh.getReadableDatabase().rawQuery(query, null);
            
            while (cursor.moveToNext()) {
                p2_timestampDiff = rawTimestampNow - cursor.getLong(2);
                p2_combinedIndice = Side.calcCombinedIndice(cursor.getInt(1), p2_timestampDiff);
                
                if (p2_combinedIndice == p2_threshold) {
                    p2_preselectedIds.add(cursor.getInt(0));
                } else if (p2_combinedIndice < p2_threshold) {
                    p2_preselectedIds = new ArrayList<Integer>();
                    p2_preselectedIds.add(cursor.getInt(0));
                    p2_threshold = p2_combinedIndice;
                }
            }
            cursor.close();
            
            if ( ! p2_preselectedIds.isEmpty()) {
                return TrainingCard.find(p2_preselectedIds.get(random.nextInt(p2_preselectedIds.size())), startingLangName, true);
            }
        }
        //END: 2nd phase of the algorithm -------------------------------------

        // 3rd phase of the algorithm :
        // If no eligible Word at phase 2 or phase 2 has been skipped
        // and if there is at least one Word with status "initial" left in the Deck,
        // we randomly select one of them
        query = "SELECT COUNT(*) "
              + "FROM card "
              + "WHERE is_active_" + startingLangName + " = 1 "
              + "AND id_status_" + startingLangName + " = " + Status.findId("initial");

        cursor = dbh.getReadableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        nbInitialWords = cursor.getInt(0);
        cursor.close();
        
        if (nbInitialWords > 0) {
            p3_randomPosition = random.nextInt(nbInitialWords);

            query = "SELECT id "
                  + "FROM card "
                  + "WHERE is_active_" + startingLangName + " = 1 "
                  + "AND id_status_" + startingLangName + " = " + Status.findId("initial") + " "
                  + "LIMIT " + p3_randomPosition + ", 1";

            cursor = dbh.getReadableDatabase().rawQuery(query, null);
            cursor.moveToFirst();
            retour = TrainingCard.find(cursor.getInt(0), startingLangName, true);
            cursor.close();

            return retour;
        }
        //END: 3rd phase of the algorithm -------------------------------------
        
        // 4th phase of the algorithm :
        // If no eligible Word at phase 3, we select the oldest studied Word with status "known"
        query = "SELECT id "
              + "FROM card "
              + "WHERE is_active_" + startingLangName + " = 1 "
              + "AND id_status_" + startingLangName + " = " + Status.findId("known") + " "
              + "AND timestamp_last_answer_" + startingLangName + " = ("
              +     "SELECT MIN(timestamp_last_answer_" + startingLangName + ") "
              +     "FROM card "
              +     "WHERE is_active_" + startingLangName + " = 1 "
              +     "AND id_status_" + startingLangName + " = " + Status.findId("known")
              + ")";

        cursor = dbh.getReadableDatabase().rawQuery(query, null);

        if (cursor.getCount() > 0) {
            cursor.moveToPosition(random.nextInt(cursor.getCount()));
            retour = TrainingCard.find(cursor.getInt(0), startingLangName, false);
        }

        cursor.close();
        
        if (retour != null) {
            return retour;
        }
        //END: 4th phase of the algorithm -------------------------------------
        
        // 5th phase of the algorithm :
        // If no eligible Word at phase 4, we select the oldest studied Word with status "learning"
        query = "SELECT id "
              + "FROM card "
              + "WHERE is_active_" + startingLangName + " = 1 "
              + "AND id_status_" + startingLangName + " = " + Status.findId("learning") + " "
              + "AND timestamp_last_answer_" + startingLangName + " = ("
              +     "SELECT MIN(timestamp_last_answer_" + startingLangName + ") "
              +     "FROM card "
              +     "WHERE is_active_" + startingLangName + " = 1 "
              +     "AND id_status_" + startingLangName + " = " + Status.findId("learning")
              + ")";

        cursor = dbh.getReadableDatabase().rawQuery(query, null);

        if (cursor.getCount() > 0) {
            cursor.moveToPosition(random.nextInt(cursor.getCount()));
            retour = TrainingCard.find(cursor.getInt(0), startingLangName, false);
        }

        cursor.close();

        return retour;
        //END: 5th phase of the algorithm -------------------------------------
    }
}
