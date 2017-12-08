package christophershae.budgettracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Map;

public class BudgetDetailsBarGraph extends AppCompatActivity
{
    //----------------------------------------------------------------------------------------------------------------------------------------
    // Initialization of global variables
    //----------------------------------------------------------------------------------------------------------------------------------------
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mFireBaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private String userId;

    // These are variables for the current weeks date, all previous week dates, and the budget for the current week
    private String currentWeeksDate;
    private String prevWeeksDate;
    private Date prevDate;
    private WeekLongBudget currentWeeksBudget;
    private ArrayList<WeekLongBudget> allWeekBudgets = new ArrayList<>();
    private ArrayList<String> allWeeks = new ArrayList<>();   //for x-axis values
    BarChart barGraph;

    //----------------------------------------------------------------------------------------------------------------------------------------
    // Utility functions for bar graph
    //----------------------------------------------------------------------------------------------------------------------------------------

    // Reformats display of expenses on top of each stacked bar
    public class StackedValueFormatter implements IValueFormatter
    {
        private boolean mDrawWholeStack;
        private String mAppendix;
        private DecimalFormat mFormat;

        public StackedValueFormatter(boolean drawWholeStack, String appendix, int decimals)
        {
            this.mDrawWholeStack = drawWholeStack;
            this.mAppendix = appendix;

            StringBuffer b = new StringBuffer();
            for (int i = 0; i < decimals; i++) {
                if (i == 0)
                    b.append(".");
                b.append("0");
            }
            this.mFormat = new DecimalFormat("###,###,###,##0" + b.toString());
        }
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler)
        {
            if (!mDrawWholeStack && entry instanceof BarEntry)
            {
                BarEntry barEntry = (BarEntry) entry;
                float[] vals = barEntry.getYVals();
                if (vals != null) {

                    // Find out if we are on top of the stack
                    if (vals[vals.length - 1] == value)
                    {
                        return  mAppendix + mFormat.format(value) + " [" +
                                mAppendix + mFormat.format(barEntry.getY()) + "]";
                    } else {
                        return ""; // return empty
                    }
                }
            }
            // Return the "proposed" value
            return mAppendix + mFormat.format(value);
        }
    }

    // Reformats y-axis value display
    public class MyYAxisValueFormatter implements IAxisValueFormatter
    {
        private DecimalFormat mFormat;

        public MyYAxisValueFormatter()
        {
            // Formats values to 1 decimal digit
            mFormat = new DecimalFormat("###,###,##0.0");
        }
        @Override
        public String getFormattedValue(float value, AxisBase axis)
        {
            // "value" represents the position of the label on the axis (x or y)
            return "$" + mFormat.format(value);
        }
    }

    // Reformats date strings to mm/dd to save space on x-axis
    public String formatDateLabel(String date)
    {
        String month = new StringBuilder().append(date.charAt(0)).append(date.charAt(1)).toString();
        String day = new StringBuilder().append(date.charAt(2)).append(date.charAt(3)).toString();
        return month + "/" + day;
    }

    //----------------------------------------------------------------------------------------------------------------------------------------
    // Firebase integration to link data to bar graph in real-time
    //----------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_details_bar_graph);

        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = Utils.getDatabase();
        mFireBaseDatabase = mFirebaseInstance.getReference("users");

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();

        mFireBaseDatabase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("We are getting data from the database.");

                // Instantiates current week's budget
                currentWeeksDate = Utils.decrementDate(new Date()); //get the current week's index
                allWeeks.add(formatDateLabel(currentWeeksDate));    //get the week as a string for x-axis labels

                // Attain Firebase data for current week
                currentWeeksBudget = dataSnapshot.child(userId).child(currentWeeksDate).getValue(WeekLongBudget.class);
                allWeekBudgets.add(currentWeeksBudget);       //keep track of this week's data for use
                System.out.println("The current week is: ");
                System.out.println(allWeekBudgets.get(0).getStartDate());
                System.out.println("--------------------------------------------------------");

                // Instantiates the previous week's budget as recorded on Firebase
                prevDate = Utils.prevDate(new Date());
                prevWeeksDate = Utils.convertDate(prevDate);

                // Does the previous week exist?
                if (dataSnapshot.child(userId).child(prevWeeksDate).getValue(WeekLongBudget.class) != null) {
                    System.out.println("The week of " + prevWeeksDate + " actually exists!");
                    allWeeks.add(formatDateLabel(prevWeeksDate));
                }
                else {
                    System.out.println("The week of " + prevWeeksDate + " does not exist!");
                }

                // Instantiates the all other previous weeks' budgets up until the first recorded week
                int i = 1;
                while (dataSnapshot.child(userId).child(prevWeeksDate).getValue(WeekLongBudget.class) != null)
                {
                    allWeekBudgets.add(dataSnapshot.child(userId).child(prevWeeksDate).getValue(WeekLongBudget.class));
                    System.out.println("The previous week is: ");
                    System.out.println(allWeekBudgets.get(i).getStartDate());
                    System.out.println("--------------------------------------------------------");
                    prevDate = Utils.prevDate(prevDate);
                    prevWeeksDate = Utils.convertDate(prevDate);
                    i++;

                    //What about the next previous?
                    if (dataSnapshot.child(userId).child(prevWeeksDate).getValue(WeekLongBudget.class) != null) {
                        System.out.println("The week of " + prevWeeksDate + " actually exists!");
                        allWeeks.add(formatDateLabel(prevWeeksDate));
                    }
                    else {
                        System.out.println("The week of " + prevWeeksDate + " does not exist!");
                    }
                }

                // Start setting up the bar graph itself
                barGraph = findViewById(R.id.barGraph);

                Description description = new Description();
                description.setText("Expenses per week");
                barGraph.setDescription(description);
                description.setTextSize(16);
                description.setPosition(970,50);

                addDataSet(barGraph); //create actual bar graph with data attained from Firebase
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                System.out.println("You arent reDING CORRECTLTY");
            }
        });
        System.out.println("The current user ID is: " +userId);

        if(firebaseAuth.getCurrentUser() == null)
        {
            System.out.println("You are not signed in");
        } else {
            System.out.println("You are signed in on the main page: oncreate");
        }
    }

    //----------------------------------------------------------------------------------------------------------------------------------------
    // Attains all data stored in every week on Firebase and creates the whole bar graph
    //----------------------------------------------------------------------------------------------------------------------------------------
    private void addDataSet(BarChart barGraph)
    {
        List<BarEntry> barEntries = new ArrayList<>();

        float pos = 0f;    //determines bar positioning for each week (in correct order)
        for (int i = allWeekBudgets.size() - 1; i >= 0; i--) {
            ArrayList<Float> stackData = new ArrayList<>();   //convert to array when fully populated with stacked bars
            ArrayList<String> stackLabels = new ArrayList<>(); //holds each category's name

            // Checks if the currently checked week has null entries
            if(allWeekBudgets.get(i).costOfAllCategories == null) return;

            // Extract specific data from each category from each recorded week
            for (Map.Entry<String, Double> entry : allWeekBudgets.get(i).costOfAllCategories.entrySet()) {
                BigDecimal number = new BigDecimal(entry.getValue());
                float myFloat = number.floatValue();                   //get total category expenses
                if (myFloat != 0.00) {
                    stackData.add(myFloat);                            //prepare a new stacked bar for the week
                    stackLabels.add(entry.getKey());                   //get category name
                }
                // Convert Arraylist to float array for API requirements
                float[] barData = new float[stackData.size()];
                int j = 0;
                for (Float f : stackData) {
                    barData[j] = (f != null ? f : stackData.get(j));
                    j++;
                }
                barEntries.add(new BarEntry(pos, barData));  //prepare entire bar with all categories accounted for
            }
            pos++;      //iterate through positions of all week bars
        }

        // Creates the data set for the bar graph
        BarDataSet dataSet = new BarDataSet(barEntries, "BarDataSet");

        // Sets a different color for each category stack
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        // Disables the legend
        Legend legend = barGraph.getLegend();
//        legend.setEnabled(false);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);

        // Sets and formats the data set prepared for the bar graph
        BarData data = new BarData(dataSet);
        data.setValueFormatter(new StackedValueFormatter(false, "$", 2)); //reformat bar data
        data.setBarWidth(0.9f); //set custom bar width
        barGraph.setData(data);
        barGraph.setFitBars(true); //make the x-axis fit exactly all bars
        barGraph.invalidate(); //refresh

        // Reformats x-axis
        XAxis xAxis = barGraph.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        Collections.reverse(allWeeks);      //gets correct order
        String[] allWeeksArray = allWeeks.toArray(new String[allWeeks.size()]);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(allWeeksArray));
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        // Reformats y-axis
        YAxis yLeft = barGraph.getAxisLeft();
        YAxis yRight = barGraph.getAxisRight();
        yLeft.setValueFormatter(new MyYAxisValueFormatter());
        yRight.setValueFormatter(new MyYAxisValueFormatter());
    }
}
