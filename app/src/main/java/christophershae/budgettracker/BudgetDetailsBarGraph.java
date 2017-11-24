package christophershae.budgettracker;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BudgetDetailsBarGraph extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mFireBaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private String userId;

    SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyy");

    //These are variables for the current weeks date, and the budget for the current week
    private String currentWeeksDate;
    private WeekLongBudget currentWeeksBudget;

    public class MyXAxisValueFormatter implements IAxisValueFormatter {

        private String[] mValues;
        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            return mValues[(int) value];
        }
        /** this is only needed if numbers are returned, else return 0 */
//    @Override
//    public int getDecimalDigits() { return 0; }
    }

    String[] mWeeks = new String[]{
            "10/29", "11/05", "11/12", "11/19", "11/26", "12/3", "12/10"
    };

    /* Reinitialize data arrays when new data is appended, since arrays are immutable */
    public float[] addStackedData(float[] barData, float newData) {
        float[] newBarData = new float[barData.length+1];

        for (int i = 0; i < barData.length; i++) {
            newBarData[i] = barData[i];
        }
        newBarData[barData.length] = newData;
        return newBarData;
    }

//    class MyBarDataSet extends BarDataSet {
//        public MyBarDataSet(List<BarEntry> yVals, String label) {
//            super(yVals, label);
//        }
//        @Override
//        public int getColor(int index) {
//            if(getEntryForIndex(index).getY() < 140)
//                return mColors.get(0);
//            else if(getEntryForIndex(index).getY() > 145)
//                return mColors.get(1);
//            else
//                return mColors.get(2);
//        }
//    }

    private int[] getColors() {
        int stacksize = 3;
        // have as many colors as stack-values per entry
        int[] colors = new int[stacksize];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = ColorTemplate.MATERIAL_COLORS[i];
        }
        return colors;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_details_bar_graph);
        BarChart barGraph = (BarChart) findViewById(R.id.bargraph);
        final TextView totalIncomeTextView = (TextView) findViewById(R.id.Total_Spent);

        //Firebase stuff
        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = Utils.getDatabase();
        mFireBaseDatabase = mFirebaseInstance.getReference("users");

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();
        System.out.println("The current user ID is: " +userId);

        //Getting the current weeks index
        currentWeeksDate = Utils.decrementDate(new Date());

        if(firebaseAuth.getCurrentUser() == null){
            System.out.println("You are not signed in");
        } else {
            System.out.println("You are signed in on the main page: oncreate");
        }

        /* All entries must be represented as stacked bars (including single ones), or app will crash */
        List<BarEntry> entries = new ArrayList<>();

        //arrays are immutable; create helper function to reinitialize arrays when appending new values
        float[] bar1Data = new float[]{10f, 20f, 35f};
        bar1Data = addStackedData(bar1Data, 50f); //add new data

        entries.add(new BarEntry(0f, bar1Data));
        entries.add(new BarEntry(1f, new float[] {10f, 25f, 80f}));
        entries.add(new BarEntry(2f, new float[] {5f, 17f, 44f}));
        entries.add(new BarEntry(3f, new float[] {25f, 81f, 68f}));
        // gap of 2f
        entries.add(new BarEntry(5f, new float[] {83f, 49f, 9f}));
        entries.add(new BarEntry(6f, new float[] {56f, 32f, 65f}));

        BarDataSet dataSet = new BarDataSet(entries, "BarDataSet");
        dataSet.setColors(getColors());

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f); // set custom bar width
        barGraph.setData(data);
        barGraph.setFitBars(true); // make the x-axis fit exactly all bars
        barGraph.invalidate(); // refresh

        /* Defines budget cap limit line */
        YAxis leftAxis = barGraph.getAxisLeft();
        LimitLine ll = new LimitLine(150f, "WEEKLY BUDGET CAP EXCEEDED!");
        ll.setLineColor(Color.RED);
        ll.setLineWidth(4f);
        ll.setTextColor(Color.BLACK);
        ll.setTextSize(12f);
        // .. and more styling options

        leftAxis.addLimitLine(ll);

        /* Repositions x-axis values and removes grid lines */
        XAxis xAxis = barGraph.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(mWeeks));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
    }
}