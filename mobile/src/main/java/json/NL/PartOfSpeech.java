package json.NL;

import com.google.gson.annotations.SerializedName;

public class PartOfSpeech {
    public String person;

    public String aspect;

    public String voice;

    public String form;

    public String tag;

    public String tense;

    public String mood;

    public String gender;

    public String number;

    public String proper;

    @SerializedName("case")
    public String caseText;

    public String reciprocity;
}
