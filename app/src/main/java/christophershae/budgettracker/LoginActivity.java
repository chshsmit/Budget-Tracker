package christophershae.budgettracker;

import com.google.firebase.auth.FirebaseAuth;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;


import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password. Uses Firebase Authentication
 */
public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_READ_CONTACTS = 0;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignup;
    private FirebaseAuth firebaseAuth;
    private Button buttonSignIn;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //toolbar setup
        Toolbar topToolBar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);

        //reads input from edittext fields on the layout
        firebaseAuth = FirebaseAuth.getInstance();
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);

        // user creates account
        TextView signupLink = (TextView) findViewById(R.id.signUp);
        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
                Toast.makeText(LoginActivity.this, "Account Created", Toast.LENGTH_LONG).show();
            }
        });

        //User forgot password
        TextView forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
                Toast.makeText(LoginActivity.this, "Forgot Password", Toast.LENGTH_LONG).show();
            }
        });

        //checks if user must sign in
        if(firebaseAuth.getCurrentUser() == null){
            System.out.println("You are not signed in");
        } else {
            System.out.println("You are signed in");
            changeToMainBudgetScreen();
        }
    }

    @Override
    protected void onResume(){
        //checks if user must sign in
        super.onResume();
        if(firebaseAuth.getCurrentUser() == null){
            System.out.println("You are not signed in");
        } else {
            System.out.println("You are signed in");
        }
    }

    //Checks credentials and internet connection
    //if both are valid go to mainbudgetscreen
    public void Simple_Nav(View view){
        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();

        //returns out of function if no credentials or connection
        if(checkConnectionAndInput(email,password)==false)
        {
            return;
        }
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, Log a message to the LogCat. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            // there was an error
                            Toast.makeText(LoginActivity.this, "Incorrect Email/Password", Toast.LENGTH_LONG).show();

                        }
                        else {
                            changeToMainBudgetScreen();
                        }
                    }
                });
    }

    //creates a new user from the edittext fields
    private void registerUser()
    {
        //getting email and password from edit texts
        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();

        //returns out of function if no credentials or connection
        if(checkConnectionAndInput(email,password)==false)
        {
            return;
        }
        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password);
    }

    //helper function to transition to mainbudgetscreen
    public void changeToMainBudgetScreen(){
        //Intent next_activity = new Intent(LoginActivity.this, MainBudgetScreen.class);
        Intent next_activity = new Intent(LoginActivity.this, Splash.class);
        startActivity(next_activity);
        finish();
    }

    //checks both both input and connection and returns a boolean
    private boolean checkConnectionAndInput(String email, String password)
    {
        //checks if fields are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return false;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return false;
        }
        //check if there is internet connection

        return checkConnection();
    }

    //checks internet connection and returns a bool depending on result
    public boolean checkConnection()
    {
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(!isConnected){
            Toast.makeText(this,"Check Internet Connection",Toast.LENGTH_LONG).show();
            return false;
        }
        else
        {
            return true;
        }
    }

    // function to reset the users password if forgotten
    private String userEmail;
    public void resetPassword(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        final EditText inputEmail = new EditText(this);
        inputEmail.setHint("example@example.com");
        alertDialogBuilder.setView(inputEmail);

        alertDialogBuilder.setTitle("Send Password Reset Email");
        alertDialogBuilder.setPositiveButton("Send",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1)
                    {
                        userEmail = inputEmail.getText().toString().trim();

                        //send email to user
                        firebaseAuth = FirebaseAuth.getInstance();
                        firebaseAuth.sendPasswordResetEmail(userEmail)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            System.out.println("Email Sent");
                                            Toast.makeText(LoginActivity.this, "Sent Password Reset Email", Toast.LENGTH_LONG).show();
                                        }else{
                                            Toast.makeText(LoginActivity.this, "No Account With That Email.", Toast.LENGTH_LONG).show();
                                            System.out.println("You failed");
                                        }
                                    }
                                });


                    }
                });

        //cancel button and alert
        alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
