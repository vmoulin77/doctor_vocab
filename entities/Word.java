package vincent.moulin.vocab.entities;

public class Word {
    private String content;
    private int mode;
    private boolean isAccelerated;
    private int indicePrimary;
    private int indiceSecondary;
    private int timestampLastAnswer;
    
    public Word(String content, int mode, boolean isAccelerated,
            int indicePrimary, int indiceSecondary,
            int timestampLastAnswer) {
        this.content = content;
        this.mode = mode;
        this.isAccelerated = isAccelerated;
        this.indicePrimary = indicePrimary;
        this.indiceSecondary = indiceSecondary;
        this.timestampLastAnswer = timestampLastAnswer;
    }
    
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public int getMode() {
        return mode;
    }
    public void setMode(int mode) {
        this.mode = mode;
    }
    public boolean isAccelerated() {
        return isAccelerated;
    }
    public void setAccelerated(boolean isAccelerated) {
        this.isAccelerated = isAccelerated;
    }
    public int getIndicePrimary() {
        return indicePrimary;
    }
    public void setIndicePrimary(int indicePrimary) {
        this.indicePrimary = indicePrimary;
    }
    public int getIndiceSecondary() {
        return indiceSecondary;
    }
    public void setIndiceSecondary(int indiceSecondary) {
        this.indiceSecondary = indiceSecondary;
    }
    public int getTimestampLastAnswer() {
        return timestampLastAnswer;
    }
    public void setTimestampLastAnswer(int timestampLastAnswer) {
        this.timestampLastAnswer = timestampLastAnswer;
    }

}
