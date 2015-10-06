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

package vincent.moulin.vocab.utilities;

import java.util.Calendar;

import vincent.moulin.vocab.constants.Constants;

/**
 * The TimestampNow class is a singleton that represents the current timestamp
 * with the following characteristics :
 *     - value : the UNIX timestamp
 *     - zoneOffset : the raw offset from GMT
 *     - dstOffset : the daylight saving offset
 * 
 * @author Vincent MOULIN
 */
public final class TimestampNow
{
    private static TimestampNow instance = null;
    
    private long value;
    private int zoneOffset;
    private int dstOffset;
    
    private TimestampNow() {
        reinitialize();
    }
    
    public static TimestampNow getInstance() {
        if (instance == null) {
            instance = new TimestampNow();
        }
        
        return instance;
    }
    
    /**
     * Get the value of the timestamp represented by the singleton, depending on the given "timestampValueType".
     * @param timestampValueType the type of the value of the timestamp
     * @return the value of the timestamp represented by the singleton, depending on the given "timestampValueType"
     */
    public long getValue(int timestampValueType) {
        long retour = 0;
        
        switch (timestampValueType) {
            case Constants.TIMESTAMP_RAW_VALUE:
                retour = this.value;
                break;

            case Constants.TIMESTAMP_OFFSETTED_VALUE:
                retour = this.value + this.zoneOffset + this.dstOffset;
                break;

            default:
                break;
        }
        
        return retour;
    }
    
    /**
     * Reinitialize the value of the timestamp represented by the singleton.
     */
    public void reinitialize() {
        Calendar calendar = Calendar.getInstance();
        
        this.value       = calendar.getTimeInMillis() / 1000;
        this.zoneOffset  = calendar.get(Calendar.ZONE_OFFSET) / 1000;
        this.dstOffset   = calendar.get(Calendar.DST_OFFSET) / 1000;
    }
}
