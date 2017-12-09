package christophershae.budgettracker;
//imports of all neccessary utilities and libraries
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
import java.util.List;
<<<<<<< HEAD
import static christophershae.budgettracker.R.id.DeleteB;
=======

>>>>>>> origin/deleting_categories
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
<<<<<<< HEAD
import java.util.Arrays;
=======

>>>>>>> origin/deleting_categories
import java.util.Map;

import static christophershae.budgettracker.R.id.finishAddingItemsToBudget;
public class ManualInputActivity extends AppCompatActivity implements View.OnClickListener {
<<<<<<< HEAD
    //set up fire base private variables to get user information
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mFireBaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private String userId;

    //Global variables for the item price, name, date, and category
    //Global variables for the item price, name, date, adnd category
    public String newItemName;
    public String newItemPrice;
    public String newItemDate;
    public String newItemCategory;
    //public String newDate;
    SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyy");    //This is the format we want our date string to be in
    SimpleDateFormat slashedDate = new SimpleDateFormat("MM/dd/yyyy");
    //Instantiating the edit text views
    EditText nameEntry;
    EditText priceEntry;
    EditText dateEntry;
    //users budget
    Map<String, WeekLongBudget> usersBudgets = new HashMap<>();
    //Buttons for the interface
    Button Finish;
    Button deleteCategory;

    //ArrayAdapter to fill in spinner
    ArrayAdapter<CharSequence> adapter;
    List<CharSequence> EditMyList;
    Spinner spinner;
    //make an array to store our categories
    public String [] Categories_list = {"Food" ,"Rent", "Gas", "Personal Items", "Household Items",

            "Groceries", "Entertainment"};
=======


>>>>>>> origin/deleting_categories

    public ArrayList<String> myCategories = new ArrayList<>();


    //----------------------------------------------------------------------------------------
    //This code has all the functions that need to be overridden
    //----------------------------------------------------------------------------------------

<<<<<<< HEAD
=======

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


>>>>>>> origin/deleting_categories
    @Override
    //set up firebase and set up input fields
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

<<<<<<< HEAD
        //Instantiating the adapter for the listview
        currentItemsAddedToList = new ArrayList<ListElement>();
        aa = new MyAdapter(this, R.layout.manually_input_list_element, currentItemsAddedToList);
        ListView myListView = (ListView) findViewById(R.id.itemsAddedToBudgetAlready);
        myListView.setAdapter(aa);
        //refresh the data in the listview
        aa.notifyDataSetChanged();

=======
    //----------------------------------------------------------------------------------------
    //This code instantiates all the buttons and text views
    //----------------------------------------------------------------------------------------
    //Buttons for the interface
    Button Finish;


    public void instantiateGlobalButtonsAndViews(){
>>>>>>> origin/deleting_categories
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
<<<<<<< HEAD
        EditMyList = new ArrayList<CharSequence>(Arrays.<CharSequence>asList(Categories_list));

        adapter =  new ArrayAdapter<CharSequence>(ManualInputActivity.this,R.layout.dropdown_editlist,
                EditMyList);
=======

        adapter =  new ArrayAdapter<String>(ManualInputActivity.this,R.layout.dropdown_editlist,
                myCategories);
>>>>>>> origin/deleting_categories

        //specify layout for now basic later on design it better
        adapter.setDropDownViewResource(R.layout.dropdown_editlist);
        //apply the adapter create list to the Spinner(drop down list)
        spinner.setAdapter(adapter);
<<<<<<< HEAD
        //this method load our data from the spinner when onCreate happens
        LoadPreferences();
=======
        //LoadPreferences();
>>>>>>> origin/deleting_categories
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            //set up the click listener for the spinner
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {//set selectem items
                if(spinner.getSelectedItem().toString().equals("Add a Category....."))
                {
                    createCat();
                }
            }
            //do nothing here
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                //this function does nothing
            }
        });
<<<<<<< HEAD
    }
    //save listview data
    protected void SavePreferences(String key, String value, boolean x) {

        SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(this);

        String s=data.getString(key,""); //to fetch previous stored values

        s=s+"!"+value;   //to add new value to previous one
        if(!x) {
            data.edit().putString(key, s).commit();
        }
        if(x){
            data.edit().remove(key).commit();
        }
    }
    //load listview data
    protected void LoadPreferences(){
        SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(this);
        String dataSet = data.getString("List","Add a Category....." );
=======
        adapter.notifyDataSetChanged();
    }

    //Creates category for spinner
    public void createCat()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        final EditText incomeInput = new EditText(this);
        incomeInput.setHint("New Category");
        alertDialogBuilder.setView(incomeInput);
