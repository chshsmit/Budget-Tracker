package christophershae.budgettracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static christophershae.budgettracker.R.id.itemNameView;
import android.content.Intent;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Set;


import static christophershae.budgettracker.R.id.finishAddingItemsToBudget;


public class ManualInputActivity extends AppCompatActivity implements View.OnClickListener {



    public ArrayList<String> myCategories = new ArrayList<>();


    //----------------------------------------------------------------------------------------
    //This code has all the functions that need to be overridden
    //----------------------------------------------------------------------------------------


    public void addInitialCategories(){
        myCategories.add("Rent");
        myCategories.add("Food");
        myCategories.add("Gas");
        myCategories.add("Personal Items");
        myCategories.add("Household Items");
        myCategories.add("Groceries");
        myCategories.add("Entertainment");
        myCategories.add("Add a Category.....");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_input);

        //toolbar setup
        Toolbar topToolBar = (Toolbar)findViewById(R.id.toolbar);
        topToolBar.setNavigationIcon(getResources().getDrawable(R.drawable.backbut));
        setSupportActionBar(topToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        topToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Firebase setup
        instantiateFirebase();

        //Instantiating the adapter for the listview
        instantiateListView();

        instantiateGlobalButtonsAndViews();


    }

    //----------------------------------------------------------------------------------------
    //This code has all the stuff to pull info from firebase
    //----------------------------------------------------------------------------------------
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mFireBaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private String userId;
    Map<String, WeekLongBudget> usersBudgets = new HashMap<>();

    public void instantiateFirebase()
    {
        //Firebase stuff
        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = Utils.getDatabase();
        mFireBaseDatabase = mFirebaseInstance.getReference("users");

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();

        firebaseListener();
    }

