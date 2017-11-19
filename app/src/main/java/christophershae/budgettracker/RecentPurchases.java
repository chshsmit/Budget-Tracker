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


public class RecentPurchases extends AppCompatActivity {

// Testing User and Date classes
//-------------------------------------------------------------------------
    //Global variables
    User testUser = new User("CrashTestDummy0.1");


    //Global variables for the item price, name, and date
    public String newItemName;
    public String newItemPrice;
    public String newItemDate;
    public String newItemCategory;
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy, EEEE");
//-------------------------------------------------------------------------


    private static final String TAG = "*** Popup Debugger ***";

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


        // -------------------------------------------------------------------
        Item newItem = new Item("CrashDummyItem");
        newItemDate = sdf.format(new Date());
        newItem.setPrice(Double.valueOf("344.99"));
        testUser.addItem(newItem);

        newItem.setDate(newItemDate);
        // -------------------------------------------------------------------

        //Create the Item objects
        Item curry = new Item("Instant curry");
        curry.setDate("12/07/35, Friday");
        curry.setPrice(9.99);

        Item pasta = new Item("Pasta");
        pasta.setDate("12/07/35, Friday");
        pasta.setPrice(6.99);

        Item television = new Item("Plasma TV");
        television.setDate("12/05/35, Wednesday");
        television.setPrice(3199.99);

        Item hummus = new Item("Hummus");
        hummus.setDate("12/05/35, Wednesday");
        hummus.setPrice(6.99);

        Item naan = new Item("Naan bread");
        naan.setDate("12/03/35, Monday");
        naan.setPrice(9.99);

        Item ramen = new Item("Top ramen");
        ramen.setDate("12/03/35, Monday");
        ramen.setPrice(0.99);

        Item chair = new Item("Wheelchair");
        chair.setDate("12/02/35, Sunday");
        chair.setPrice(74.99);

        Item lamp6 = new Item("Lava lamp");
        lamp6.setDate("12/02/35, Sunday");
        lamp6.setPrice(39.99);
        Item lamp5 = new Item("Lava lamp");
        lamp5.setDate("12/02/35, Sunday");
        lamp5.setPrice(39.99);
        Item lamp4 = new Item("Lava lamp");
        lamp4.setDate("12/02/35, Sunday");
        lamp4.setPrice(39.99);
        Item lamp3 = new Item("Lava lamp");
        lamp3.setDate("12/02/35, Sunday");
        lamp3.setPrice(39.99);
        Item lamp2 = new Item("Lava lamp");
        lamp2.setDate("12/02/35, Sunday");
        lamp2.setPrice(39.99);
        Item lamp = new Item("Lava lamp");
        lamp.setDate("12/02/35, Sunday");
        lamp.setPrice(39.99);


        //Add the Transaction objects to an ArrayList
        // recentItemList = new ArrayList<>();
        recentItemList.add(newItem);

        recentItemList.add(curry);
        recentItemList.add(pasta);
        recentItemList.add(television);
        recentItemList.add(hummus);
        recentItemList.add(naan);
        recentItemList.add(ramen);
        recentItemList.add(chair);
        recentItemList.add(lamp6);
        recentItemList.add(lamp5);
        recentItemList.add(lamp4);
        recentItemList.add(lamp3);
        recentItemList.add(lamp2);
        recentItemList.add(lamp);



        ItemListAdapter adapter = new ItemListAdapter(this, R.layout.adapter_view_layout, recentItemList);
        mListView.setAdapter(adapter);

    }
}
