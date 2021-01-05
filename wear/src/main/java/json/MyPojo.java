package json;

/**
 * Created by xuanlam on 10/28/15.
 */
public class MyPojo {
    private ResponseData responseData;

    private Matches[] matches;

    private String responseDetails;

    private String responseStatus;

    private String responderId;

    public ResponseData getResponseData ()
    {
        return responseData;
    }

    public void setResponseData (ResponseData responseData)
    {
        this.responseData = responseData;
    }

    public Matches[] getMatches ()
    {
        return matches;
    }

    public void setMatches (Matches[] matches)
    {
        this.matches = matches;
    }

    public String getResponseDetails ()
    {
        return responseDetails;
    }

    public void setResponseDetails (String responseDetails)
    {
        this.responseDetails = responseDetails;
    }

    public String getResponseStatus ()
    {
        return responseStatus;
    }

    public void setResponseStatus (String responseStatus)
    {
        this.responseStatus = responseStatus;
    }

    public String getResponderId ()
    {
        return responderId;
    }

    public void setResponderId (String responderId)
    {
        this.responderId = responderId;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [responseData = "+responseData+", matches = "+matches+", responseDetails = "+responseDetails+", responseStatus = "+responseStatus+", responderId = "+responderId+"]";
    }
}
