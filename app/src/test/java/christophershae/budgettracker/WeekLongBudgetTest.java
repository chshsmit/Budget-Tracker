package christophershae.budgettracker;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;


public class WeekLongBudgetTest {

    private static final double DELTA = 1e-15;

    private WeekLongBudget testbudget = new WeekLongBudget("11242017");
    private Map<String, Double> testMap = new HashMap<>();


    //------------------------------------------------------------------------
    //Tests for the WeekLongBudget class
    //------------------------------------------------------------------------


    //---------------------------------------------------
    //These are to ensure the constructor works properly
    //---------------------------------------------------
    @Test     //1
    public void constructorSetDateCorrectly() throws Exception
    {
        assertEquals("11242017", testbudget.getStartDate());
    }

    @Test    //2
    public void constructorSetTotalSpentCorrectly() throws Exception
    {
        assertEquals(0.00, testbudget.getTotalAmountSpent(), DELTA);
    }

    @Test    //3
    public void constructorSetIncomeCorrectly() throws Exception
    {
        assertEquals(0.00, testbudget.getNetIncome(), DELTA);
    }


    @Test    //4
    public void constructorCreatedCostOfAllCategories() throws Exception
    {
        assertEquals(testMap, testbudget.costOfAllCategories);
    }


    //---------------------------------------------------
    //Testing Setter Functions
    //---------------------------------------------------

    @Test    //5
    public void testingGoalTotal() throws Exception
    {
        testbudget.setGoalTotal(1000.00);
        assertEquals(1000.00, testbudget.getGoalTotal(), DELTA);
    }

    @Test    //6
    public void changingGoalTotal() throws Exception
    {
        testbudget.setGoalTotal(1000);
        testbudget.setGoalTotal(1250);
        assertEquals(1250.00, testbudget.getGoalTotal(), DELTA);
    }


    //------------------
    //Testing of Income
    //------------------

    @Test    //7
    public void addingMoneyToIncome1() throws Exception
    {
        testbudget.addMoneyToIncome(543);
        assertEquals(543, testbudget.getTotalIncomeAccumulated(), DELTA);
    }

    @Test    //8
    public void addingMoneyToIncome2() throws Exception
    {
        testbudget.addMoneyToIncome(457);
        testbudget.addMoneyToIncome(543);
        assertEquals(1000.00, testbudget.getTotalIncomeAccumulated(), DELTA);
    }

    @Test    //9
    public void addingMoneyToIncome3() throws Exception
    {
        testbudget.addMoneyToIncome(1);
        testbudget.addMoneyToIncome(1200);
        testbudget.addMoneyToIncome(99.25);
        assertEquals(1300.25, testbudget.getTotalIncomeAccumulated(), DELTA);
    }


    //---------------------------------------------------
    //Creating test items
    //---------------------------------------------------

    final private Item testItem1 = new Item("Bagels", "Food", 2.99, "11252017");
    final private Item testItem2 = new Item("Movie Tickets", "Entertainment", 13.25, "11252017");
    final private Item testItem3 = new Item("Shoes", "Personal", 45.00, "11252017");
    final private Item testItem4 = new Item("Pizza", "Food", 5.00, "11252017");
    final private Item testItem5 = new Item("Rent", "Rent", 1450, "11252017");
    final private Item testItem6 = new Item("Toothpaste", "Personal", 3.00, "11252017");
    final private Item testItem7 = new Item("Chevron", "Gas", 31.18, "11252017");
    final private Item testItem8= new Item("Books", "School", 87.00, "11252017");
    final private Item testItem9 = new Item("Pen", "School", 2.00, "11252017");
    final private Item testItem10 = new Item("TV", "Entertainment", 150.00, "11252017");


    //------------------------------
    //Testing Addition of Items
    //------------------------------

    @Test    //10
    public void addingOneItemTotalAmountSpent() throws Exception
    {
        testbudget.addItem(testItem1);
        assertEquals(2.99, testbudget.getTotalAmountSpent(), DELTA);
    }

