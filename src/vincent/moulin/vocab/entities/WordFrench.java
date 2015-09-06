/**
 * Copyright 2013, 2015 Vincent MOULIN
 * 
 * This file is part of Doctor Vocab.
 * 
 * Doctor Vocab is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package vincent.moulin.vocab.entities;

import vincent.moulin.vocab.constants.ConstantsHM;

/**
 * The WordFrench class represents a French word.
 * 
 * @author Vincent MOULIN
 */
public class WordFrench extends Word
{
    public WordFrench(
        String content,
        boolean isActive,
        Status status,
        boolean isAccelerated,
        int primaryIndice,
        int secondaryIndice,
        long timestampLastAnswer
    ) {
        super(
            content,
            isActive,
            status,
            isAccelerated,
            primaryIndice,
            secondaryIndice,
            timestampLastAnswer
        );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean belongsToPack() {
        return super.belongsToPackWithIdLang(ConstantsHM.LANGUAGES.getId("french"));
    }
    
    /**
     * {@inheritDoc}
     */
    public Pack retrievePack() {
        return super.retrievePackWithIdLang(ConstantsHM.LANGUAGES.getId("french"));
    }
}
