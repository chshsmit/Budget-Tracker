package christophershae.budgettracker;

import android.support.v4.media.MediaMetadataCompat;

import org.junit.Test;

import java.text.SimpleDateFormat;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyy");
    Item newItem = new Item("Bagels");
    private static final double DELTA = 1e-15;


    //------------------------------------------------------------------------
    //Tests for the item class
    //------------------------------------------------------------------------

    @Test    //1
    public void itemNameSetCorrectly() throws Exception
    {
        assertEquals("Bagels", newItem.name);
    }

    @Test    //2
    public void itemPriceSetCorrectly() throws Exception
    {
        newItem.setPrice(1.29);
        assertEquals(1.29, newItem.getPrice(), DELTA);
    }


    @Test    //3
    public void itemDateSetCorrectly() throws Exception
    {
       newItem.setDate("10292017");
        assertEquals("10292017", newItem.getDate());
    }

    @Test    //4
    public void itemCategorySetCorrectly() throws Exception
    {
        newItem.setCategory("Food");
        assertEquals("Food", newItem.getCategory());
    }

    @Test    //5
    public void storeNameSetCorrectly() throws Exception
    {
        newItem.setStoreName("Trader Joe's");
        assertEquals("Trader Joe's", newItem.getStoreName());
    }
}