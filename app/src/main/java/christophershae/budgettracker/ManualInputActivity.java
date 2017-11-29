package christophershae.budgettracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static android.R.attr.category;
import static christophershae.budgettracker.R.id.DeleteB;
import static christophershae.budgettracker.R.id.addItemToBudget;
import static christophershae.budgettracker.R.id.item;
import static christophershae.budgettracker.R.id.itemNameView;
import static christophershae.budgettracker.R.id.snap;
import static java.security.AccessController.getContext;
import android.content.Intent;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


import static christophershae.budgettracker.R.id.Edit_List;
import static christophershae.budgettracker.R.id.finishAddingItemsToBudget;


public class ManualInputActivity extends AppCompatActivity implements View.OnClickListener {


    private FirebaseAuth firebaseAuth;
    private DatabaseReference mFireBaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    private String userId;


    Map<String, WeekLongBudget> usersBudgets = new HashMap<>();


    //Buttons for the interface
    Button Add;
    Button Finish;
    Button deleteCategory;
    EditText edit_list;

    //ArrayAdapter to fill in spinner
    ArrayAdapter<CharSequence> adapter;
    List<CharSequence> EditMyList;
    Spinner spinner;
    String get_text;
    SharedPreferences store;
    //make an array
    public String [] Categories_list = {"Food" ,"Rent", "Gas", "Personal Items", "Household Items",
            "Groceries", "Entertainment"};
    //----------------------------------------------------------------------------------------
    //This code has all the functions that need to be overridden
    //----------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_input);

        //Firebase stuff
        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = Utils.getDatabase();
        mFireBaseDatabase = mFirebaseInstance.getReference("users");

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();

        mFireBaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Clearing the current user budgets
                usersBudgets.clear();

                //Looping through all children within the users node and adding each child
                //to the users budgets
                for(DataSnapshot snapshot: dataSnapshot.child(userId).getChildren()){
                    //System.out.println(snapshot.getKey());
                    usersBudgets.put(snapshot.getKey(),snapshot.getValue(WeekLongBudget.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("You arent reDING CORRECTLTY");
            }
        });



        System.out.println("The current user ID is: " +userId);


        //Instantiating the adapter for the listview
        currentItemsAddedToList = new ArrayList<ListElement>();
        aa = new MyAdapter(this, R.layout.manually_input_list_element, currentItemsAddedToList);
        ListView myListView = (ListView) findViewById(R.id.itemsAddedToBudgetAlready);
        myListView.setAdapter(aa);
        aa.notifyDataSetChanged();

        //Edit texts for the price and name entry
        priceEntry = (EditText) findViewById(R.id.itemPriceEntry);
        nameEntry = (EditText) findViewById(R.id.itemNameEntry);
        dateEntry = (EditText) findViewById(R.id.setDate);
        dateEntry.setText(slashedDate.format(new Date()));
        //create drop down menu to view the categories of expenses
        //Define spinner from xml file
        spinner = (Spinner) findViewById(R.id.Menu_C);
        //define button from xml file
        Add = (Button) findViewById(Edit_List);
        Add.setOnClickListener(this);
        //define button for Finish
        Finish =(Button) findViewById(finishAddingItemsToBudget);
        Finish.setOnClickListener(this);
        //delete button
        deleteCategory = (Button) findViewById(R.id.DeleteB);
        deleteCategory.setOnClickListener(this);
        //define edittext
        edit_list = (EditText) findViewById(R.id.text_editlist);
        //define list
        EditMyList = new ArrayList<CharSequence>(Arrays.<CharSequence>asList(Categories_list));


