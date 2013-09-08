package vincent.moulin.vocab;

import vincent.moulin.vocab.R;
import vincent.moulin.vocab.database.DatabaseHelper;
import vincent.moulin.vocab.entities.Dico;
import vincent.moulin.vocab.menu.DefaultMenuManager;
import java.util.HashMap;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class StatActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        HashMap<String, int[]> statistiques;
        int[] partStatFrToEn, partStatEnToFr;
        TextView textView;
        DatabaseHelper dbh = new DatabaseHelper(this);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);
        
        statistiques = Dico.calcStatistiques(dbh);
        
        //Statistiques FRA -> ENG
        partStatFrToEn = statistiques.get("FrToEn");
        
        textView = (TextView)findViewById(R.id.stat_fr_to_en_known_value_id);
        textView.setText(Integer.toString(partStatFrToEn[Constantes.MODE_KNOWN]));
        
        textView = (TextView)findViewById(R.id.stat_fr_to_en_learning_in_progress_value_id);
        textView.setText("\n" + partStatFrToEn[Constantes.MODE_LEARNING_IN_PROGRESS]);
        
        textView = (TextView)findViewById(R.id.stat_fr_to_en_never_answered_value_id);
        textView.setText(Integer.toString(partStatFrToEn[Constantes.MODE_NEVER_ANSWERED]));
        
        textView = (TextView)findViewById(R.id.stat_fr_to_en_total_value_id);
        textView.setText(
            Integer.toString(
                partStatFrToEn[Constantes.MODE_KNOWN]
                + partStatFrToEn[Constantes.MODE_LEARNING_IN_PROGRESS]
                + partStatFrToEn[Constantes.MODE_NEVER_ANSWERED]
            )
        );
        //-----------------------------------------------------------------
        
        //Statistiques ENG -> FRA
        partStatEnToFr = statistiques.get("EnToFr");
        
        textView = (TextView)findViewById(R.id.stat_en_to_fr_known_value_id);
        textView.setText(Integer.toString(partStatEnToFr[Constantes.MODE_KNOWN]));
        
        textView = (TextView)findViewById(R.id.stat_en_to_fr_learning_in_progress_value_id);
        textView.setText("\n" + partStatEnToFr[Constantes.MODE_LEARNING_IN_PROGRESS]);
        
        textView = (TextView)findViewById(R.id.stat_en_to_fr_never_answered_value_id);
        textView.setText(Integer.toString(partStatEnToFr[Constantes.MODE_NEVER_ANSWERED]));
        
        textView = (TextView)findViewById(R.id.stat_en_to_fr_total_value_id);
        textView.setText(
            Integer.toString(
                partStatEnToFr[Constantes.MODE_KNOWN]
                + partStatEnToFr[Constantes.MODE_LEARNING_IN_PROGRESS]
                + partStatEnToFr[Constantes.MODE_NEVER_ANSWERED]
            )
        );
        //-----------------------------------------------------------------
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

}
