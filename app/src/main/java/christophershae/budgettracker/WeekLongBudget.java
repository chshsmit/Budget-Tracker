package christophershae.budgettracker;

import android.graphics.Bitmap;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static christophershae.budgettracker.R.string.finish;


public class WeekLongBudget {

    public ArrayList<Item> allItems = new ArrayList<>();
    public Map<String, Double> costOfAllCategories;

    public List<Bitmap> myImages = new ArrayList<>();
    //public Map<String, Double> amountForEachCategory;
    //public double totalAmountOfMoneySpent;

    public double totalAmountSpent;
    public double goalTotal;
    public double totalIncomeAccumulated;

    public double netIncome;

    public String startDate;

    public WeekLongBudget(){}

    public WeekLongBudget(String date){
        this.startDate = date;
        this.totalAmountSpent = 0.00;
        this.totalIncomeAccumulated = 0.00;
        this.netIncome = Math.round((this.totalIncomeAccumulated - this.totalAmountSpent) * 100.00) / 100.00;
        this.costOfAllCategories = new HashMap<>();
    }


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

    public void addImageToList(Bitmap image)
    {
        myImages.add(image);
    }

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

    public void setGoalTotal(double goalTotal){
        this.goalTotal = Math.round(goalTotal * 100.00) / 100.00;
    }

    public void addMoneyToIncome(double income){

        this.totalIncomeAccumulated += Math.round(income * 100.00) / 100.00;
        setNetIncome();

    }

    public void setNetIncome()
    {
        this.netIncome = Math.round((this.totalIncomeAccumulated - this.totalAmountSpent) * 100.00) / 100.00;
    }

    public void clearImages()
    {
        this.myImages.clear();
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

    public Map<String, Double> getCostOfAllCategories()
    {

        if(this.costOfAllCategories == null) {return null;}

        this.costOfAllCategories.clear();
        //System.out.println(this.costOfAllCategories.containsKey("Food"));
        double newPrice;
        for(Item item: allItems){
            if(item == null) break;
            newPrice = 0.00;
            if(this.costOfAllCategories.containsKey(item.category)){
                newPrice = Math.round((item.getPrice() + this.costOfAllCategories.get(item.category)) * 100.00) / 100.00;
                this.costOfAllCategories.put(item.category, newPrice);
                //System.out.println("You have a total of $"+newPrice+" spent in the category "+item.category);
            } else {
                this.costOfAllCategories.put(item.category, item.getPrice());
                //System.out.println("You have a total of $"+item.getPrice()+" spent in the category "+item.category);
            }
        }

        return this.costOfAllCategories;
    }

    public String getStartDate(){return this.startDate;}

    public ArrayList<Item> getAllItems(){return this.allItems;}





}