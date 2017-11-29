package christophershae.budgettracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;


public class RecentPurchases extends AppCompatActivity {

    private static final String TAG = "Popup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.915), (int)(height*.725));

        Intent intent = getIntent();
        Bundle recentItemBundle = intent.getBundleExtra("BUNDLE");
        ArrayList<Item> recentItemList = (ArrayList<Item>) recentItemBundle.getSerializable("ARRAYLIST");

        Log.d(TAG, "onCreate: Started.");
        ListView mListView = (ListView) findViewById(R.id.listView);

        ItemListAdapter adapter = new ItemListAdapter(this, R.layout.adapter_view_layout, recentItemList);
        mListView.setAdapter(adapter);

    }
}
