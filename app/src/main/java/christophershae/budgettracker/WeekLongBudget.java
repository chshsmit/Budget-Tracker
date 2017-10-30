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

    public double totalAmountSpent;

    public String startDate;



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

    public Double getTotalAmountOfMoneySpent()
    {
        return this.totalAmountSpent;
    }

    public Map<String, Double> getAmountForEachCategory()
    {
        double newPrice = 0.0;
        for(Item item: allItems){
            if(this.costOfAllCategories.containsKey(item.category)){
                newPrice = item.getPrice() + this.costOfAllCategories.get(item.category);
                this.costOfAllCategories.put(item.category, newPrice);
            } else {
                this.costOfAllCategories.put(item.category, item.getPrice());
            }
        }

        return this.costOfAllCategories;
    }


    public String getStartDate(){return this.startDate;}





}