    @Test    //11
    public void addingTwoItemsTotalAmountSpent() throws Exception
    {
        testbudget.addItem(testItem1);
        testbudget.addItem(testItem2);
        assertEquals(16.24, testbudget.getTotalAmountSpent(), DELTA);
    }

    @Test    //12
    public void addingThreeItemsTotalAmountSpent() throws Exception
    {
        testbudget.addItem(testItem1);
        testbudget.addItem(testItem2);
        testbudget.addItem(testItem3);
        assertEquals(61.24, testbudget.getTotalAmountSpent(), DELTA);
    }

    @Test    //13
    public void addingFourItemsTotalAmountSpent() throws Exception
    {
        testbudget.addItem(testItem1);
        testbudget.addItem(testItem2);
        testbudget.addItem(testItem3);
        testbudget.addItem(testItem4);
        assertEquals(66.24, testbudget.getTotalAmountSpent(), DELTA);
    }

    @Test    //14
    public void addingFiveItemsTotalAmountSpent() throws Exception
    {
        testbudget.addItem(testItem1);
        testbudget.addItem(testItem2);
        testbudget.addItem(testItem3);
        testbudget.addItem(testItem4);
        testbudget.addItem(testItem5);
        assertEquals(1516.24, testbudget.getTotalAmountSpent(), DELTA);
    }

    @Test    //15
    public void addingSixItemsTotalAmountSpent() throws Exception
    {
        testbudget.addItem(testItem1);
        testbudget.addItem(testItem2);
        testbudget.addItem(testItem3);
        testbudget.addItem(testItem4);
        testbudget.addItem(testItem5);
        testbudget.addItem(testItem6);
        assertEquals(1519.24, testbudget.getTotalAmountSpent(), DELTA);
    }

    @Test    //16
    public void addingSevenItemsTotalAmountSpent() throws Exception
    {
        testbudget.addItem(testItem1);
        testbudget.addItem(testItem2);
        testbudget.addItem(testItem3);
        testbudget.addItem(testItem4);
        testbudget.addItem(testItem5);
        testbudget.addItem(testItem6);
        testbudget.addItem(testItem7);
        assertEquals(1550.42, testbudget.getTotalAmountSpent(), DELTA);
    }

    @Test    //17
    public void addingEightItemsTotalAmountSpent() throws Exception
    {
        testbudget.addItem(testItem1);
        testbudget.addItem(testItem2);
        testbudget.addItem(testItem3);
        testbudget.addItem(testItem4);
        testbudget.addItem(testItem5);
        testbudget.addItem(testItem6);
        testbudget.addItem(testItem7);
        testbudget.addItem(testItem8);
        assertEquals(1637.42, testbudget.getTotalAmountSpent(), DELTA);
    }

    @Test    //18
    public void addingNineItemsTotalAmountSpent() throws Exception
    {
        testbudget.addItem(testItem1);
        testbudget.addItem(testItem2);
        testbudget.addItem(testItem3);
        testbudget.addItem(testItem4);
        testbudget.addItem(testItem5);
        testbudget.addItem(testItem6);
        testbudget.addItem(testItem7);
        testbudget.addItem(testItem8);
        testbudget.addItem(testItem9);
        assertEquals(1639.42, testbudget.getTotalAmountSpent(), DELTA);
    }

    @Test    //19
    public void addingTenItemsTotalAmountSpent() throws Exception
    {
        addTenItems();
        assertEquals(1789.42, testbudget.getTotalAmountSpent(), DELTA);
    }

    //-------------------------------
    //Testing Cost of All Categories
    //-------------------------------

    @Test    //20
    public void testingCostOfAllCategories1() throws Exception
    {
        addTenItems();
        assertEquals(163.25, testbudget.getCostOfAllCategories().get("Entertainment"), DELTA);
    }

    @Test    //21
    public void testingCostOfAllCategories2() throws Exception
    {
        addTenItems();
        assertEquals(7.99, testbudget.getCostOfAllCategories().get("Food"), DELTA);
    }

