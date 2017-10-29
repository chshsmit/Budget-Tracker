
package christophershae.budgettracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ravi Tamada on 07/10/16.
 * www.androidhive.info
 */


public class User {

    public String name;
    public String email;
    private String pass;
    private Map<String, ArrayList> items;
    public ArrayList currentWeek;


    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {
    }


    //Preliminary constructor, might expand with firebase integration
    public User(String name, String email) {
        this.name = name;
        this.email = email;
        items = new HashMap();
    }

    private void changePassword(String pass)
    {
        this.pass = pass;
    }

    public Map<String, ArrayList> getMap()
    {
        return items;
    }

    //adds a new week to hash but assumes the input date is a sunday
    public void addWeek(String date)
    {
        items.put(date, new ArrayList());
    }

    //returns null if a week for that date doesnt exists
    public ArrayList getWeek(String date)
    {
        if(items.get(date) != null )
        {
            return items.get(date);
        }
        else
        {
            for(int i = 1; i < 7; i++)//loops keeps decrementing the date to find sunday
            {
               date = decrementDate(date);
               if(items.get(date) != null )
               {
                    return items.get(date);
               }
            }
        }
        return null;
    }

    //inputs item into right arraylist using the items week. Feature works if for example user
    // wants to add item to past or future
    public void addItem(Item item)
    {
        String date = item.getDate();
        ArrayList inputWeek = getWeek(date);
        inputWeek.add(item);
    }
    
    public static String decrementDate(String date)
    {
        //check string comparison
        String day = date.substring(2,4);
        if(day.equals("01"))
        {
             String month = date.substring(0,2);
            String year = date.substring(4,8);
            day = "31";
            month = Integer.toString(Integer.parseInt(month)-1);
            String newDate = month + day + year;
            return newDate;
        }
        else
        {
            String month = date.substring(0,2);
            String year = date.substring(4,8);
            day = Integer.toString(Integer.parseInt(day)-1);
            String newDate = month + day + year;
            return newDate;
        }
    }
    
}
