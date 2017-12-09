package christophershae.budgettracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.auth.FirebaseAuth;
import android.widget.EditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Date;

public class SettingsActivity extends AppCompatPreferenceActivity {


    //This is the global variable to reference the current weeks budget
    public WeekLongBudget currentWeeksBudget;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        //App Bar
        setUpAppBar();

        int horizontalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        int verticalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        int topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (int) getResources().getDimension(R.dimen.activity_vertical_margin) , getResources().getDisplayMetrics());
        getListView().setPadding(horizontalMargin, topMargin, horizontalMargin, verticalMargin);

        //sets up firebase
        instantiateFirebase();

        //sets up preferences buttons
        instantiatePrefs();
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------
    //These functions instantiate the app bar, database, and the preferences within settings
    //---------------------------------------------------------------------------------------------------------------------------------------------
    public void setUpAppBar()
    {
        getLayoutInflater().inflate(R.layout.toolbar, (ViewGroup)findViewById(android.R.id.content));
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.backbut));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    //Firebase Variables
    private DatabaseReference mFireBaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private String currentDate;

    public void instantiateFirebase()
    {
        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = Utils.getDatabase();
        mFireBaseDatabase = mFirebaseInstance.getReference("users");
        currentDate = Utils.decrementDate(new Date());
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();

        //Gets current week data from firebase and puts it onto the perferences screen
        mFireBaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentWeeksBudget = dataSnapshot.child(userId).child(currentDate).getValue(WeekLongBudget.class);
                if (currentWeeksBudget != null) {
                    findPreference("totalSpent").setTitle("Current Week Total Spent:" +Utils.getStringToTwoDecimalPlaces(currentWeeksBudget.getTotalAmountSpent()));
                    findPreference("goalBudget").setTitle("Current Week Goal Budget: "+Utils.getStringToTwoDecimalPlaces(currentWeeksBudget.getGoalTotal()));
                    findPreference("income").setTitle("Current Week Income: " +Utils.getStringToTwoDecimalPlaces(currentWeeksBudget.getTotalIncomeAccumulated()));
                    findPreference("netIncome").setTitle("Current Week Net Income: "+Utils.getStringToTwoDecimalPlaces(currentWeeksBudget.getNetIncome()));
                }
                else {
                    System.out.println("There is no existing week in Firebase!");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void instantiatePrefs()
    {
        //sets up preferences buttons
        Preference goalPref = (Preference) findPreference("changeGoal");
        goalPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                changeWeeklyGoal();
                return true;
            }
        });

        Preference incomePerf = (Preference) findPreference("changeIncome");
        incomePerf.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                changeIncome();
                return true;
            }
        });

        Preference deletePref = (Preference) findPreference("deleteItem");
        deletePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                deleteItemFromBudget();
                return true;
            }
        });

        Preference singoutPref = (Preference) findPreference("signout");
        singoutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                signOut();
                return true;
            }
        });

        Preference about = (Preference) findPreference("about");
        about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                about();
                return true;
            }
        });
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------
    //This code deals with firebase authentication and changing to appropriate avtivities
    //---------------------------------------------------------------------------------------------------------------------------------------------
    //sign out of firebase user
    public void signOut(){
        System.out.println("You did it");
        firebaseAuth.signOut();
        changeToLoginScreen();
    }

    //helper function to go to login screen
    private void changeToLoginScreen(){
        Intent login = new Intent(getApplicationContext(), LoginActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(login);
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------
    //These functions create a dialog to change the users weekly goal budget
    //---------------------------------------------------------------------------------------------------------------------------------------------
    //brings up dialog alert for changing goal budget
    private String newGoalBudget;
    public void changeWeeklyGoal()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        setUpGoalDialog(alertDialogBuilder);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void setUpGoalDialog(AlertDialog.Builder alertDialogBuilder)
    {
        final EditText goalInput = new EditText(this);
        goalInput.setHint("Weekly Goal");
        goalInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        alertDialogBuilder.setView(goalInput);

        alertDialogBuilder.setTitle("Set this week's goal!");
        alertDialogBuilder.setPositiveButton("Set",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1)
                    {
                        newGoalBudget = goalInput.getText().toString();
                        try{
                            currentWeeksBudget.setGoalTotal(Double.valueOf(newGoalBudget));
                        } catch(NumberFormatException e){
                            Utils.toastMessage("Input Valid Number", SettingsActivity.this);
                            changeWeeklyGoal();
                            return;
                        }

                        mFireBaseDatabase.child(userId).child(currentDate).setValue(currentWeeksBudget);
                        Utils.toastMessage("Updated Weekly Goal", SettingsActivity.this);
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
                //User pressed cancel so do nothing
            }
        });
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------
    //These functions create a dialog to change the users income
    //---------------------------------------------------------------------------------------------------------------------------------------------
    private String newIncome;
    public void changeIncome()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        setUpIncomeDialog(alertDialogBuilder);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void setUpIncomeDialog(AlertDialog.Builder alertDialogBuilder)
    {
        final EditText incomeInput = new EditText(this);
        incomeInput.setHint("Weekly Income");
        incomeInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL |
                InputType.TYPE_NUMBER_FLAG_SIGNED);
        alertDialogBuilder.setView(incomeInput);

        alertDialogBuilder.setTitle("Set this week's income!");
        alertDialogBuilder.setPositiveButton("Set",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1)
                    {
                        newIncome = incomeInput.getText().toString();
                        try {
                            currentWeeksBudget.addMoneyToIncome(Double.valueOf(newIncome));
                        } catch(NumberFormatException e){
                            Utils.toastMessage("Input Valid Number", SettingsActivity.this);
                            changeIncome();
                            return;
                        }

                        mFireBaseDatabase.child(userId).child(currentDate).setValue(currentWeeksBudget);
                        Utils.toastMessage("Added Income to Week", SettingsActivity.this);

                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
                //User pressed cancel so do nothing
            }
        });
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------
    //These fucntions create a dialog to delete an item from the current week
    //---------------------------------------------------------------------------------------------------------------------------------------------
    int deletedItemIndex;
    public void deleteItemFromBudget()
    {
        //This for loop gets every item name from the current weeks budget
        ArrayList<String> itemNames = new ArrayList<>();
        for(Item item: currentWeeksBudget.allItems)
        {
            itemNames.add(item.name);
        }

        //This builds the pop up dialog
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        setUpDeleteDialog(alertDialogBuilder, itemNames);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void setUpDeleteDialog(AlertDialog.Builder alertDialogBuilder, ArrayList<String> itemNames)
    {
        alertDialogBuilder.setTitle("Select an item to delete:");
        alertDialogBuilder.setSingleChoiceItems(itemNames.toArray(new CharSequence[itemNames.size()]),0, null);
        alertDialogBuilder.setPositiveButton("Delete",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1)
                    {
                        //Get the selected item index and remove it from the current budget
                        deletedItemIndex = ((AlertDialog)arg0).getListView().getCheckedItemPosition();
                        currentWeeksBudget.removeItem(deletedItemIndex);
                        mFireBaseDatabase.child(userId).child(currentDate).setValue(currentWeeksBudget);
                        Utils.toastMessage("Deleted Item", SettingsActivity.this);
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
                //User pressed cancel so do nothing
            }
        });
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------
    //This function takes you to the about activity
    //---------------------------------------------------------------------------------------------------------------------------------------------
    public void about()
    {
        Intent about = new Intent(getApplicationContext(), AboutPage.class);
        startActivity(about);
    }

}
