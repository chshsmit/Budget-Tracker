package christophershae.budgettracker;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import static christophershae.budgettracker.R.id.signout;


import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

//import static christophershae.budgettracker.R.id.signout;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatActivity{
    private Button buttonSignOut;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        firebaseAuth = FirebaseAuth.getInstance();

        buttonSignOut = (Button) findViewById(R.id.signout);

    }

    public void signOut(View v){
        System.out.println("You did it");
        firebaseAuth.signOut();
        changeToLoginScreen();
    }

//    @Override
//    public void onClick(View view) {
//        switch(view.getId()) {
//            case signout:
//                System.out.println("You did it");
//                firebaseAuth.signOut();
//                changeToLoginScreen();
//                break;
//
//        }
//
//    }

    private void changeToLoginScreen(){
        Intent login = new Intent(getApplicationContext(), LoginActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(login);
    }
}
