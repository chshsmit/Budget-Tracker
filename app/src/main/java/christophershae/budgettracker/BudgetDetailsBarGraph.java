package christophershae.budgettracker;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.github.mikephil.charting.utils.ViewPortHandler;
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

import static com.github.mikephil.charting.utils.ColorTemplate.rgb;
import static java.lang.Boolean.FALSE;
import static java.lang.Float.NaN;

public class BudgetDetailsBarGraph extends AppCompatActivity
{
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mFireBaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private String userId;

    // These are variables for the current weeks date, all previous week dates, and the budget for the current week
    private String currentWeeksDate;
    private String prevWeeksDate;
    private Date prevDate;
    private WeekLongBudget currentWeeksBudget;
    private ArrayList<WeekLongBudget> allWeekBudgets = new ArrayList<WeekLongBudget>();
    private ArrayList<String> allWeeks = new ArrayList<String>();   //for x-axis values
    BarChart barGraph;

    String[] mWeeks = new String[]{
            "10/29", "11/05"//, "11/12", "11/19", "11/26", "12/3", "12/10"
    };

    // Compiles all the colors available from MPAndroidChart's ColorTemplate
    private static final int[] STACK_COLORS = {
            rgb("#2ecc71"), rgb("#f1c40f"), rgb("#e74c3c"), rgb("#3498db"),
            Color.rgb(207, 248, 246), Color.rgb(148, 212, 212),
            Color.rgb(136, 180, 187), Color.rgb(118, 174, 175),
            Color.rgb(42, 109, 130), Color.rgb(217, 80, 138),
            Color.rgb(254, 149, 7), Color.rgb(254, 247, 120),
            Color.rgb(106, 167, 134), Color.rgb(53, 194, 209),
            Color.rgb(193, 37, 82), Color.rgb(255, 102, 0),
            Color.rgb(245, 199, 0), Color.rgb(106, 150, 31),
            Color.rgb(179, 100, 53), Color.rgb(64, 89, 128),
            Color.rgb(149, 165, 124), Color.rgb(217, 184, 162),
            Color.rgb(191, 134, 134), Color.rgb(179, 48, 80),
            Color.rgb(192, 255, 140), Color.rgb(255, 247, 140),
            Color.rgb(255, 208, 140), Color.rgb(140, 234, 255),
            Color.rgb(255, 140, 157)
    };

