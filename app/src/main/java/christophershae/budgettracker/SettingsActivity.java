package christophershae.budgettracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static christophershae.budgettracker.R.id.signout;


import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

//import static christophershae.budgettracker.R.id.signout;

public class SettingsActivity extends AppCompatActivity{
    private Button buttonSignOut;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        //get current weeklongbudget

        TextView textView = (TextView) findViewById(R.id.weekbudget);
        textView.setText("$"+(int)1275);//change to display real time

        firebaseAuth = FirebaseAuth.getInstance();
        buttonSignOut = (Button) findViewById(R.id.signout);

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

    public void changeWeeklyGoal(View v)
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


    public void setGoal(View v)
    {

    }

    public void closeDiag(View v)
    {

    }
}
