package vincent.moulin.vocab.menu;

import java.util.Calendar;

import vincent.moulin.vocab.Constantes;
import vincent.moulin.vocab.R;
import vincent.moulin.vocab.TrainingActivity;
import vincent.moulin.vocab.entities.Word;
import vincent.moulin.vocab.database.DatabaseHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.text.Html;
import android.view.MenuItem;
import android.widget.Toast;
import android.database.Cursor;

public class TrainingMenuManager {
    
    public static boolean onOptionsItemSelected(MenuItem item, Activity activity) {
        TrainingActivity trainingActivity = (TrainingActivity) activity;
        String languageOfWordToTranslate = trainingActivity.getLanguageOfWordToTranslate();
        DatabaseHelper dbh = trainingActivity.getDbh();
        String doctorVocabAdviceContent, query;
        Calendar currentCalendar;
        long currentTimestamp, timestampDiff;
        Cursor cursor;
        int nbOfWordsInLearningAndEligible, nbOfWordsKnownAndPotentiallyEligible, combinedIndice;
        String [] singular = {"word", "has"};
        String [] plural = {"words", "have"};
        String [] grammar;
        
        if (DefaultMenuManager.onOptionsItemSelected(item, activity)) {
            return true;
        } else {
            switch (item.getItemId()) {
                case R.id.action_doctor_vocab_advice_id:
                    currentCalendar = Calendar.getInstance();
                    currentTimestamp = currentCalendar.getTimeInMillis() / 1000;
                    
                    //the number of  words in learning and eligible
                    query =
                    "SELECT indice_" + languageOfWordToTranslate + "_secondary, "
                        + "timestamp_last_answer_" + languageOfWordToTranslate
                        + " FROM dico"
                        + " WHERE type <> ";
                
                    if (languageOfWordToTranslate.equals("english")) {
                        query += Constantes.TYPE_ONLY_FRENCH;
                    } else {
                        query += Constantes.TYPE_ONLY_ENGLISH;
                    }
                    
                    query +=
                        " AND mode_" + languageOfWordToTranslate + " = " + Constantes.MODE_LEARNING_IN_PROGRESS;
                    
                    cursor = dbh.getReadableDatabase().rawQuery(query, null);
                    
                    nbOfWordsInLearningAndEligible = 0;
                    while (cursor.moveToNext()) {
                        timestampDiff = currentTimestamp - cursor.getLong(1);
                        if (Word.wordInLearningIsEligible(cursor.getInt(0), timestampDiff)) {
                            nbOfWordsInLearningAndEligible++;
                        }
                    }
                    cursor.close();
                    //-------------------------------------------------------------------
                    
                    //the number of  words in learning and eligible
                    query =
                        "SELECT indice_" + languageOfWordToTranslate + "_primary, "
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
                    
                    nbOfWordsKnownAndPotentiallyEligible = 0;
                    while (cursor.moveToNext()) {
                        timestampDiff = currentTimestamp - cursor.getLong(1);
                        combinedIndice = Word.calcCombinedIndice(cursor.getInt(0), timestampDiff);
                            
                        if (combinedIndice >= 11) {
                            nbOfWordsKnownAndPotentiallyEligible++;
                        }
                    }
                    cursor.close();
                    //-------------------------------------------------------------------
                    
                    if ((nbOfWordsInLearningAndEligible == 0) && (nbOfWordsKnownAndPotentiallyEligible == 0)) {
                        doctorVocabAdviceContent = "<b>IT'S ALL GOOD !</b><br /><br />";
                        doctorVocabAdviceContent += "Concerning the already studied words, the current state of your base doesn't need that you work.";
                    } else {
                        doctorVocabAdviceContent = "<b>IT'S TIME TO WORK !</b><br /><br />";
                        
                        doctorVocabAdviceContent += "<b>" + Integer.toString(nbOfWordsInLearningAndEligible) + "</b> ";
                        if (nbOfWordsInLearningAndEligible <= 1) {
                            grammar = singular;
                        } else {
                            grammar = plural;
                        }
                        doctorVocabAdviceContent += grammar[0] + " whose status is \"Learning in progress\" " + grammar[1] + " to be quickly reviewed in order to make the learning process efficient.";
                        
                        doctorVocabAdviceContent += "<br /><br />";
                        
                        doctorVocabAdviceContent += "<b>" + Integer.toString(nbOfWordsKnownAndPotentiallyEligible) + "</b> ";
                        if (nbOfWordsKnownAndPotentiallyEligible <= 1) {
                            grammar = singular;
                        } else {
                            grammar = plural;
                        }
                        doctorVocabAdviceContent += grammar[0] + " whose status is \"Known\" " + grammar[1] + " to be quickly reviewed in order to consolidate the acquired knowledge.";
                    }
                    
                    new AlertDialog.Builder(activity)
                        .setMessage(Html.fromHtml(doctorVocabAdviceContent))
                        .setNeutralButton(R.string.closure_button_content, null)
                        .show();
                    return true;
                    
                case R.id.action_cancel_last_action_id:
                    trainingActivity.getPrevDicotupleBeforeAnswering().updateDicotupleInDatabase(dbh);
                    trainingActivity.setCurrentDicotuple(trainingActivity.getPrevDicotupleBeforeAnswering());
                    trainingActivity.setPrevDicotupleBeforeAnswering(null);
                    trainingActivity.displayWord();
                    Toast
                        .makeText(trainingActivity, R.string.cancellation_toast_content, Toast.LENGTH_SHORT)
                        .show();
                    return true;
            }
        }
        
        return false;
    }
    
}