    // Set as many colors as stack-values per week entry (not properly working yet)
    // (borrows colors from MPAndroidChart's ColorTemplate)
    private int[] getColors(int stackSize)
    {

        int[] colors = new int[stackSize];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = STACK_COLORS[i];
            System.out.println("We are now at color: " + i);
        }
        return colors;
    }

    // Reinitialize data arrays when new data is appended, since arrays are immutable
    public float[] addStackedData(float[] barData, float newData)
    {
        float[] newBarData = new float[barData.length + 1];

        for (int i = 0; i < barData.length; i++) {
            newBarData[i] = barData[i];
        }
        newBarData[barData.length] = newData;
        return newBarData;
    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter {

        private String[] mValues;
        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            System.out.println(value);
            // "value" represents the position of the label on the axis (x or y)
            return mValues[(int) value];
        }
        /** this is only needed if numbers are returned, else return 0 */
//    @Override
//    public int getDecimalDigits() { return 0; }
    }

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

                    // find out if we are on top of the stack
                    if (vals[vals.length - 1] == value)
                    {
                        // return the "sum" across all stack values
                        return  mAppendix + mFormat.format(value) + " (TOTAL: " +
                                mAppendix + mFormat.format(barEntry.getY()) + ")";
                    } else {
                        return ""; // return empty
                    }
                }
            }
            // return the "proposed" value
            return mAppendix + mFormat.format(value);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_details_bar_graph);

        // Firebase stuff
        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = Utils.getDatabase();
        mFireBaseDatabase = mFirebaseInstance.getReference("users");

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();

        mFireBaseDatabase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("We are getting data from the database");

                // Instantiates current week's budget
                currentWeeksDate = Utils.decrementDate(new Date()); //get the current week's index
                allWeeks.add(currentWeeksDate);
                currentWeeksBudget = dataSnapshot.child(userId).child(currentWeeksDate).getValue(WeekLongBudget.class);
                allWeekBudgets.add(currentWeeksBudget);
                System.out.println("The current week is: ");
                System.out.println(allWeekBudgets.get(0).getStartDate());
                System.out.println("--------------------------------------------------------");

                // Instantiates all previous weeks' budgets as recorded on Firebase
                prevDate = Utils.prevDate(new Date());
                prevWeeksDate = Utils.convertDate(prevDate);

                // Does the previous week exist?
                if (dataSnapshot.child(userId).child(prevWeeksDate).getValue(WeekLongBudget.class) != null) {
                    System.out.println("The week of " + prevWeeksDate + " actually exists!");
                    allWeeks.add(prevWeeksDate);
                }
                else {
                    System.out.println("The week of " + prevWeeksDate + " does not exist!");
                }

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
                        allWeeks.add(prevWeeksDate);
                    }
                    else {
                        System.out.println("The week of " + prevWeeksDate + " does not exist!");
                    }
                }

                for(int x = 0; x < allWeeks.size(); x++) {
                    System.out.println(allWeeks.get(x));
                }

                barGraph = (BarChart) findViewById(R.id.barGraph);

                Description description = new Description();
                description.setText("Expenses per week");
                barGraph.setDescription(description);
                description.setTextSize(16);
                description.setPosition(970,50);

                addDataSet(barGraph); //create actual bar graph
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

    // If there exists (category) data from a week, add it to bar graph
    private void addDataSet(BarChart barGraph)
    {
        List<BarEntry> barEntries = new ArrayList<>();
        List<LegendEntry> legendLabels = new ArrayList<>();

        int l = 0;    //for stack color
//        double weeklyTotal = currentWeeksBudget.getTotalAmountSpent();
        int categoryCount = 0;

        float pos = 0f;    //determines bar positioning for each week (in correct order)
        for (int i = allWeekBudgets.size() - 1; i >= 0; i--) {
            float[] barData = new float[]{}; //create new bar for each week checked
//            ArrayList<Float> stackData = new ArrayList<Float>();   // convert to array when fully populated with stacked bars

            // Really important that you check if the currently checked week has null entries later!
            for (Map.Entry<String, Double> entry : allWeekBudgets.get(i).costOfAllCategories.entrySet()) {
                categoryCount++;
                BigDecimal number = new BigDecimal(entry.getValue());
                float myFloat = number.floatValue();
                if (myFloat != 0.00) {
                    barData = addStackedData(barData, myFloat); //add new data
//                    barEntries.add(new BarEntry(pos, barData));
//                    stackData.add(myFloat);
//                    legendLabels.add(new LegendEntry(entry.getKey(), Legend.LegendForm.DEFAULT, NaN, NaN,
//                            null, ColorTemplate.COLORFUL_COLORS[l]));
                    l++;
                }
                barEntries.add(new BarEntry(pos, barData));
            }
            pos++;
        }
        // Creates example bars
//        barEntries.add(new BarEntry(0F, new float[]{10f, 156f}));
//        barEntries.add(new BarEntry(1F, new float[]{10f}));
//        barEntries.add(new BarEntry(0F, 10f));
//        barEntries.add(new BarEntry(1F, new float[]{57f, 145f, 230f}));

        // Creates the data set for the bar graph
        BarDataSet dataSet = new BarDataSet(barEntries, "BarDataSet");

        // Sets a different color for each category stack
//        dataSet.setColors(getColors(categoryCount));
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        // Creates the legend
        Legend legend = barGraph.getLegend();
        legend.setCustom(legendLabels);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);

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
//        xAxis.setValueFormatter(new IndexAxisValueFormatter(allWeeksArray));
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        // Defines budget cap limit line (for perhaps each week?)
        YAxis leftAxis = barGraph.getAxisLeft();
        LimitLine ll = new LimitLine(230f, "WEEKLY BUDGET CAP EXCEEDED!");
        ll.setLineColor(Color.RED);
        ll.setLineWidth(4f);
        ll.setTextColor(Color.BLACK);
        ll.setTextSize(12f);
        leftAxis.addLimitLine(ll);
    }
//    @Override
//    public void onBackPressed()
//    {
//        Intent intent  = new Intent(this, MainBudgetScreen.class);
////        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//        System.out.println("**********You have exited the bar graph activity.");
//        finish();
//    }


//    @Override
//    protected void onStop()
//    {
//        super.onStop();
////        System.out.println("You have exited the bar graph activity.");
//        finish();
//    }
}
