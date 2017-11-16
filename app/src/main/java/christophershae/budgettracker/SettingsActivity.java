package christophershae.budgettracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static android.R.id.input;
import static christophershae.budgettracker.R.id.goalInput;
import static christophershae.budgettracker.R.id.signout;
import static christophershae.budgettracker.R.id.textView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import static christophershae.budgettracker.R.id.signout;

public class SettingsActivity extends AppCompatActivity{
    private Button buttonSignOut;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mFireBaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private String userId;
    private String currentDate;

    private WeekLongBudget currentWeeksBudget;
    Map<String, WeekLongBudget> usersBudgets = new HashMap<>();




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        //get current weeklongbudget

        final TextView textView = (TextView) findViewById(R.id.weekbudget);



        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFireBaseDatabase = mFirebaseInstance.getReference("users");
        buttonSignOut = (Button) findViewById(R.id.signout);

        currentDate = Utils.decrementDate(new Date());

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();

        mFireBaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               currentWeeksBudget = dataSnapshot.child(userId).child(currentDate).getValue(WeekLongBudget.class);
                textView.setText("$"+currentWeeksBudget.getTotalAmountSpent());//change to display real time
               System.out.println(currentWeeksBudget.getStartDate());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("You arent reDING CORRECTLTY");
            }
        });


    }

    public void signOut(View v){
        firebaseAuth.signOut();
        changeToLoginScreen();
    }

    private void changeToLoginScreen(){
        Intent login = new Intent(getApplicationContext(), LoginActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(login);
    }

    public void changeIncome(View v)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        alertDialogBuilder.setView(inflater.inflate(R.layout.goal_budget_diag, null));

        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1)
                    {

                    }
                });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {

            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private String newGoalBudget;

    public void changeWeeklyGoal(View v)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        //LayoutInflater inflater = this.getLayoutInflater();
        //alertDialogBuilder.setView(inflater.inflate(R.layout.goal_budget_diag, null));
        final EditText goalInput = new EditText(this);
        goalInput.setHint("Weekly Goal");
        alertDialogBuilder.setView(goalInput);

        alertDialogBuilder.setTitle("Set this week's goal!");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1)
                    {
                        newGoalBudget = goalInput.getText().toString();
                        currentWeeksBudget.setGoalTotal(Double.valueOf(newGoalBudget));

                        mFireBaseDatabase.child(userId).child(currentDate).setValue(currentWeeksBudget);

                        Toast.makeText(SettingsActivity.this, "Updated Weekly Goal", Toast.LENGTH_LONG).show();

                    }
                });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {

            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public void setGoal(View v)
    {

    }

    public void closeDiag(View v)
    {

    }

}
