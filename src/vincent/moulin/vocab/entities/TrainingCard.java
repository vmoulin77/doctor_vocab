/**
 * Copyright (c) 2013-2016 Vincent MOULIN
 * 
 * This file is part of Doctor Vocab.
 * 
 * Doctor Vocab is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package vincent.moulin.vocab.entities;

import vincent.moulin.vocab.utilities.Now;

/**
 * The Card class represents a word.
 * One side of the card represents the English form and the other side represents the French form.
 * Some additional information attached to the card is mainly used in order to implement the Spaced Repetition System.
 * 
 * @author Vincent MOULIN
 */
public class TrainingCard extends Card
{
    private Side questionSide;
    private Side solutionSide;
    private boolean questionWordIsEligible;
    
    public TrainingCard(int id, EnglishSide englishSide, FrenchSide frenchSide, String startingLangName, boolean questionWordIsEligible) {
        super(id, englishSide, frenchSide);
        
        if (startingLangName.equals("english")) {
            this.questionSide = englishSide;
            this.solutionSide = frenchSide;
        } else {
            this.questionSide = frenchSide;
            this.solutionSide = englishSide;
        }
        
        this.questionWordIsEligible = questionWordIsEligible;
    }
    
    public Side getQuestionSide() {
        return this.questionSide;
    }
    public Side getSolutionSide() {
        return this.solutionSide;
    }
    public void setTrainingSides(String startingLangName) {
        if (startingLangName.equals("english")) {
            this.questionSide = this.englishSide;
            this.solutionSide = this.frenchSide;
        } else {
            this.questionSide = this.frenchSide;
            this.solutionSide = this.englishSide;
        }
    }
    
    public boolean getQuestionWordIsEligible() {
        return this.questionWordIsEligible;
    }
    public void setQuestionWordIsEligible(boolean questionWordIsEligible) {
        this.questionWordIsEligible = questionWordIsEligible;
    }
    
    public void setEnglishSide(EnglishSide englishSide) {
        if (this.englishSide == this.questionSide) {
            this.questionSide = englishSide;
        } else {
            this.solutionSide = englishSide;
        }
        
        super.setEnglishSide(englishSide);
    }
    
    public void setFrenchSide(FrenchSide frenchSide) {
        if (this.frenchSide == this.questionSide) {
            this.questionSide = frenchSide;
        } else {
            this.solutionSide = frenchSide;
        }
        
        super.setFrenchSide(frenchSide);
    }
    
    public TrainingCard clone() throws CloneNotSupportedException {
        TrainingCard retour = (TrainingCard) super.clone();

        if (this.questionSide == this.englishSide) {
            retour.questionSide = retour.englishSide;
            retour.solutionSide = retour.frenchSide;
        } else {
            retour.questionSide = retour.frenchSide;
            retour.solutionSide = retour.englishSide;
        }
        
        return retour;
    }
    
    public static TrainingCard find(int id, String startingLangName, boolean questionWordIsEligible) {
        Card card = Card.find(id);
        
        return new TrainingCard(card.id, card.englishSide, card.frenchSide, startingLangName, questionWordIsEligible);
    }
    
    /**
     * Manage the answer of the user.
     * Update the current TrainingCard object and the database.
     * @param startingLangName the starting language name
     * @param answerIsOk true if the answer is ok and false otherwise
     */
    public void manageAnswer(String startingLangName, boolean answerIsOk) {
        Pack linkedPack;
        long rawTimestampNow = Now.getInstance().getRawTimestamp();
        
        // Calculation of the new status, the new primary indice and the new secondary indice
        if (this.questionSide.getStatus().getName().equals("initial")) {
            if (answerIsOk) {
                this.questionSide.setStatus("known");
                this.questionSide.setPrimaryIndice(3);
            } else {
                this.questionSide.setStatus("learning");
                this.questionSide.setPrimaryIndice(1);
            }
            this.questionSide.setSecondaryIndice(1);
        } else if (this.questionSide.getStatus().getName().equals("known")) {
            if (answerIsOk) {
                if ((this.questionSide.getPrimaryIndice() != Side.MAX_PRIMARY_INDICE)
                    && this.questionWordIsEligible
                ) {
                    if (this.questionSide.getIsAccelerated()) {
                        this.questionSide.setPrimaryIndice(Side.MAX_PRIMARY_INDICE);
                    } else {
                        this.questionSide.setPrimaryIndice(this.questionSide.getPrimaryIndice() + 1);
                    }
                }
            } else {
                this.questionSide.setStatus("learning");
                if (this.questionSide.getPrimaryIndice() <= 3) {
                    this.questionSide.setPrimaryIndice(1);
                } else if (this.questionSide.getPrimaryIndice() <= 6) {
                    this.questionSide.setPrimaryIndice(2);
                } else {
                    this.questionSide.setPrimaryIndice(3);
                }
                this.questionSide.setSecondaryIndice(1);
            }
        } else {
            if (answerIsOk) {
                if (this.questionSide.getSecondaryIndice() == Side.MAX_SECONDARY_INDICE) {
                    this.questionSide.setStatus("known");
                    this.questionSide.setSecondaryIndice(1);
                } else {
                    this.questionSide.setSecondaryIndice(this.questionSide.getSecondaryIndice() + 1);
                }
            } else {
                this.questionSide.setSecondaryIndice(1);
            }
        }
        //--------------------------------------------------------------------
        
        // We set if the "questionSide" is accelerated or not
        if ( ! answerIsOk) {
            this.questionSide.setIsAccelerated(false);
        }
        //--------------------------------------------------------------------
        
        // Calculation of the new "timestampLastAnswer" and management of the Pack
        if (answerIsOk && this.questionSide.getStatus().getName().equals("learning")) {
            linkedPack = this.questionSide.retrievePack();
            
            if (this.questionSide.belongsToPack()) {
                this.questionSide.setTimestampLastAnswer(linkedPack.getTimestampPack());
            } else {
                this.questionSide.setTimestampLastAnswer(rawTimestampNow);
                linkedPack.setTimestampPack(rawTimestampNow);
            }
            linkedPack.setTimestampLastAnswer(rawTimestampNow);
            
            linkedPack.save();
        } else {
            this.questionSide.setTimestampLastAnswer(rawTimestampNow);
        }
        //--------------------------------------------------------------------

        this.save();
    }
}
