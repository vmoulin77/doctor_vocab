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
 * The ConstantsHM class contains all the HashMaps of constants.
 * Each HashMap corresponds to a database table containing an enumeration of constants.
 * 
 * @author Vincent MOULIN
 */
public final class ConstantsHM {
    // HashMap of constants corresponding to the "language" table of the database
    public static final ConstantsHashMap LANGUAGES = new ConstantsHashMap();
    static {
        LANGUAGES.put("english",  0);
        LANGUAGES.put("french",   1);
    }
    //--------------------------------------------------------------------
    
    // HashMap of constants corresponding to the "status" table of the database
    public static final ConstantsHashMap STATUSES = new ConstantsHashMap();
    static {
        STATUSES.put("initial",   0);
        STATUSES.put("learning",  1);
        STATUSES.put("known",     2);
    }
    //--------------------------------------------------------------------
    
    // HashMap of constants corresponding to the "frequency" table of the database
    public static final ConstantsHashMap FREQUENCIES = new ConstantsHashMap();
    static {
        FREQUENCIES.put("daily",    0);
        FREQUENCIES.put("weekly",   1);
        FREQUENCIES.put("monthly",  2);
    }
    //--------------------------------------------------------------------
}