        adapter =  new ArrayAdapter<CharSequence>(ManualInputActivity.this,R.layout.dropdown_editlist,
                EditMyList);
        //specify layout for now basic later on design it better
        adapter.setDropDownViewResource(R.layout.dropdown_editlist);
        //apply the adapter create list to the Spinner(drop down list)
        spinner.setAdapter(adapter);
        LoadPreferences();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                //set selectem items
                //int spinnerPosition= spinner.getSelectedItemPosition();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

    }
    //save listview data
    protected void SavePreferences(String key, String value) {
        //
        SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(this);

        String s=data.getString(key,""); //to fetch previous stored values

        s=s+"!"+value;   //to add new value to previous one

        data.edit().putString(key,s).commit();
    }


    //load listview data
    protected void LoadPreferences(){
        SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(this);
        String dataSet = data.getString("List", "Add a Category ......");
            adapter.remove("");
            adapter.remove("");
        if(dataSet.contains("!")){ //to check if previous items are there or not

            String rows[]=dataSet.split("!"); //to get individual rows of list

            for(int i=1;i<rows.length;i++){
                adapter.add(rows[i]);   //to add each value to the list
                adapter.notifyDataSetChanged();
            }
        } else{
            adapter.add(dataSet);
            adapter.notifyDataSetChanged();
        }
    }
    //method to add a category
    public void addCategory(){
        get_text = edit_list.getText().toString();
        //check if input is empty or contains strings
        if(!get_text.isEmpty() && get_text.length() > 0)
        {
           adapter.add(get_text);
           //refresh data
           adapter.notifyDataSetChanged();
           edit_list.setText("");
           SavePreferences("List", get_text);
        }
        else
        {
            Toast.makeText(ManualInputActivity.this, "No Category To Add", Toast.LENGTH_LONG).show();
        }
    }
    //method to delete
    public void delete()
    {
        //String getList = edit_list.getText().toString();

        //get the postion selected
        int pos = spinner.getSelectedItemPosition();
        //user has selected a category if >-1
        if(pos > -1)
        {
            adapter.remove(EditMyList.get(pos));
            Toast.makeText(ManualInputActivity.this, "Category Deleted", Toast.LENGTH_LONG).show();
            adapter.notifyDataSetChanged();
            edit_list.setText("");
        }
        else
        {
            Toast.makeText(ManualInputActivity.this, "Nothing to Delete", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case Edit_List:
                addCategory();
                //lets the user know their category was added
                Toast.makeText(ManualInputActivity.this, "Category Added", Toast.LENGTH_LONG).show();
                break;
            case finishAddingItemsToBudget:
                //testUser.getMap().get("10292017").getAmountForEachCategory();
                //System.out.println(testUser.getMap().get("10292017").getTotalAmountOfMoneySpent());
     //           load();
                finish();
                break;
            case DeleteB:
                 delete();

                 break;
        }

    }


    //----------------------------------------------------------------------------------------
    //This code handles the generation of the list view
    //----------------------------------------------------------------------------------------

    //Instantiating the list and its adapter
    ArrayList<ListElement> currentItemsAddedToList;
    private MyAdapter aa;

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
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, s, duration);
                    toast.show();
                }
            });

            return newView;
        }
    }


    //----------------------------------------------------------------------------------------
    //This code creates a new item object and adds it to a user's current weeklong budget object
    //----------------------------------------------------------------------------------------

    //Global variables
    User testUser = new User("Chris");



    //Global variables for the item price, name, date, and category
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

    //This function executes when the user presses the add button
    public void createNewItem(View v)
    {

        //Getting the user input from the edit texts
        newItemPrice = priceEntry.getText().toString();
        newItemName = nameEntry.getText().toString();
        newItemDate = dateEntry.getText().toString();
        newItemDate = newItemDate.replace("/","");

        System.out.println("The user inputted the price as: "+newItemPrice);

        //Creating a new Item object and setting the price and name
        Item newItem = new Item(newItemName);
        newItem.setPrice(Double.valueOf(newItemPrice));
        System.out.println("This item costs: "+newItem.getPrice());

        //Getting the category from the spinner
        newItemCategory = spinner.getSelectedItem().toString();
        newItem.setCategory(newItemCategory);
        System.out.println(newItemCategory);   //debugging function

        //Setting the date the object was purchased to the current date
        //newItemDate = sdf.format(new Date());
        newItem.setDate(newItemDate);
        System.out.println("The current date is:" +newItem.getDate());    //debugging function

        //Adding the new item to the test user's current week budget
        testUser.addItem(newItem);

        //Add the item to the correct weeks budget
        addItemToWeek(newItem);



        //This adds the item to the list view
        currentItemsAddedToList.add(new ListElement(newItemName, newItemPrice, newItemCategory));
        aa.notifyDataSetChanged();

    }





    //Retrieving the correct weeklong budget object to store the new item in
    public WeekLongBudget getWeek(String date)
    {
        //DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
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


    }

    //This function decrements the date so it adds it to the correct weeklong budget
    public String decrementDate(Date date)
    {

        //Get an instance of the calendar and set the time to the date the item was purchased
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_WEEK);    //Get which day of the week that was

        //Depending on what day it is, decrement the date to be the most recent sunday
        //If it is Sunday, then it won't change the date at all
        switch(day){
            case Calendar.MONDAY:
                calendar.setTime(date);
                calendar.add(Calendar.DATE, -1);
                date = calendar.getTime();
                break;

            case Calendar.TUESDAY:
                calendar.setTime(date);
                calendar.add(Calendar.DATE, -2);
                date = calendar.getTime();
                break;

            case Calendar.WEDNESDAY:
                calendar.setTime(date);
                calendar.add(Calendar.DATE, -3);
                date = calendar.getTime();
                break;

            case Calendar.THURSDAY:
                calendar.setTime(date);
                calendar.add(Calendar.DATE, -4);
                date = calendar.getTime();
                break;

            case Calendar.FRIDAY:
                calendar.setTime(date);
                calendar.add(Calendar.DATE, -5);
                date = calendar.getTime();
                break;

            case Calendar.SATURDAY:
                calendar.setTime(date);
                calendar.add(Calendar.DATE, -6);
                date = calendar.getTime();
                break;

            default:
                break;
        }


        return sdf.format(date);   //return the decremented date as a string
    }




}
