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

import static christophershae.budgettracker.R.id.Enter_Man;
import static christophershae.budgettracker.R.id.Picture_Screen;
import static christophershae.budgettracker.R.id.Settings;

public class MainBudgetScreen extends AppCompatActivity implements View.OnClickListener{

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

    @Override
    public void onResume() {
        super.onResume();
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
