//overrides PieChart Display

package christophershae.budgettracker;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

public class MonetaryDisplay implements IValueFormatter {

    private DecimalFormat mFormat;

    public MonetaryDisplay() {
        mFormat = new DecimalFormat("###,###,##0.00"); // use two decimal
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

        return "$" + mFormat.format(value); // e.g. append a dollar-sign
    }

}
