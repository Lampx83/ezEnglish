package entity;

import flashcard.Image;

public class Term extends MItem {
    private String definition;
    private Image image;
    private String term;
    public long translateID = -1;

    public String getDefinition() {
        return this.definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getTerm() {
        return this.term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

}
