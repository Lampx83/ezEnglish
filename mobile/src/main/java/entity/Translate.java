package entity;

/**
 * Created by xuanlam on 10/29/15.
 */
public class Translate {


    public String image;
    public long translateid=0;
    public String langfrom;
    public String langto;
    public String text;
    public String result;
    public int device; //0 watch, //1 speak from phone //2 type from phone //3 cloud vision  //4 Syntax




    public String server;
    public long time;
    public String date;
    public int tts = 0;
    public int score = -1; //-2 turn off //-3 Wrong
    public int practice_times = 0;
    public long step = 0;
    public long stepon = 0;

    public double longitude = 0;
    public double latitude = 0;

    public int mark = 0;
    public String learn_context;

    public String user_id;

    public String user_name;
    public String note;

    public int add=0;
    public int delete=0;
    public int edit=0;
}
