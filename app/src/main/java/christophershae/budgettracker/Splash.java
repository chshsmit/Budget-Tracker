package christophershae.budgettracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

//splash for coldstarting but login
public class Splash extends AppCompatActivity 
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MainBudgetScreen.class);
        startActivity(intent);
        finish();
    }
}