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

package vincent.moulin.vocab.constants;

import vincent.moulin.vocab.utilities.ConstantsHashMap;

/**
 * The Constants class defines all the constants of the application.
 * 
 * @author Vincent MOULIN
 */
public final class Constants
{
    // Constants for the database
    // LANGUAGES
    public static final ConstantsHashMap LANGUAGES = new ConstantsHashMap();
    static {
        LANGUAGES.put("english",  0);
        LANGUAGES.put("french",   1);
    }
    //END: LANGUAGES
    
    // STATUSES
    public static final ConstantsHashMap STATUSES = new ConstantsHashMap();
    static {
        STATUSES.put("initial",   0);
        STATUSES.put("learning",  1);
        STATUSES.put("known",     2);
    }
    //END: STATUSES
    
    // FREQUENCIES
    public static final ConstantsHashMap FREQUENCIES = new ConstantsHashMap();
    static {
        FREQUENCIES.put("daily",    0);
        FREQUENCIES.put("weekly",   1);
        FREQUENCIES.put("monthly",  2);
    }
    //END: FREQUENCIES
    //END: Constants for the database
    
    // Constants for the application
    public static final int MAX_PRIMARY_INDICE = 10;
    public static final int MAX_SECONDARY_INDICE = 10;
    public static final int MAX_COMBINED_INDICE = 20;
    public static final int MAX_COMBINED_INDICE_ELIGIBLE_WORD = 10;
    
    public static final int TIMESTAMP_RAW_VALUE = 0;
    public static final int TIMESTAMP_OFFSETTED_VALUE = 1;
    
    public static final int DAYSTAMP = 0;
    public static final int WEEKSTAMP = 1;
    public static final int MONTHSTAMP = 2;
    //END: Constants for the application
}
