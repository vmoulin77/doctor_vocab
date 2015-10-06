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
    public Language getLanguage() {
        return Language.getByName("french");
    }
}
