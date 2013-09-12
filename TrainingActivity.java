package vincent.moulin.vocab;

import vincent.moulin.vocab.R;
import vincent.moulin.vocab.database.DatabaseHelper;
import vincent.moulin.vocab.entities.Dico;
import vincent.moulin.vocab.entities.Dicotuple;
import vincent.moulin.vocab.entities.Word;
import vincent.moulin.vocab.menu.TrainingMenuManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Html;

public class TrainingActivity extends Activity {
    private DatabaseHelper dbh;
    private Dicotuple currentDicotuple, prevDicotupleBeforeAnswering = null;
    private String languageOfWordToTranslate;

    public DatabaseHelper getDbh() {
        return dbh;
    }

    public void setDbh(DatabaseHelper dbh) {
        this.dbh = dbh;
    }

    public Dicotuple getCurrentDicotuple() {
        return currentDicotuple;
    }

    public void setCurrentDicotuple(Dicotuple currentDicotuple) {
        this.currentDicotuple = currentDicotuple;
    }

    public Dicotuple getPrevDicotupleBeforeAnswering() {
        return prevDicotupleBeforeAnswering;
    }

    public void setPrevDicotupleBeforeAnswering(Dicotuple prevDicotupleBeforeAnswering) {
        this.prevDicotupleBeforeAnswering = prevDicotupleBeforeAnswering;
    }

    public String getLanguageOfWordToTranslate() {
        return languageOfWordToTranslate;
    }

    public void setLanguageOfWordToTranslate(String languageOfWordToTranslate) {
        this.languageOfWordToTranslate = languageOfWordToTranslate;
    }

    public void displayWord() {
        TextView textViewWordToTranslate, textViewMsgHeader;
        Word wordToTranslate;
        
        textViewMsgHeader = (TextView)findViewById(R.id.msg_header_id);
        textViewWordToTranslate = (TextView)findViewById(R.id.word_to_translate_id);
        
        if (this.languageOfWordToTranslate.equals("english")) {
            wordToTranslate = this.currentDicotuple.getWordEnglish();
        } else {
            wordToTranslate = this.currentDicotuple.getWordFrench();
        }
        
        if (wordToTranslate.getMode() == Constantes.MODE_NEVER_ANSWERED) {
            textViewMsgHeader.setTextColor(Color.parseColor("#000000"));
        } else if (wordToTranslate.getMode() == Constantes.MODE_KNOWN) {
            textViewMsgHeader.setTextColor(getResources().getColor(R.color.DarkGreenColor));
        } else {
            textViewMsgHeader.setTextColor(getResources().getColor(R.color.DarkRedColor));
        }
        
        textViewWordToTranslate.setText(Html.fromHtml(wordToTranslate.getContent()));
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TextView textViewIndicSensTrad;
        
        SharedPreferences sharedPreferences = this.getSharedPreferences("vincent.moulin.vocab", MODE_PRIVATE);
        
        this.dbh = new DatabaseHelper(this);
        this.languageOfWordToTranslate = sharedPreferences.getString("language_of_word_to_translate", null);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        
        textViewIndicSensTrad = (TextView)findViewById(R.id.indic_sens_trad_id);
        if (this.languageOfWordToTranslate.equals("english")) {
            textViewIndicSensTrad.setText(R.string.indic_sens_trad_en_to_fr);
        } else {
            textViewIndicSensTrad.setText(R.string.indic_sens_trad_fr_to_en);
        }
        
        this.currentDicotuple = Dico.algoSelectWord(this.languageOfWordToTranslate, this.dbh);
        this.displayWord();
    }
    
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem cancelLastActionItem = menu.findItem(R.id.action_cancel_last_action_id);
        
        if (this.prevDicotupleBeforeAnswering == null) {
            cancelLastActionItem.setEnabled(false);
        } else {
            cancelLastActionItem.setEnabled(true);
        }
        
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.default_menu, menu);
        getMenuInflater().inflate(R.menu.extras_training_menu, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        return TrainingMenuManager.onOptionsItemSelected(item, this);
    }
    
    public void statAccess(View v) {
        startActivity(new Intent(this, StatActivity.class));
    }
    
    public void processOk(View v) {
        Word wordToTranslate;
        
        this.currentDicotuple.manageAnswer(this.languageOfWordToTranslate, true, this.dbh);
        
        //Affichage du Toast de félicitation
        if (this.languageOfWordToTranslate.equals("english")) {
            wordToTranslate = this.currentDicotuple.getWordEnglish();
        } else {
            wordToTranslate = this.currentDicotuple.getWordFrench();
        }
        
        if ( (wordToTranslate.getMode() == Constantes.MODE_LEARNING_IN_PROGRESS)
              && (wordToTranslate.getIndiceSecondary() == (Constantes.MAX_INDICE_SECONDARY - 1)) ){
            Toast
                .makeText(this, R.string.congratulation_toast_content, Toast.LENGTH_SHORT)
                .show();
        }
        //----------------------------------------------------------
        
        this.prevDicotupleBeforeAnswering = this.currentDicotuple;
        
        this.currentDicotuple = Dico.algoSelectWord(this.languageOfWordToTranslate, this.dbh);
        this.displayWord();
    }
    
    public void processNok(View v) {
        this.currentDicotuple.manageAnswer(this.languageOfWordToTranslate, false, this.dbh);
        
        this.prevDicotupleBeforeAnswering = this.currentDicotuple;
        
        this.currentDicotuple = Dico.algoSelectWord(this.languageOfWordToTranslate, this.dbh);
        this.displayWord();
    }
    
    public void showTraduction(View v) {
        Word wordSolution;
        
        if (this.languageOfWordToTranslate.equals("english")) {
            wordSolution = this.currentDicotuple.getWordFrench();
        } else {
            wordSolution = this.currentDicotuple.getWordEnglish();
        }
        
        new AlertDialog.Builder(this)
            .setMessage(Html.fromHtml(wordSolution.getContent()))
            .setNeutralButton(R.string.closure_button_content, null)
            .show();
    }

}
