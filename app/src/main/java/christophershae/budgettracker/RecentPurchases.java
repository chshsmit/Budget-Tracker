package christophershae.budgettracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


//----------------------------------------------------------------------------------------------------------------------------------------
// Creates a listview display of all recently added items without exiting MainBudgetScreen
//----------------------------------------------------------------------------------------------------------------------------------------
public class RecentPurchases extends AppCompatActivity {
    private static final String TAG = "*** Popup Debugger ***";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_window);

        // Formats popup window display
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.915), (int)(height*.725));

        Intent intent = getIntent();
        Bundle recentItemBundle = intent.getBundleExtra("BUNDLE");
        ArrayList<Item> recentItemList = (ArrayList<Item>) recentItemBundle.getSerializable("ARRAYLIST");

        Log.d(TAG, "onCreate: Started.");
        ListView mListView = findViewById(R.id.listView);

        // Sets extracted Firebase data for all items added into the current week
        ItemListAdapter adapter = new ItemListAdapter(this, R.layout.adapter_view_layout, recentItemList);
        mListView.setAdapter(adapter);
    }
}
