package christophershae.budgettracker;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
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




public class MainBudgetScreen extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mFireBaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private String userId;

    SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyy");

    //These are variables for the current weeks date, and the budget for the current week
    private String currentWeeksDate;
    private WeekLongBudget currentWeeksBudget;





    private double totalSpent;
    private float[] ydata = {800.00f, 100.00f, 75.00f, 300f};
    private String[] xdata = {"Rent", "Drugs", "Util", "Food"};
    PieChart pieChart;


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


        //Firebase stuff
        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFireBaseDatabase = mFirebaseInstance.getReference("users");


        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();

        //Getting the current weeks index
        currentWeeksDate = decrementDate(new Date());
        mFireBaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("We are getting data from the database");

                currentWeeksBudget = dataSnapshot.child(userId).child(currentWeeksDate).getValue(WeekLongBudget.class);  //This instantiates this weeks budget

                System.out.println("This is the current weeks start date: ");
                //System.out.println(currentWeeksBudget.getStartDate());


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("You arent reDING CORRECTLTY");
            }
        });

        System.out.println("The current user ID is: " +userId);

        Button purchases = (Button) findViewById(R.id.Recent_Purchases);

        purchases.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainBudgetScreen.this, RecentPurchases.class));
            }
        });


        if(firebaseAuth.getCurrentUser() == null){
            System.out.println("You are not signed in");
        } else {
            System.out.println("You are signed in on the main page: oncreate");
        }


    }

    @Override
    protected void onResume(){
        super.onResume();

        if(firebaseAuth.getCurrentUser() == null){
            System.out.println("You are not signed in");
        } else {
            System.out.println("You are signed in on the main page: onresume");
        }

        pieChart = (PieChart) findViewById(R.id.idPieChart);

        pieChart.setDescription("Sales by Category");
        pieChart.setRotationEnabled(true);
        //pieChart.setUsePercentValues(true);
        pieChart.setHoleRadius(0f);
        //pieChart.setCenterText("Maybe a button");
        //pieChart.setCenterTextSize(10);

        addDataSet(pieChart);

        TextView textView = (TextView) findViewById(R.id.Total_Spent);
        textView.setText("$"+(int)totalSpent);

    }





    private void addDataSet(PieChart chart){
        ArrayList<Entry> pieEntries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        totalSpent = 0;
        for(int i = 0; i < ydata.length; i++){
            totalSpent += ydata[i];
            pieEntries.add(new Entry(ydata[i], i));
        }

        for(int i = 0; i < xdata.length; i++){
            labels.add(xdata[i]);
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
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

        //create pie data object
        PieData pieData = new PieData(labels, dataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();

    }

    @Override
    public void onBackPressed(){}


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

    //This function decrements the date so it adds it to the correct weeklong budget
    public String decrementDate(Date date)
    {

        //Get an instance of the calenday and get the current day of the week
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        //Depending on what day it is, decrement the date to be the most recent sunday
        //If it is Sunday, then it won't change the date at all
        switch(day){
            case Calendar.MONDAY:
                calendar.setTime(date);
                calendar.add(Calendar.DATE, -1);
                date = calendar.getTime();
                break;

            case Calendar.TUESDAY:
                calendar.setTime(date);
                calendar.add(Calendar.DATE, -2);
                date = calendar.getTime();
                break;

            case Calendar.WEDNESDAY:
                calendar.setTime(date);
                calendar.add(Calendar.DATE, -3);
                date = calendar.getTime();
                break;

            case Calendar.THURSDAY:
                calendar.setTime(date);
                calendar.add(Calendar.DATE, -4);
                date = calendar.getTime();
                break;

            case Calendar.FRIDAY:
                calendar.setTime(date);
                calendar.add(Calendar.DATE, -5);
                date = calendar.getTime();
                break;

            case Calendar.SATURDAY:
                calendar.setTime(date);
                calendar.add(Calendar.DATE, -6);
                date = calendar.getTime();
                break;

            default:
                break;
        }


        return sdf.format(date);   //return the decremented date as a string
    }
}
