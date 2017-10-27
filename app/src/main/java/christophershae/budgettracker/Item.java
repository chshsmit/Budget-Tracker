package christophershae.budgettracker;

/**
 * Created by chrissmith on 10/26/17.
 */

public class Item {

    public String name;
    public String category;
    public double price;

    public String storeName;


    //Here is our constructed method
    public Item(String name){
        this.name = name;
    }

    //---------------------------------------------------------------------------------------------
    //These are all setter and getter functions
    //---------------------------------------------------------------------------------------------

    public void setPrice(double price){
        this.price = Math.round(price *100D) / 100;
    }

    public void setCategory(String category){
        this.category = category;
    }

    public void setStoreName(String storeName){
        this.storeName = storeName;
    }

    public double getPrice(){
        return this.price;
    }

    public String getCategory(){
        return this.category;
    }

    public String getStoreName(){
        return this.storeName;
    }



}

