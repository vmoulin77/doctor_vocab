/**
 * Copyright 2013, 2016 Vincent MOULIN
 * 
 * This file is part of Doctor Vocab.
 * 
 * Doctor Vocab is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package vincent.moulin.vocab.helpers;

import java.util.Calendar;

import vincent.moulin.vocab.constants.Constants;

/**
 * The TimeHelper class
 * 
 * @author Vincent MOULIN
 */
public final class TimeHelper
{
    /**
     * Convert the given timestamp in the given type of stamp.
     * @param timestamp the timestamp to convert
     * @param stampType the type of stamp in which the timestamp must be converted
     * @return the converted timestamp
     */
    public static long convertTimestamp(long timestamp, int stampType) {
        long retour = 0;
        
        switch (stampType) {
            case Constants.DAYSTAMP:
                retour = timestamp / (24*60*60);
                break;
                
            case Constants.WEEKSTAMP:
                retour = (timestamp + 3*24*60*60) / (7*24*60*60);
                break;
                
            case Constants.MONTHSTAMP:
                Calendar calendarNow = Calendar.getInstance();
                
                calendarNow.setTimeInMillis(timestamp * 1000);
                retour = (12 * (calendarNow.get(Calendar.YEAR) - 1970)) + calendarNow.get(Calendar.MONTH);
                break;
                
            default:
                break;
        }
        
        return retour;
    }
}
