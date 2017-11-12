package christophershae.budgettracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrissmith on 10/29/17.
 */

public class WeekLongBudget {

    public ArrayList<Item> allItems = new ArrayList<>();
    public Map<String, Double> costOfAllCategories;
    //public Map<String, Double> amountForEachCategory;
    //public double totalAmountOfMoneySpent;

    public double totalAmountSpent;
    public double goalTotal;

    public String startDate;

    public WeekLongBudget(){}

    public WeekLongBudget(String date){
        this.startDate = date;
        this.totalAmountSpent = 0.00;
        this.costOfAllCategories = new HashMap<>();
    }


    public void addItem(Item item)
    {
        this.allItems.add(item);
        this.totalAmountSpent += item.getPrice();
    }

    public Double getTotalAmountSpent()
    {
        return this.totalAmountSpent;
    }

    public void setGoalTotal(double goalTotal){
        this.goalTotal = Math.round(goalTotal * 100.0) / 100.0;
    }
    

    public Double getGoalTotal(){ return this.goalTotal; }


    public Map<String, Double> getCostOfAllCategories()
    {
        this.costOfAllCategories.clear();
        double newPrice;
        for(Item item: allItems){
            newPrice = 0.00;
            if(this.costOfAllCategories.containsKey(item.category)){
                newPrice = Math.round((item.getPrice() + this.costOfAllCategories.get(item.category)) * 100.0) / 100.0;
                this.costOfAllCategories.put(item.category, newPrice);
                System.out.println("You have a total of $"+newPrice+" spent in the category "+item.category);
            } else {
                this.costOfAllCategories.put(item.category, item.getPrice());
                System.out.println("You have a total of $"+item.getPrice()+" spent in the category "+item.category);
            }
        }

        return this.costOfAllCategories;
    }


    public String getStartDate(){return this.startDate;}





}