>>>>>>> origin/deleting_categories

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
<<<<<<< HEAD
        } else{
            adapter.add(dataSet);
            adapter.notifyDataSetChanged();
        }
    }
    //method to add a category
    public void addCategory(String cat){
        //check if user picks the Add Category choice is so just return
        if(cat.equals("Add a Category....."))
        {
            return;
        }

        //check if input is empty or contains strings
        if(!cat.isEmpty() && cat.length() > 0)
        {
           adapter.add(cat);
           //refresh data
           adapter.notifyDataSetChanged();
           SavePreferences("List", cat, false);
        }
        else
        {
            Utils.toastMessage("No Category To Add", this);
        }
    }
    //method to delete
    public void delete(int pos, String deleteVal )
    {
        if(pos > -1)
        {
            adapter.remove(EditMyList.get(pos));
            Utils.toastMessage("Category Deleted", this);
            adapter.notifyDataSetChanged();
            SavePreferences("List", deleteVal, true);
        }
        else
        {
            Utils.toastMessage("Nothing to Delete", this);
        }
    }
   
    //set up response to click on buttons
=======
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        //lets the user know their category was added
        Utils.toastMessage("Category Added", this);
    }

    //----------------------------------------------------------------------------------------
    //This is our onclick listener
    //----------------------------------------------------------------------------------------

>>>>>>> origin/deleting_categories
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {//when this happens user is returned to mainbudget screen
            case finishAddingItemsToBudget:
                finish();
                break;
<<<<<<< HEAD
             //deletes a cetegory from the list
            case DeleteB:
                //create a dialog box to delete items from category list
                final AlertDialog.Builder deleteAlert = new AlertDialog.Builder(this);
                //set title, contents and delete option in alert dialog box
                deleteAlert.setTitle("Select an Category to delete:");
                deleteAlert.setSingleChoiceItems(EditMyList.toArray(new CharSequence[EditMyList.size()]),0, null);
                deleteAlert.setPositiveButton("Delete",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1)
                            {
                                int deletedCat = ((AlertDialog)arg0).getListView().getCheckedItemPosition();
                                String deleteCat = String.valueOf(deletedCat);
                                delete(deletedCat, deleteCat);
                            }
                        });
                //set the cancel option in the dialog box
                deleteAlert.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1)
                    {
                    }
                });
                AlertDialog deleteAlertDiag = deleteAlert.create();
                deleteAlertDiag.show();
                break;
=======
>>>>>>> origin/deleting_categories
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
<<<<<<< HEAD
=======

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

>>>>>>> origin/deleting_categories
    //This function executes when the user presses the add button
    public void createNewItem(View v)
    {
        //Getting the user input from the edit texts
<<<<<<< HEAD
        if(priceEntry.getText().toString().equals("") || nameEntry.getText().toString().equals(""))
        {
            Utils.toastMessage("Must Input Price and Name", this);
            return;
        }
        //setting up the editext to pass our data into fire base
        newItemPrice = priceEntry.getText().toString();
        newItemName = nameEntry.getText().toString();
        newItemDate = dateEntry.getText().toString();
        newItemDate = newItemDate.replace("/","");
=======
        checkForAllFields();
        getInfoForNewItem();
>>>>>>> origin/deleting_categories

        //Creating a new Item object and setting the price and name
        Item newItem = new Item(newItemName);

        //Attempt to set item price or show message
        try{
            newItem.setPrice(Double.valueOf(newItemPrice));
        } catch(NumberFormatException e){
            Utils.toastMessage("Input Valid Price", this);
            return;
        }
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

<<<<<<< HEAD
=======
    public void addItemsToListView()
    {
>>>>>>> origin/deleting_categories
        //This adds the item to the list view
        currentItemsAddedToList.add(new ListElement(newItemName, newItemPrice, newItemCategory));
        aa.notifyDataSetChanged();

        nameEntry.getText().clear();
        priceEntry.getText().clear();
<<<<<<< HEAD
    }
=======

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


>>>>>>> origin/deleting_categories
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

        //If the budget week for the current item is null, then we create a new WeekLongbudget
        if(usersBudgets.get(date) == null){
            System.out.println("Creating new week");
            WeekLongBudget newWeek = new WeekLongBudget(date);
            return newWeek;
        } else{
            return usersBudgets.get(date);        //Return the WeekLongBudget for the date if it isn't null
        }
    }
<<<<<<< HEAD
=======

>>>>>>> origin/deleting_categories
    //inputs item into right arraylist using the items week. Feature works if for example user
    // wants to add item to past or future
    public void addItemToWeek(Item item)
    {
        String date = item.getDate();                   //Get the date of the item
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
<<<<<<< HEAD
=======



    //----------------------------------------------------------------------------------------
    //Code for the toolbar
    //----------------------------------------------------------------------------------------


>>>>>>> origin/deleting_categories
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

<<<<<<< HEAD

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
                        addCategory(newCat);
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
=======
>>>>>>> origin/deleting_categories


}
