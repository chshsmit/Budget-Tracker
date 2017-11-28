package christophershae.budgettracker;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
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
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
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

import static java.lang.Boolean.FALSE;
import static java.lang.Float.NaN;

public class BudgetDetailsBarGraph extends AppCompatActivity
{
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mFireBaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private String userId;

    SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyy");

    // These are variables for the current weeks date, all previous week dates, and the budget for the current week
    private String currentWeeksDate;
    private String prevWeeksDate;
    private Date prevDate;
    private WeekLongBudget currentWeeksBudget;
    private ArrayList<WeekLongBudget> allWeekBudgets = new ArrayList<WeekLongBudget>();
    BarChart barGraph;

    // Set as many colors as stack-values per week entry (not properly working yet)
    private int[] getColors(int stackSize)
    {
        int[] colors = new int[stackSize];
        for (int i = 0; i < colors.length; i++) {
//            colors[i] = ColorTemplate.MATERIAL_COLORS[i];
            colors[i] = ColorTemplate.COLORFUL_COLORS[i];
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
                currentWeeksBudget = dataSnapshot.child(userId).child(currentWeeksDate).getValue(WeekLongBudget.class);
                allWeekBudgets.add(currentWeeksBudget);
                System.out.println("The current week is: ");
                System.out.println(allWeekBudgets.get(0).getStartDate());
                System.out.println("--------------------------------------------------------");

                // Instantiates all previous weeks' budgets as recorded on Firebase
                prevDate = Utils.prevDate(new Date());
                prevWeeksDate = Utils.convertDate(prevDate);

                // Does the previous week exist?
//                if (dataSnapshot.child(userId).child(prevWeeksDate).getValue(WeekLongBudget.class) != null) {
//                    System.out.println("The week of " + prevWeeksDate + " actually exists!");
//                }
//                else {
//                    System.out.println("The week of " + prevWeeksDate + " does not exist!");
//                }

                int i = 1;
                while (dataSnapshot.child(userId).child(prevWeeksDate).getValue(WeekLongBudget.class) != null)
                {
                    allWeekBudgets.add(dataSnapshot.child(userId).child(prevWeeksDate).getValue(WeekLongBudget.class));
//                    System.out.println("The previous week is: ");
//                    System.out.println(allWeekBudgets.get(i).getStartDate());
//                    System.out.println("--------------------------------------------------------");
                    prevDate = Utils.prevDate(prevDate);
                    prevWeeksDate = Utils.convertDate(prevDate);
                    i++;

                     //What about the next previous?
//                    if (dataSnapshot.child(userId).child(prevWeeksDate).getValue(WeekLongBudget.class) != null) {
//                        System.out.println("The week of " + prevWeeksDate + " actually exists!");
//                    }
//                    else {
//                        System.out.println("The week of " + prevWeeksDate + " does not exist!");
//                    }
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

            // Really important that you check if the currently checked week has null entries later!
            for (Map.Entry<String, Double> entry : allWeekBudgets.get(i).costOfAllCategories.entrySet()) {
                categoryCount++;
                BigDecimal number = new BigDecimal(entry.getValue());
                float myFloat = number.floatValue();
                if (myFloat != 0.00) {
                    barData = addStackedData(barData, myFloat); //add new data
                    barEntries.add(new BarEntry(pos, barData)); //does i have to be a float, or does int also work?
//                    legendLabels.add(new LegendEntry(entry.getKey(), Legend.LegendForm.DEFAULT, NaN, NaN,
//                            null, ColorTemplate.COLORFUL_COLORS[l]));
                    l++;
                }
            }
            pos++;
        }
        // Creates example bars
//        barEntries.add(new BarEntry(1F, new float[]{10f, 156f}));
//        barEntries.add(new BarEntry(2F, new float[]{57f, 145f, 230f}));

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
//        xAxis.setDrawGridLines(false);
//        xAxis.setValueFormatter(new MyXAxisValueFormatter(mWeeks));

        // Defines budget cap limit line (for perhaps each week?)
        YAxis leftAxis = barGraph.getAxisLeft();
        LimitLine ll = new LimitLine(230f, "WEEKLY BUDGET CAP EXCEEDED!");
        ll.setLineColor(Color.RED);
        ll.setLineWidth(4f);
        ll.setTextColor(Color.BLACK);
        ll.setTextSize(12f);
        leftAxis.addLimitLine(ll);
    }
}
