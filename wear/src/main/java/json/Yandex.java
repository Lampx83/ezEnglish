package json;

/**
 * Created by xuanlam on 10/28/15.
 */
public class Yandex {
    private String[] text;

    private String code;

    private String lang;

    public String[] getText ()
    {
        return text;
    }

    public void setText (String[] text)
    {
        this.text = text;
    }

    public String getCode ()
    {
        return code;
    }

    public void setCode (String code)
    {
        this.code = code;
    }

    public String getLang ()
    {
        return lang;
    }

    public void setLang (String lang)
    {
        this.lang = lang;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [text = "+text+", code = "+code+", lang = "+lang+"]";
    }
}
