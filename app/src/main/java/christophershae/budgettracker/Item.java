package christophershae.budgettracker;

import java.io.Serializable;

/**
 * Created by chrissmith on 10/26/17.
 */

public class Item implements Serializable
{

    //---------------------------------------------------------------------------------------------
    //All of our global variables
    //---------------------------------------------------------------------------------------------
    public String name;
    public String category;
    public double price;
    public String date;
    public String storeName;

    //---------------------------------------------------------------------------------------------
    //These are all of our constructors
    //---------------------------------------------------------------------------------------------
    //Here is our constructed method
    public Item(String name)
    {
        this.name = name;
    }

    public Item(String name, String category, double price, String date)
    {
        this.name = name;
        this.category = category;
        this.price = price;
        this.date = date;
    }

    public Item(){}

    //---------------------------------------------------------------------------------------------
    //These are all of the setter functions
    //---------------------------------------------------------------------------------------------
    public void setPrice(double price){
        this.price = price;
    }

    public void setCategory(String category){
        this.category = category;
    }

    public void setDate(String date){ this.date = date;}

    public void setStoreName(String storeName){
        this.storeName = storeName;
    }

    //---------------------------------------------------------------------------------------------
    //These are all of the getter functions
    //---------------------------------------------------------------------------------------------

    public String getName(){
        return this.name;
    }

    public double getPrice(){
        return (double) Math.round(this.price * 100.0) /100.0;
    }

    public String getCategory(){
        return this.category;
    }

    public String getStoreName(){
        return this.storeName;
    }

    public String getDate() { return this.date;}



}


