package vincent.moulin.vocab;

import vincent.moulin.vocab.R;
import vincent.moulin.vocab.database.DatabaseHelper;
import vincent.moulin.vocab.entities.Dico;
import vincent.moulin.vocab.menu.DefaultMenuManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Pour forcer l'initialisation de la base de données sur la page d'accueil
        Dico.calcStatistiques(new DatabaseHelper(this));
        //-----------------------------------------------------
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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
    
    public void startTrainingFrToEn(View v) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("vincent.moulin.vocab", MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("language_of_word_to_translate", "french");
        editor.apply();
        
        startActivity(new Intent(this, TrainingActivity.class));
    }
    
    public void startTrainingEnToFr(View v) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("vincent.moulin.vocab", MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("language_of_word_to_translate", "english");
        editor.apply();
        
        startActivity(new Intent(this, TrainingActivity.class));
    }
}
