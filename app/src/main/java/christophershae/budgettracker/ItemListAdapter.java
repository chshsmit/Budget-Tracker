package christophershae.budgettracker;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ItemListAdapter extends ArrayAdapter<Item> {

    private static final String TAG = "ItemListAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        TextView name;
        TextView date;
        TextView price;
    }

    /**
     * Default constructor for the ItemListAdapter
     * @param context
     * @param resource
     * @param objects
     */
    public ItemListAdapter(Context context, int resource, ArrayList<Item> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    SimpleDateFormat slashedDate = new SimpleDateFormat("MM/dd/yyyy");
    SimpleDateFormat startingVersion = new SimpleDateFormat("MMddyyyy");

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the persons information
        String name = getItem(position).getName();
        String date = getItem(position).getDate();
        double price = Math.round(getItem(position).getPrice() * 100.00) / 100.00;

        try
        {
            //Adding the WeekLongBudget to all of the users budgets
            Date itemDate = startingVersion.parse(date);
            date = slashedDate.format(itemDate);
        }
        catch (ParseException e)    //This exception catches the parsing of the date
        {
            e.printStackTrace();
        }


        //Create the transaction object with the information
        Item item = new Item(name);
        item.setDate(date);
        item.setPrice(price);

        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        ViewHolder holder;


        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.textView1);
            holder.date = (TextView) convertView.findViewById(R.id.textView2);
            holder.price = (TextView) convertView.findViewById(R.id.textView3);
            holder.price.setTypeface(holder.price.getTypeface(), Typeface.BOLD);

            result = convertView;

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        result.startAnimation(animation);
        lastPosition = position;

        holder.name.setText(item.getName());
        holder.date.setText(item.getDate());
        String priceText = "$ " + Double.toString(item.getPrice());
        holder.price.setText(priceText);


        return convertView;
    }
}
