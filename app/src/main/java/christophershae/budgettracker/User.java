
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
    public ArrayList<Item> currentWeek = new ArrayList<>();
    //maybe more



    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {

    }


    public User(String name) {
        this.name = name;
        //this.email = email;
        items = new HashMap();
        ArrayList firstweek = new ArrayList();
    }

    private void changePassword(String pass)
    {
        this.pass = pass;
    }

    public Map<String, ArrayList> getMap()
    {
        return items;
    }

    public void addWeek(String date)
    {
        items.put(date, new ArrayList());
    }

    public void addItem(String item, String date)
    {
        currentWeek = items.get(date);
    }









}
