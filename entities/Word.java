package vincent.moulin.vocab.entities;

import vincent.moulin.vocab.Constantes;

public abstract class Word {
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
    
    /**
     * Test the eligibility of the considered word.
     * @param indiceSecondary the secondary indice of the considered word
     * @param timestampDiff the number of elapsed seconds since the last time the considered word has been studied
     * @return true if the considered word in learning is eligible and false otherwise
     */
    public static boolean wordInLearningIsEligible (int indiceSecondary, long timestampDiff) {
        int [] levels = {
                   5,
                  20,
                  60,
                4*60,
               15*60,
               60*60,
             4*60*60,
            12*60*60,
            24*60*60,
            48*60*60
        };
        
        if (timestampDiff > levels[indiceSecondary]) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Calculate the combined indice of the considered word.
     * @param indicePrimary the primary indice of the considered word
     * @param timestampDiff the number of elapsed seconds since the last time the considered word has been studied
     * @return the combined indice of the considered word
     */
    public static int calcCombinedIndice(int indicePrimary, long timestampDiff) {
        int combinedIndice;
        int [] levels = {
              4*24*60*60,
              7*24*60*60,
             10*24*60*60,
             14*24*60*60,
             21*24*60*60,
             30*24*60*60,
             45*24*60*60,
             60*24*60*60,
             90*24*60*60,
            180*24*60*60
        };
        
        combinedIndice = Constantes.MAX_INDICE_PRIMARY + 1 - indicePrimary;
        
        for (int i = 0; i < levels.length; i++) {
            if (timestampDiff < levels[i]) {
                return combinedIndice + i;
            }
        }
        
        return combinedIndice + 10;
    }

}
