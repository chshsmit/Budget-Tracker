
package christophershae.budgettracker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.util.Calendar;

/**
 * Created by Ravi Tamada on 07/10/16.
 * www.androidhive.info
 */


public class User {

    public String name;
    public String email;
    private String pass;
    private Map<String, WeekLongBudget> items;


    //maybe more



    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {

    }




    //Preliminary constructor, might expand with firebase integration
    public User(String name) {

        this.name = name;
        //this.email = email;
        items = new HashMap();
    }

    private void changePassword(String pass)
    {
        this.pass = pass;
    }

    public Map<String, WeekLongBudget> getMap()
    {
        return items;
    }


    //returns null if a week for that date doesnt exists
    public WeekLongBudget getWeek(String date)
    {
        if(items.get(date) != null )
        {
            System.out.println("Its already sunday");
            return items.get(date);
        }
        else
        {
            date = decrementDate(new Date());
            if(items.get(date) != null )
            {
                return items.get(date);
            }

        }

        System.out.println("Creating a new list");
        System.out.println("The current day is "+date);
        WeekLongBudget newWeek = new WeekLongBudget(date);
        items.put(date, newWeek);
        return newWeek;
    }


    SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyy");

    //inputs item into right arraylist using the items week. Feature works if for example user
    // wants to add item to past or future
    public void addItem(Item item)
    {
        String date = item.getDate();
        WeekLongBudget inputWeek = getWeek(date);
        inputWeek.addItem(item);
    }
    
    public String decrementDate(Date date)
    {

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

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

        return sdf.format(date);
    }
    
}
