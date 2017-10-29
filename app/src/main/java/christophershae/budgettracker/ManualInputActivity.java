package christophershae.budgettracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static christophershae.budgettracker.R.id.Edit_List;
import static christophershae.budgettracker.R.id.finishAddingItemsToBudget;


public class ManualInputActivity extends AppCompatActivity implements View.OnClickListener {
    //Buttons for the interface
    Button Add;
    Button Finish;
    EditText edit_list;

    //ArrayAdapter to fill in spinner

    ArrayAdapter<CharSequence> adapter;
    List<CharSequence> EditMyList;
    Spinner spinner;
    String get_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_input);

        //create drop down menu to view the categories of expenses
        //Define spinner from xml file
        spinner = (Spinner) findViewById(R.id.Menu_C);
        //define button from xml file
        Add = (Button) findViewById(Edit_List);
        Add.setOnClickListener(this);
        //define button for Finish
        Finish =(Button) findViewById(finishAddingItemsToBudget);
        Finish.setOnClickListener(this);
        //define edittext
        edit_list = (EditText) findViewById(R.id.text_editlist);
        //define list
        EditMyList = new ArrayList<CharSequence>(Arrays.<CharSequence>asList(
                getResources().getStringArray(R.array.list_Categories)));

        adapter = new ArrayAdapter<CharSequence>(ManualInputActivity.this,R.layout.dropdown_editlist,
                EditMyList);
        //specify layout for now basic later on design it better
        adapter.setDropDownViewResource(R.layout.dropdown_editlist);
        //apply the adapter create list to the Spinner(drop down list)
        spinner.setAdapter(adapter);



        }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case Edit_List:
                //when edit is pressed list should update to include
                //current category
                //set get_Text to string
                get_text = edit_list.getText().toString();
                //then add it to my array_list of strings and
                //notify the spinner about the change
                EditMyList.add(get_text);
                adapter.notifyDataSetChanged();

                //lets the user know that their category was added
                Toast.makeText(ManualInputActivity.this, "Category Added", Toast.LENGTH_LONG).show();
                break;
            case finishAddingItemsToBudget:
                 finish();
                break;
        }
     }
}
