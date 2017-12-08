package christophershae.budgettracker;

import com.google.firebase.auth.FirebaseAuth;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;


//----------------------------------------------------------------------------------------------------------------------------------------
//  A login screen that offers login via email/password. Uses Firebase Authentication.
//----------------------------------------------------------------------------------------------------------------------------------------
public class LoginActivity extends AppCompatActivity
{

    private EditText editTextEmail;
    private EditText editTextPassword;

    // Accesses Firebase
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Toolbar setup
        Toolbar topToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);

        // Reads input from edittext fields on the layout
        firebaseAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);

        // User account creation
        TextView signupLink = findViewById(R.id.signUp);
        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
                Utils.toastMessage("Account Created", LoginActivity.this);
            }
        });

        // If user forgets password
        TextView forgotPassword = findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
                Utils.toastMessage("Forgot Password", LoginActivity.this);
            }
        });

        // Checks if user must sign in
        if(firebaseAuth.getCurrentUser() == null)
        {
            System.out.println("You are not signed in");
        } else {
            System.out.println("You are signed in");
            changeToMainBudgetScreen();
        }
    }

    @Override
    protected void onResume()
    {
        // Checks if user must sign in
        super.onResume();
        if(firebaseAuth.getCurrentUser() == null)
        {
            System.out.println("You are not signed in");
        } else {
            System.out.println("You are signed in");
        }
    }

    // Checks credentials and internet connection
    // If both are valid go to MainBudgetScreen
    public void Simple_Nav(View view)
    {
        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();

        // Returns out of function if no credentials or connection
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
                        if (!task.isSuccessful())
                        {
                            // There was an error
                            Utils.toastMessage("Incorrect Email/Password", LoginActivity.this);
                        }
                        else {
                            changeToMainBudgetScreen();
                        }
                    }
                });
    }

    // Creates a new user from the edittext fields
    private void registerUser()
    {
        // Getting email and password from edittexts
        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();

        // Returns out of function if no credentials or connection
        if(checkConnectionAndInput(email,password)==false)
        {
            return;
        }
        // Creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password);
    }

    // Helper function to transition to MainBudgetScreen
    public void changeToMainBudgetScreen()
    {
        Intent next_activity = new Intent(LoginActivity.this, Splash.class);
        startActivity(next_activity);
        finish();
    }

    // Checks0 both input and connection and returns a boolean
    private boolean checkConnectionAndInput(String email, String password)
    {
        // Checks if fields are empty
        if(TextUtils.isEmpty(email))
        {
            Utils.toastMessage("Please enter email", this);
            return false;
        }
        if(TextUtils.isEmpty(password))
        {
            Utils.toastMessage("Please enter password", this);
            return false;
        }
        // Check if there is internet connection
        return checkConnection();
    }

    // Checks internet connection and returns a bool depending on result
    public boolean checkConnection()
    {
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(!isConnected){
            Utils.toastMessage("Check Internet Connection", this);
            return false;
        }
        else
        {
            return true;
        }
    }

    // Function to reset the users password if forgotten
    private String userEmail;
    public void resetPassword()
    {
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

                        // Send emails to user
                        firebaseAuth = FirebaseAuth.getInstance();
                        firebaseAuth.sendPasswordResetEmail(userEmail)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            System.out.println("Email Sent");
                                            Utils.toastMessage("Sent Password Reset Email", LoginActivity.this);
                                        }else{
                                            Utils.toastMessage("No Account With That Email.", LoginActivity.this);
                                            System.out.println("You failed");
                                        }
                                    }
                                });
                    }
                });

        // Cancels button and alert
        alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {}
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
