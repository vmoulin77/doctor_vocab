package vincent.moulin.vocab;

import vincent.moulin.vocab.R;
import vincent.moulin.vocab.database.DatabaseHelper;
import vincent.moulin.vocab.entities.Dico;
import vincent.moulin.vocab.entities.Dicotuple;
import vincent.moulin.vocab.entities.Word;
import vincent.moulin.vocab.menu.DefaultMenuManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class TrainingActivity extends Activity {
    private DatabaseHelper dbh;
    private Dicotuple currentDicotuple;
    private String languageOfWordToTranslate;

    private void displayWord() {
        TextView textViewWordToTranslate, textViewMsgNewWord;
        
        this.currentDicotuple = Dico.algoSelectWord(this.languageOfWordToTranslate, this.dbh);
        
        textViewMsgNewWord = (TextView)findViewById(R.id.msg_new_word_id);
        textViewWordToTranslate = (TextView)findViewById(R.id.word_to_translate_id);
        
        if (this.languageOfWordToTranslate.equals("english")) {
            if (this.currentDicotuple.getWordEnglish().getMode() == Constantes.MODE_NEVER_ANSWERED) {
                textViewMsgNewWord.setText(R.string.msg_new_word_content);
            } else {
                textViewMsgNewWord.setText("");
            }
            textViewWordToTranslate.setText(this.currentDicotuple.getWordEnglish().getContent());
        } else {
            if (this.currentDicotuple.getWordFrench().getMode() == Constantes.MODE_NEVER_ANSWERED) {
                textViewMsgNewWord.setText(R.string.msg_new_word_content);
            } else {
                textViewMsgNewWord.setText("");
            }
            textViewWordToTranslate.setText(this.currentDicotuple.getWordFrench().getContent());
        }
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
        
        this.displayWord();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.default_menu, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        return DefaultMenuManager.onOptionsItemSelected(item, this);
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
        
        this.displayWord();
    }
    
    public void processNok(View v) {
        this.currentDicotuple.manageAnswer(this.languageOfWordToTranslate, false, this.dbh);
        
        this.displayWord();
    }
    
    public void showTraduction(View v) {
        if (this.languageOfWordToTranslate.equals("english")) {
            new AlertDialog.Builder(this)
                .setMessage(this.currentDicotuple.getWordFrench().getContent())
                .setNeutralButton(R.string.closure_button_content, null)
                .show();
        } else {
            new AlertDialog.Builder(this)
                .setMessage(this.currentDicotuple.getWordEnglish().getContent())
                .setNeutralButton(R.string.closure_button_content, null)
                .show();
        }
    }

}