    @Test    //22
    public void testingCostOfAllCategories3() throws Exception
    {
        addTenItems();
        assertEquals(48.00, testbudget.getCostOfAllCategories().get("Personal"), DELTA);
    }

    @Test    //23
    public void testingCostOfAllCategories4() throws Exception
    {
        addTenItems();
        assertEquals(1450.00, testbudget.getCostOfAllCategories().get("Rent"), DELTA);
    }


    @Test    //24
    public void testingCostOfAllCategories5() throws Exception
    {
        addTenItems();
        assertEquals(31.18, testbudget.getCostOfAllCategories().get("Gas"), DELTA);
    }

    @Test    //25
    public void testingCostOfAllCategories6() throws Exception
    {
        addTenItems();
        assertEquals(89.00, testbudget.getCostOfAllCategories().get("School"), DELTA);
    }

    //----------------------
    //Testing of Net Income
    //----------------------


    @Test    //26
    public void testingNetIncome1() throws Exception
    {
        addTenItems();
        testbudget.addMoneyToIncome(2115.89);
        assertEquals(326.47, testbudget.getNetIncome(), DELTA);
    }

    @Test    //27
    public void testingNetIncome2() throws Exception
    {
        addTenItems();
        testbudget.addMoneyToIncome(3284.73);
        assertEquals(1495.31, testbudget.getNetIncome(), DELTA);
    }

    @Test    //28
    public void testingNetIncome3() throws Exception
    {
        addTenItems();
        testbudget.addMoneyToIncome(383.22);
        assertEquals(-1406.20, testbudget.getNetIncome(), DELTA);
    }

    @Test    //29
    public void testingNetIncome4() throws Exception
    {
        addTenItems();
        testbudget.addMoneyToIncome(154.89);
        assertEquals(-1634.53, testbudget.getNetIncome(), DELTA);
    }

    @Test    //30
    public void testingNetIncome5() throws Exception
    {
        addTenItems();
        testbudget.addMoneyToIncome(1619.83);
        assertEquals(-169.59, testbudget.getNetIncome(), DELTA);
    }

    @Test    //31
    public void testingNetIncome6() throws Exception
    {
        addTenItems();
        testbudget.addMoneyToIncome(1789.42);
        assertEquals(0.00, testbudget.getNetIncome(), DELTA);
    }

    //---------------------------
    //Testing Removal of Items
    //---------------------------

    @Test    //32
    public void removingItem1() throws Exception
    {
        addTenItems();
        testbudget.removeItem(1);    //Removes the Movie Tickets
        assertEquals(1776.17, testbudget.getTotalAmountSpent(), DELTA);

    }

    @Test    //33
    public void removingItem2() throws Exception
    {
        addTenItems();
        testbudget.removeItem(7);    //Removes the Books
        assertEquals(1702.42, testbudget.getTotalAmountSpent(), DELTA);

    }

    @Test    //34
    public void removingItem3() throws Exception
    {
        addTenItems();
        testbudget.removeItem(6);    //Removes the Gas
        assertEquals(1758.24, testbudget.getTotalAmountSpent(), DELTA);

    }

    @Test    //35
    public void removingItem4() throws Exception
    {
        addTenItems();
        testbudget.removeItem(3);    //Removes the Pizza
        assertEquals(1784.42, testbudget.getTotalAmountSpent(), DELTA);

    }

    @Test    //36
    public void removingItem5() throws Exception
    {
        addTenItems();
        testbudget.removeItem(2);    //Removes the Shoes
        assertEquals(1744.42, testbudget.getTotalAmountSpent(), DELTA);
    }





    //This function just adds all ten items to the test budget
    private void addTenItems()
    {
        testbudget.addItem(testItem1);
        testbudget.addItem(testItem2);
        testbudget.addItem(testItem3);
        testbudget.addItem(testItem4);
        testbudget.addItem(testItem5);
        testbudget.addItem(testItem6);
        testbudget.addItem(testItem7);
        testbudget.addItem(testItem8);
        testbudget.addItem(testItem9);
        testbudget.addItem(testItem10);
    }

}
