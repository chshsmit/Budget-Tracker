package christophershae.budgettracker;

import android.graphics.Bitmap;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static christophershae.budgettracker.R.string.finish;


public class WeekLongBudget {


    //---------------------------------------------------------------------------------------------
    // Global Variables
    //---------------------------------------------------------------------------------------------

    public ArrayList<Item> allItems = new ArrayList<>();
    public Map<String, Double> costOfAllCategories;
    public double totalAmountSpent;
    public double goalTotal;
    public double totalIncomeAccumulated;
    public int photoCounter;
    public double netIncome;
    public String startDate;


    //---------------------------------------------------------------------------------------------
    // Constructors
    //---------------------------------------------------------------------------------------------

    public WeekLongBudget(){}

    public WeekLongBudget(String date){
        this.startDate = date;
        this.totalAmountSpent = 0.00;
        this.totalIncomeAccumulated = 0.00;
        this.netIncome = Math.round((this.totalIncomeAccumulated - this.totalAmountSpent) * 100.00) / 100.00;
        this.photoCounter = 0;
        this.costOfAllCategories = new HashMap<>();
    }

    //---------------------------------------------------------------------------------------------
    //Functions that deal with adding and removing items to the budget
    //---------------------------------------------------------------------------------------------


    public void addItem(Item item)
    {
        this.allItems.add(item);

        calculateTotal();
        setNetIncome();
    }

    public void removeItem(int index)
    {
        this.totalAmountSpent -= Math.round(this.allItems.get(index).getPrice() * 100.00) / 100.00;
        setNetIncome();
        this.allItems.remove(index);


        calculateTotal();
    }


    //This calculates the total amount of money spent
    public void calculateTotal()
    {
        this.totalAmountSpent = 0;
        for(Item eachItem: allItems){
            this.totalAmountSpent += eachItem.getPrice();
        }
    }



    //---------------------------------------------------------------------------------------------
    // Setter functions
    //---------------------------------------------------------------------------------------------

    public void setGoalTotal(double goalTotal)
    {
        this.goalTotal = Math.round(goalTotal * 100.00) / 100.00;
    }

    public void addMoneyToIncome(double income)
    {

        this.totalIncomeAccumulated += Math.round(income * 100.00) / 100.00;
        setNetIncome();

    }

    public void setNetIncome()
    {
        this.netIncome = Math.round((this.totalIncomeAccumulated - this.totalAmountSpent) * 100.00) / 100.00;
    }

    public void increasePhotoCount()
    {
        this.photoCounter += 1;
    }


    //---------------------------------------------------------------------------------------------
    // Getter functions
    //---------------------------------------------------------------------------------------------
    public Double getNetIncome(){
        return Math.round(this.netIncome * 100.00) / 100.00;
    }

    public Double getTotalAmountSpent()
    {
        return Math.round(this.totalAmountSpent *100.00) / 100.00;
    }

    public Double getTotalIncomeAccumulated(){ return this.totalIncomeAccumulated; }

    public Double getGoalTotal(){ return Math.round(this.goalTotal * 100.00) / 100.00; }

    public int getPhotoCounter(){ return this.photoCounter;}

    public String getStartDate(){return this.startDate;}

    public ArrayList<Item> getAllItems(){return this.allItems;}


    public Map<String, Double> getCostOfAllCategories()
    {

        //If the current cost of all categories is null then create a new hashmap
        if(this.costOfAllCategories == null) {this.costOfAllCategories = new HashMap<>();}

        //Clear the hashmap to recalculate totals
        this.costOfAllCategories.clear();

        //New variable for new price to be inserted into map
        double newPrice;

        //Loop through all items that have been added to the budget
        for(Item item: allItems){
            if(item == null) break;  //When we reach a null item when can end the loop
            newPrice = 0.00;
            if(this.costOfAllCategories.containsKey(item.category))    //If the category aready exists, then add the price to the current price
            {
                newPrice = Math.round((item.getPrice() + this.costOfAllCategories.get(item.category)) * 100.00) / 100.00;
                this.costOfAllCategories.put(item.category, newPrice);
            } else {
                this.costOfAllCategories.put(item.category, item.getPrice());   //Else the price for that category is the price of that item
            }
        }

        return this.costOfAllCategories;     //Return the hashmap
    }






}