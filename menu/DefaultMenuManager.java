package vincent.moulin.vocab.menu;

import vincent.moulin.vocab.R;
import vincent.moulin.vocab.PhoneticRulesActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.MenuItem;

public class DefaultMenuManager {
    
    public static boolean onOptionsItemSelected(MenuItem item, Activity activity) {
        switch (item.getItemId()) {
            case R.id.action_phonetic_rules_id:
                activity.startActivity(new Intent(activity, PhoneticRulesActivity.class));
                return true;
            case R.id.action_about_id:
                new AlertDialog.Builder(activity)
                    .setMessage(R.string.about_content)
                    .setNeutralButton(R.string.closure_button_content, null)
                    .show();
                return true;
        }
        return false;
    }
    
}
