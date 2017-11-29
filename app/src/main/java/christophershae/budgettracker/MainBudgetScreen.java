package christophershae.budgettracker;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import java.util.Map;

import static christophershae.budgettracker.R.id.Enter_Man;
import static christophershae.budgettracker.R.id.Picture_Screen;
import static christophershae.budgettracker.R.id.Recent_Purchases;
import static christophershae.budgettracker.R.id.Settings;
//import static christophershae.budgettracker.R.id.textView;


public class MainBudgetScreen extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mFireBaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private String userId;

    SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyy");

    //These are variables for the current weeks date, and the budget for the current week
    private String currentWeeksDate;
    private WeekLongBudget currentWeeksBudget;
    PieChart pieChart;


//    public void onClick(@NonNull MenuItem item) {
//        Class next_activity = null;
//        switch (item.getItemId()) {
//            case R.id.Enter_Man:
//                Toast.makeText(MainBudgetScreen.this,
//                        "Action Manual Add Clicked", Toast.LENGTH_SHORT).show();
//                //next_activity = ManualInputActivity.class;
//                break;
//
//            case R.id.Recent_Purchases:
//                Toast.makeText(MainBudgetScreen.this,
//                        "Action Recent Purchases Clicked", Toast.LENGTH_SHORT).show();
//                //next_activity = RecentPurchases.class;
//                break;
//
//            case R.id.Picture_Screen:
//                Toast.makeText(MainBudgetScreen.this,
//                        "Action Scan Clicked", Toast.LENGTH_SHORT).show();
//                //next_activity = Camera_Interface.class;
//                break;
//        }
//        //startActivity(new Intent(this, next_activity));
//    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_budget_screen);
        final TextView totalIncomeTextView = (TextView) findViewById(R.id.Total_Spent);


        //define Buttons from main screen
//        Button enter = (Button) findViewById(Enter_Man);
//        enter.setOnClickListener(this);
        Button settings = (Button) findViewById(Settings);
        settings.setOnClickListener(this);
//        Button photo = (Button) findViewById(Picture_Screen);
//        photo.setOnClickListener(this);


        //Firebase stuff
        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = Utils.getDatabase();
        mFireBaseDatabase = mFirebaseInstance.getReference("users");


        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();


        //Getting the current weeks index
        currentWeeksDate = Utils.decrementDate(new Date());

        mFireBaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("We are getting data from the database");

                currentWeeksBudget = dataSnapshot.child(userId).child(currentWeeksDate).getValue(WeekLongBudget.class);  //This instantiates this weeks budget
                totalIncomeTextView.setText("$"+currentWeeksBudget.getTotalAmountSpent());

                pieChart = (PieChart) findViewById(R.id.idPieChart);

                Description description = new Description();
                description.setTextColor(ColorTemplate.VORDIPLOM_COLORS[2]);
                description.setText("Price per Category");
                pieChart.setDescription(description);
                pieChart.setRotationEnabled(true);
                //pieChart.setUsePercentValues(true);
                pieChart.setHoleRadius(0f);
                pieChart.setTransparentCircleRadius(0);
                //pieChart.setCenterText("Maybe a button");
                //pieChart.setCenterTextSize(10);

                addDataSet(pieChart);

                System.out.println("This is the current weeks start date: ");
                //System.out.println(currentWeeksBudget.getStartDate());


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("You arent reDING CORRECTLTY");
            }
        });

        System.out.println("The current user ID is: " +userId);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.idBottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent next_activity = null;
                switch (menuItem.getItemId()) {
                    case R.id.Enter_Man:
                        Toast.makeText(MainBudgetScreen.this,
                                "Action Manual Add Clicked", Toast.LENGTH_SHORT).show();
                        next_activity = new Intent(MainBudgetScreen.this, ManualInputActivity.class);
                        startActivity(next_activity);
                        break;

                    case R.id.Recent_Purchases:
                        Toast.makeText(MainBudgetScreen.this,
                                "Action Recent Purchases Clicked", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainBudgetScreen.this, RecentPurchases.class);
                        Bundle args = new Bundle();
                        args.putSerializable("ARRAYLIST", (Serializable) currentWeeksBudget.getAllItems());
                        intent.putExtra("BUNDLE", args);
                        startActivity(intent);
                        break;

                    case R.id.Picture_Screen:
                        Toast.makeText(MainBudgetScreen.this,
                                "Action Scan Clicked", Toast.LENGTH_SHORT).show();
                        next_activity = new Intent(MainBudgetScreen.this, Camera_Interface.class);
                        startActivity(next_activity);
                        break;
                }
                return true;
            }
        });

        //Button purchases = (Button) findViewById(R.id.Recent_Purchases);

        //purchases.setOnClickListener(new View.OnClickListener() {

//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainBudgetScreen.this, RecentPurchases.class);
//                Bundle args = new Bundle();
//                args.putSerializable("ARRAYLIST", (Serializable) currentWeeksBudget.getAllItems());
//                intent.putExtra("BUNDLE", args);
//                startActivity(intent);
//            }
//        });


        if(firebaseAuth.getCurrentUser() == null){
            System.out.println("You are not signed in");
        } else {
            System.out.println("You are signed in on the main page: oncreate");
        }



    }


    private void addDataSet(PieChart chart){
        //checks to see if there's data to add

        ArrayList<PieEntry> pieEntries = new ArrayList<PieEntry>();
        ArrayList<String> labels = new ArrayList<String>();

        int l = 0;
        for (Map.Entry<String, Double> entry : currentWeeksBudget.costOfAllCategories.entrySet())
        {
            BigDecimal number = new BigDecimal(entry.getValue());

            int myInt = number.intValue();
            float myFloat = number.floatValue();
            if(myFloat != 0.00) {
                pieEntries.add(new PieEntry(myFloat, l));
                labels.add(entry.getKey());
                l++;
            }
        }


        //create the dataset
        PieDataSet dataSet = new PieDataSet(pieEntries, "Category");
        dataSet.setSliceSpace(2);
        dataSet.setValueTextSize(12);

        //add colors
        ArrayList<Integer> colors = new ArrayList<>();

        dataSet.setColors(ColorTemplate.COLORFUL_COLORS); // set the color<br />

        //custom data display MonetaryDisplay
        dataSet.setValueFormatter(new MonetaryDisplay());

        //make legend
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        //legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);


        //create pie data object
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();

    }

    @Override
    public void onBackPressed(){}


    @Override
    public void onClick(View view) {
        switch(view.getId()) {
//            case Enter_Man:
//                Intent manual_input = new Intent(MainBudgetScreen.this, ManualInputActivity.class);
//                startActivity(manual_input);
//                break;
            case Settings:
                Intent setting = new Intent(MainBudgetScreen.this, SettingsActivity.class);
                startActivity(setting);
                break;
//            case Picture_Screen:
//                Intent picture_screen = new Intent(MainBudgetScreen.this, Camera_Interface.class);
//                startActivity(picture_screen);
        }

    }
}
