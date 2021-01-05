package entity;

public class MItem { // Question [Set - Flashcard] Article News

    protected String ItemID;
    protected String ItemName;
    protected String ItemDescription;
    protected String PackID;
    public int mark; // Mark=1 => Bookmark , Mark=2 => Master of it, Mark=3 => It isnot useful
    //Con dung trong car BaseCardListActivity (dung nho)
    public int box = -1; // -1 No Answers yet, 0 Often miss, 1 Sometimes miss, 2 Seldom miss, 3 Never miss
    public long bookmarkTime;

    public int type = -1; // -1 Refresh and Cutomize buttons
    // 0 Question
    // 1 Flashcard Set
    // 2 Article
    // 3 News
    // 4 Flashcard card
    // 5 Notes

    // 6 TAM QUESTION
    // 7 TAM Feedback FORM
    // 8 TAM SUBMIT

    // 10 HighScore
    // 11 Sta
    // 12 Article
    // 13 News

    // protected String Lastread;
    protected int TimesRead;

    public MItem() {
        super();
        // TODO Auto-generated constructor stub
    }

    public MItem(String itemID, String itemName, String packID) {
        super();
        ItemID = itemID;
        ItemName = itemName;
        PackID = packID;
    }

    public String getPackID() {
        return PackID;
    }

    public void setPackID(String packID) {
        PackID = packID;
    }

    public MItem(String itemID, String itemName, int mark) {
        super();
        ItemID = itemID;
        ItemName = itemName;
        this.mark = mark;
    }

    public String getItemDescription() {
        return ItemDescription;
    }

    public void setItemDescription(String itemDescription) {
        ItemDescription = itemDescription;
    }

    public String getItemName() {
        if (ItemName == null)
            return "";
        else
            return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public String getItemID() {
        return ItemID;
    }

    public void setItemID(String itemID) {
        ItemID = itemID;
    }

    // public String getLastread() {
    // return Lastread;
    // }
    //
    // public void setLastread(String lastread) {
    // Lastread = lastread;
    // }

    public int getTimesRead() {
        return TimesRead;
    }

    public void setTimesRead(int timesRead) {
        TimesRead = timesRead;
    }

}
