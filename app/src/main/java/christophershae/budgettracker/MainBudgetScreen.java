package christophershae.budgettracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static christophershae.budgettracker.R.id.Enter_Man;
import static christophershae.budgettracker.R.id.Picture_Screen;
import static christophershae.budgettracker.R.id.Recent_Purchases;
import static christophershae.budgettracker.R.id.Settings;

public class MainBudgetScreen extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_budget_screen);
//define Buttons from main screen
        Button enter = (Button) findViewById(Enter_Man);
        enter.setOnClickListener(this);
        Button settings = (Button) findViewById(Settings);
        settings.setOnClickListener(this);
        Button photo = (Button) findViewById(Picture_Screen);
        photo.setOnClickListener(this);
        Button purchases = (Button) findViewById(R.id.Recent_Purchases);
        purchases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainBudgetScreen.this, RecentPurchases.class));
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case Enter_Man:
                Intent manual_input = new Intent(MainBudgetScreen.this, ManualInputActivity.class);
                startActivity(manual_input);
                break;
            case Settings:
                Intent setting = new Intent(MainBudgetScreen.this, SettingsActivity.class);
                startActivity(setting);
                break;
            case Picture_Screen:
                Intent picture_screen = new Intent(MainBudgetScreen.this, Camera_Interface.class);
                startActivity(picture_screen);
        }

    }
}
