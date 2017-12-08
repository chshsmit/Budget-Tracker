package christophershae.budgettracker;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Map;




public class MainBudgetScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_budget_screen);
        final TextView totalIncomeTextView = (TextView) findViewById(R.id.totalOutOfGoal);

        //toolbar setup
        Toolbar topToolBar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);

        //Initial progress bar setup
        final RoundCornerProgressBar progress1 = (RoundCornerProgressBar) findViewById(R.id.progress_1);
        progress1.setProgressColor(Color.parseColor("#79ff19"));
        progress1.setProgressBackgroundColor(Color.parseColor("#d8d8d8"));

        //Firebase Instantiation
        instantiateFirebaseVariables();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();

        //Getting the current weeks index
        currentWeeksDate = Utils.decrementDate(new Date());

        //Updating Information from Firebase
        updateInformationFromFirebase(progress1, totalIncomeTextView);

        //sets activity transitions for the bottom nav menu
        instantiateBottomNavigation();


    }

    //---------------------------------------------------------------------------------------------------------------------------------------------
    //Code for the bottom navigation
    //---------------------------------------------------------------------------------------------------------------------------------------------

    public void instantiateBottomNavigation()
    {
        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.idBottomNav);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent next_activity = null;
                switch (menuItem.getItemId()) {
                    case R.id.Enter_Man:
                        //transition to manual entry activity
                        next_activity = new Intent(MainBudgetScreen.this, ManualInputActivity.class);
                        startActivity(next_activity);
                        break;

                    case R.id.Recent_Purchases:
                        //transition to recent purchases activity
                        //must pass firebase info
                        Intent intent = new Intent(MainBudgetScreen.this, RecentPurchases.class);
                        Bundle args = new Bundle();
                        args.putSerializable("ARRAYLIST", (Serializable) currentWeeksBudget.getAllItems());
                        intent.putExtra("BUNDLE", args);
                        startActivity(intent);
                        break;

                    case R.id.Picture_Screen:
                        //transition to picture screen
                        next_activity = new Intent(MainBudgetScreen.this, Camera_Interface.class);
                        startActivity(next_activity);
                        break;

                    case R.id.Budget_Details:
                        //transition to budget details bar graph activity
                        next_activity = new Intent(MainBudgetScreen.this, BudgetDetailsBarGraph.class);
                        startActivity(next_activity);
                        break;
                }
                return true;
            }
        });
    }

    //----------------------------------------------------------------------------------------------------------------------------------------
    //Code for firebase integration
    //----------------------------------------------------------------------------------------------------------------------------------------

    //Global Variables for Firebase
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mFireBaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private String userId;

    private String currentWeeksDate;
    private WeekLongBudget currentWeeksBudget;


    public void updateInformationFromFirebase(final RoundCornerProgressBar progress1,final TextView totalIncomeTextView)
    {
        mFireBaseDatabase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("We are getting data from the database");

                //Instantiates this weeks budget and calculates the total spent
                currentWeeksBudget = dataSnapshot.child(userId).child(currentWeeksDate).getValue(WeekLongBudget.class);
                if (currentWeeksBudget == null) {
                    currentWeeksBudget = Utils.createNewWeek();
                    mFireBaseDatabase.child(userId).child(currentWeeksDate).setValue(currentWeeksBudget);
                }
                else {
                    currentWeeksBudget.calculateTotal();
                }

                //Setting Values in Progress Bar
                progress1.setMax(currentWeeksBudget.getGoalTotal().floatValue());
                progress1.setProgress(currentWeeksBudget.getTotalAmountSpent().floatValue());
                totalIncomeTextView.setText("$"+Utils.getStringToTwoDecimalPlaces(currentWeeksBudget.getTotalAmountSpent())+
                        "/$"+Utils.getStringToTwoDecimalPlaces(currentWeeksBudget.getGoalTotal()));

                //Set up the pie chart
                setUpPieChartLayout();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("You arent reDING CORRECTLTY");
            }
        } );
    }


    public void instantiateFirebaseVariables()
    {
        //Firebase authentication and database references
        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = Utils.getDatabase();
        mFireBaseDatabase = mFirebaseInstance.getReference("users");
    }


    //---------------------------------------------------------------------------------------------------------------------------------------------
    //Code for the pie chart
    //---------------------------------------------------------------------------------------------------------------------------------------------

    //Global Variables For PieChart
    PieChart pieChart;
    ArrayList<PieEntry> pieEntries = new ArrayList<PieEntry>();
    ArrayList<String> labels = new ArrayList<String>();
    PieDataSet dataSet = new PieDataSet(pieEntries, "Category");
    ArrayList<Integer> colors = new ArrayList<Integer>();


    //Instantiate the Pie Chart
    public void setUpPieChartLayout()
    {
        pieChart = (PieChart) findViewById(R.id.idPieChart);
        Description description = new Description();
        description.setTextColor(ColorTemplate.VORDIPLOM_COLORS[2]);
        description.setText("Price per Category");
        pieChart.setDescription(description);

        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(0f);
        pieChart.setTransparentCircleRadius(0);
        addDataSet();
    }

    //Create a dataset for the Pie Chart
    private void addDataSet()
    {
        //checks to see if there's data to add
        if(currentWeeksBudget.getCostOfAllCategories() == null) System.out.println("NULL");
        if(currentWeeksBudget.getCostOfAllCategories() == null) return;
        getDataForPieChart();

        //create the dataset
        dataSet.setSliceSpace(2);
        dataSet.setValueTextSize(12);

        //Add more colors
        addColorsToPieChart(dataSet);

        //custom data display MonetaryDisplay
        dataSet.setValueFormatter(new MonetaryDisplay());

        //make legend
        createPieChartLegend();

        //create pie data object
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        pieChart.invalidate();
    }

    //Retrieve the data from the current weeks budget
    public void getDataForPieChart()
    {
        int l = 0;
        pieEntries.clear();
        labels.clear();
        for (Map.Entry<String, Double> entry : currentWeeksBudget.costOfAllCategories.entrySet())
        {
            BigDecimal number = new BigDecimal(entry.getValue());

            int myInt = number.intValue();
            float myFloat = number.floatValue();
            if(myFloat != 0.00) {
                pieEntries.add(new PieEntry(myFloat, l));
                labels.add(entry.getKey());
                System.out.println(entry.getKey());
                l++;
            }
        }
    }

    //Adding more colors to the Pie Chart
    public void addColorsToPieChart(PieDataSet dataSet)
    {
        //add colors
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
    }

    //Creating the legend for the Pie Chart
    public void createPieChartLegend()
    {
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);

        //prepare legend entries
        List<LegendEntry> entries = new ArrayList<>();

        for (int i = 0; i < pieEntries.size(); i++) {
            LegendEntry entry = new LegendEntry();
            entry.formColor = colors.get(i);
            entry.label = labels.get(i);
            entries.add(entry);
        }

        legend.setCustom(entries);
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------
    //Code to change activities
    //---------------------------------------------------------------------------------------------------------------------------------------------


    //We want the code to do nothing when the back button is pressed
    @Override
    public void onBackPressed(){}


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.Settings)
        {
            Intent setting = new Intent(MainBudgetScreen.this, SettingsActivity.class);
            startActivity(setting);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------
    //Code for the toolbar up top
    //---------------------------------------------------------------------------------------------------------------------------------------------

    //ToolBar function to setup res/menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }
}
