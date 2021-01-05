package json;

public class ResponseData
{
    private String match;

    private String translatedText;

    public String getMatch ()
    {
        return match;
    }

    public void setMatch (String match)
    {
        this.match = match;
    }

    public String getTranslatedText ()
    {
        return translatedText;
    }

    public void setTranslatedText (String translatedText)
    {
        this.translatedText = translatedText;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [match = "+match+", translatedText = "+translatedText+"]";
    }
}