    public void firebaseListener()
    {
        mFireBaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Clearing the current user budgets
                usersBudgets.clear();

                GenericTypeIndicator<List<String>> myList = new GenericTypeIndicator<List<String>>() {};

                //Get the values of our categories
                if(dataSnapshot.child(userId).child("categories").getValue(myList) == null){
                    System.out.println("Setting Value In Database");
                    addInitialCategories();
                    mFireBaseDatabase.child(userId).child("categories").setValue(myCategories);
                    setUpAdapterForSpinner();
                }else{
                    System.out.println("Retrieving info from firebase");
                    myCategories.clear();
                    System.out.println(myCategories.isEmpty());

                    myCategories.addAll((ArrayList<String>) dataSnapshot.child(userId).child("categories").getValue(myList));
                    Set<String> newSet = new HashSet<>();
                    newSet.addAll(myCategories);
                    myCategories.clear();
                    myCategories.addAll(newSet);

                    if(adapter == null){
                        setUpAdapterForSpinner();
                    }else{
                        adapter.notifyDataSetChanged();
                    }

                }


                //Looping through all children within the users node and adding each child
                //to the users budgets
                for(DataSnapshot snapshot: dataSnapshot.child(userId).getChildren()){
                    //System.out.println(snapshot.getKey());
                    try{
                        usersBudgets.put(snapshot.getKey(),snapshot.getValue(WeekLongBudget.class));
                    }catch(DatabaseException e){
                        System.out.println("This is where the photocount is");
                        continue;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("You arent reDING CORRECTLTY");
            }
        });
    }

    //----------------------------------------------------------------------------------------
    //This code instantiates all the buttons and text views
    //----------------------------------------------------------------------------------------
    //Buttons for the interface
    Button Finish;


    public void instantiateGlobalButtonsAndViews(){
        //Edit texts for the price and name entry
        priceEntry = (EditText) findViewById(R.id.itemPriceEntry);
        nameEntry = (EditText) findViewById(R.id.itemNameEntry);
        dateEntry = (EditText) findViewById(R.id.setDate);
        dateEntry.setText(slashedDate.format(new Date()));


        //create drop down menu to view the categories of expenses
        //Define spinner from xml file
        spinner = (Spinner) findViewById(R.id.Menu_C);

        Finish =(Button) findViewById(finishAddingItemsToBudget);
        Finish.setOnClickListener(this);
        //delete button
//        deleteCategory = (Button) findViewById(R.id.DeleteB);
//        deleteCategory.setOnClickListener(this);
    }

    //----------------------------------------------------------------------------------------
    //This code sets up the adapter
    //----------------------------------------------------------------------------------------
    //ArrayAdapter to fill in spinner
    ArrayAdapter<String> adapter;
    Spinner spinner;

    public void setUpAdapterForSpinner()
    {
        //define list

        adapter =  new ArrayAdapter<String>(ManualInputActivity.this,R.layout.dropdown_editlist,
                myCategories);

        //specify layout for now basic later on design it better
        adapter.setDropDownViewResource(R.layout.dropdown_editlist);
        //apply the adapter create list to the Spinner(drop down list)
        spinner.setAdapter(adapter);
        //LoadPreferences();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                //set selectem items
                //int spinnerPosition= spinner.getSelectedItemPosition();

                if(spinner.getSelectedItem().toString().equals("Add a Category....."))
                {
                    createCat();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        adapter.notifyDataSetChanged();
    }

    //Creates category for spinner
    public void createCat()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        final EditText incomeInput = new EditText(this);
        incomeInput.setHint("New Category");
        alertDialogBuilder.setView(incomeInput);

        alertDialogBuilder.setTitle("Create New Category");
        alertDialogBuilder.setPositiveButton("Create",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1)
                    {
                        String newCat = incomeInput.getText().toString();
                        myCategories.add(newCat);
                        adapter.notifyDataSetChanged();

                        mFireBaseDatabase.child(userId).child("categories").setValue(myCategories);
                        System.out.println("created New");
                        //addCategory(newCat);
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        //lets the user know their category was added
        Utils.toastMessage("Category Added", this);
    }

    //----------------------------------------------------------------------------------------
    //This is our onclick listener
    //----------------------------------------------------------------------------------------

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case finishAddingItemsToBudget:
                //testUser.getMap().get("10292017").getAmountForEachCategory();
                //System.out.println(testUser.getMap().get("10292017").getTotalAmountOfMoneySpent());
     //           load();
                finish();
                break;
        }

    }


    //----------------------------------------------------------------------------------------
    //This code handles the generation of the list view when items are added
    //----------------------------------------------------------------------------------------
    //Instantiating the list and its adapter
    ArrayList<ListElement> currentItemsAddedToList;
    private MyAdapter aa;


    public void instantiateListView()
    {
        currentItemsAddedToList = new ArrayList<ListElement>();
        aa = new MyAdapter(this, R.layout.manually_input_list_element, currentItemsAddedToList);
        ListView myListView = (ListView) findViewById(R.id.itemsAddedToBudgetAlready);
        myListView.setAdapter(aa);
        aa.notifyDataSetChanged();
    }

    //Creating a class for a single list element
    private class ListElement
    {
        ListElement() {};

        //Constructor for the list element
        ListElement(String nl, String pl, String cat) {
            nameLabel = nl;
            priceLabel = pl;
            category = cat;

        }

        //Variables that the list element needs to generate itself
        public String nameLabel;
        public String priceLabel;
        public String category;
    }


    //Private adapter class to adapt the listview to the arraylist
    private class MyAdapter extends ArrayAdapter<ListElement>
    {

        int resource;
        Context context;

        //Constructor
        public MyAdapter(Context _context, int _resource, List<ListElement> items)
        {
            super(_context, _resource, items);
            resource = _resource;
            context = _context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LinearLayout newView;

            ListElement currentItem = getItem(position);

            // Inflate a new view if necessary.
            if (convertView == null) {
                newView = new LinearLayout(getContext());
                LayoutInflater vi = (LayoutInflater)
                        getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                vi.inflate(resource,  newView, true);
            } else {
                newView = (LinearLayout) convertView;
            }

            // Fills in the view of a manually inputted item.
            TextView itemName = (TextView) newView.findViewById(itemNameView);
            TextView itemCategory = (TextView) newView.findViewById(R.id.itemCategory);
            TextView itemPrice = (TextView) newView.findViewById(R.id.itemPriceView);

            //This sets the text views in the list element
            itemName.setText(currentItem.nameLabel);
            itemPrice.setText(currentItem.priceLabel);
            itemCategory.setText(currentItem.category);



            // Set a listener for the whole list item.
            newView.setTag(currentItem.nameLabel);
            newView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String s = v.getTag().toString();
                    Utils.toastMessage(s, context);
                }
            });

            return newView;
        }
    }


    //----------------------------------------------------------------------------------------
    //This code creates a new item object and adds it to a user's current weeklong budget object
    //----------------------------------------------------------------------------------------

    //Global variables for the item price, name, date, and category
    //Global variables for the item price, name, date, adnd category
    public String newItemName;
    public String newItemPrice;
    public String newItemDate;
    public String newItemCategory;
    SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyy");    //This is the format we want our date string to be in
    SimpleDateFormat slashedDate = new SimpleDateFormat("MM/dd/yyyy");

    //Instantiating the edit text views
    EditText nameEntry;
    EditText priceEntry;
    EditText dateEntry;

    //This function executes when the user presses the add button
    public void createNewItem(View v)
    {

        //Getting the user input from the edit texts
        checkForAllFields();
        getInfoForNewItem();

        //Creating a new Item object and setting the price and name
        Item newItem = new Item(newItemName);

        //Attempt to set item price or show message
        try{
            newItem.setPrice(Double.valueOf(newItemPrice));
        } catch(NumberFormatException e){
            Utils.toastMessage("Input Valid Price", this);
            return;
        }

        System.out.println("This item costs: "+newItem.getPrice());

        //Getting the category from the spinner
        newItemCategory = spinner.getSelectedItem().toString();

        if(newItemCategory.equals("Add a Category.....")){
            Utils.toastMessage("Please Choose a Valid Category", ManualInputActivity.this);
            return;
        }

        newItem.setCategory(newItemCategory);
        //Setting the date the object was purchased to the current date
        //newItemDate = sdf.format(new Date());
        newItem.setDate(newItemDate);

        //Add the item to the correct weeks budget
        addItemToWeek(newItem);
        addItemsToListView();
    }

    public void addItemsToListView()
    {
        //This adds the item to the list view
        currentItemsAddedToList.add(new ListElement(newItemName, newItemPrice, newItemCategory));
        aa.notifyDataSetChanged();

        nameEntry.getText().clear();
        priceEntry.getText().clear();

        System.out.println("All my categories");
        for(String key: myCategories){
            System.out.println(key);
        }

        mFireBaseDatabase.child(userId).child("categories").setValue(myCategories);
    }

    public void getInfoForNewItem()
    {
        newItemPrice = priceEntry.getText().toString();
        newItemName = nameEntry.getText().toString();
        newItemDate = dateEntry.getText().toString();
        newItemDate = newItemDate.replace("/","");
    }

    public void checkForAllFields()
    {
        if(priceEntry.getText().toString().equals("") || nameEntry.getText().toString().equals(""))
        {
            Utils.toastMessage("Must Input Price and Name", this);
            return;
        }
    }


    //Retrieving the correct weeklong budget object to store the new item in
    public WeekLongBudget getWeek(String date)
    {
        //Decrement the date to be the most recent sunday
        try
        {
            date = Utils.decrementDate(sdf.parse(date));
        } catch (ParseException e)
        {
            e.printStackTrace();
        }


        System.out.println("The list is indexed by "+date);

        //If the budget week for the current item is null, then we create a new WeekLongbudget
        if(usersBudgets.get(date) == null){
            System.out.println("Creating new week");
            WeekLongBudget newWeek = new WeekLongBudget(date);
            return newWeek;
        } else{
            return usersBudgets.get(date);        //Return the WeekLongBudget for the date if it isn't null
        }
    }

    //inputs item into right arraylist using the items week. Feature works if for example user
    // wants to add item to past or future
    public void addItemToWeek(Item item)
    {
        String date = item.getDate();                   //Get the date of the item
        System.out.println("the date is:" +date);
        WeekLongBudget inputWeek = getWeek(date);       //Get the current weeks budget or the budget for the corresponding date
        inputWeek.addItem(item);

        try
        {
            //Adding the WeekLongBudget to all of the users budgets
            usersBudgets.put(Utils.decrementDate(sdf.parse(date)), inputWeek);
        }
        catch (ParseException e)    //This exception catches the parsing of the date
        {
            e.printStackTrace();
        }

        mFireBaseDatabase.child(userId).setValue(usersBudgets);


        //checks that you are over budget!
        WeekLongBudget currentWeeksBudget = getWeek(date);
        if(currentWeeksBudget.getTotalAmountSpent() > currentWeeksBudget.getGoalTotal())
        {
            Utils.toastMessage("You are over Goal Budget!", this);

        }
    }



    //----------------------------------------------------------------------------------------
    //Code for the toolbar
    //----------------------------------------------------------------------------------------


    //ToolBar function to setup res/menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.Settings) {
            Intent setting = new Intent(ManualInputActivity.this, SettingsActivity.class);
            startActivity(setting);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
