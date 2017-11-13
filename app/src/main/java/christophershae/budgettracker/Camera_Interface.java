package christophershae.budgettracker;

import android.content.ClipData;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Scanner;

public class Camera_Interface extends AppCompatActivity {
    TextView item, item2, item3, item4;
    //target for dropping items into categories
    ListView category_target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera__interface);

        //defining our textViews
        item  = (TextView) findViewById(R.id.item);
        item2 = (TextView) findViewById(R.id.item2);
        item3 = (TextView) findViewById(R.id.item3);
        item4 = (TextView) findViewById(R.id.item4);
        //defining listView
        category_target = (ListView) findViewById(R.id.Category_View);

        //making textView respond to drag Onclicklistner
        item.setOnLongClickListener(longClickListener);
        item2.setOnLongClickListener(longClickListener);
        item3.setOnLongClickListener(longClickListener);
        item4.setOnLongClickListener(longClickListener);
        //making listView respond to drag
        category_target.setOnDragListener(dragListener);

    }
    //declare and define listener
    View.OnLongClickListener longClickListener = new View.OnLongClickListener(){
         @Override
        public boolean onLongClick(View v){
             ClipData item_description = ClipData.newPlainText("", "");
             //
             View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v);
             v.startDrag(item_description, myShadow, v,0);
              return true;
         }
    };
    ListView.OnDragListener dragListener = new View.OnDragListener(){

        @Override

        public boolean onDrag(View v, DragEvent event) {
            int dragEvent = event.getAction();
            switch (dragEvent){
                case DragEvent.ACTION_DRAG_ENTERED:
                    //notifies which view we have entered,
                    // like item or item2 and so on

//                    final View view = (View) event.getLocalState();

                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:
                    break;
            }
            return true;
        }
    };
}
