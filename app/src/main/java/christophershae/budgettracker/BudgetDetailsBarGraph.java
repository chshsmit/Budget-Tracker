package christophershae.budgettracker;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Random;

//test comment for git merge
public class BudgetDetailsBarGraph extends AppCompatActivity {
    BarChart barChart;
    ArrayList<String> dates;
    ArrayList<BarEntry> barEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        barChart = (BarChart) findViewById(R.id.bargraph);

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(111f, 0));
        barEntries.add(new BarEntry(250f, 1));
        barEntries.add(new BarEntry(403f, 2));
        barEntries.add(new BarEntry(478f, 3));
        barEntries.add(new BarEntry(790f, 4));
        barEntries.add(new BarEntry(790f, 5));
        barEntries.add(new BarEntry(826f, 6));
        BarDataSet barDataSet = new BarDataSet(barEntries, "Expenses in $$$");

        ArrayList<String> theDates = new ArrayList<>();
        theDates.add("10/15");
        theDates.add("10/16");
        theDates.add("10/17");
        theDates.add("10/18");
        theDates.add("10/19");
        theDates.add("10/20");
        theDates.add("10/21");

        BarData theData = new BarData(theDates, barDataSet);
        barChart.setData(theData);

        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.setDescription("Final Total");

        YAxis leftAxis = barChart.getAxisLeft();

        LimitLine ll = new LimitLine(350f, "WEEKLY BUDGET CAP EXCEEDED!");
        ll.setLineColor(Color.RED);
        ll.setLineWidth(4f);
        ll.setTextColor(Color.BLACK);
        ll.setTextSize(12f);
        // .. and more styling options

        leftAxis.addLimitLine(ll);
    }
}
