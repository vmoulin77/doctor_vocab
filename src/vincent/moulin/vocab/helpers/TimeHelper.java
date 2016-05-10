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

package vincent.moulin.vocab.helpers;

import java.util.Calendar;

/**
 * The TimeHelper class
 * 
 * @author Vincent MOULIN
 */
public final class TimeHelper
{
    public static final int TIMESTAMP_RAW = 0;
    public static final int TIMESTAMP_OFFSETTED = 1;
    
    /**
     * Convert the given calendar into a timestamp whose type is timestampType.
     * @param calendar the calendar
     * @param timestampType the type of timestamp (accepted values are: TIMESTAMP_RAW, TIMESTAMP_OFFSETTED)
     * @return the timestamp
     */
    public static long calendarToTimestamp(Calendar calendar, int timestampType) {
        long retour = 0;

        switch (timestampType) {
            case TIMESTAMP_RAW:
                retour = calendar.getTimeInMillis() / 1000;
                break;
                
            case TIMESTAMP_OFFSETTED:
                retour = (calendar.getTimeInMillis() + calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET)) / 1000;
                break;
                
            default:
                break;
        }
        
        return retour;
    }
    
    /**
     * Convert the given calendar into a daystamp.
     * @param calendar the calendar
     * @return the daystamp corresponding to the given calendar
     */
    public static long calendarToDaystamp(Calendar calendar) {
        return calendarToTimestamp(calendar, TIMESTAMP_OFFSETTED) / (24*60*60);
    }
    
    /**
     * Convert the given calendar into a weekstamp.
     * @param calendar the calendar
     * @return the weekstamp corresponding to the given calendar
     */
    public static long calendarToWeekstamp(Calendar calendar) {
        return (calendarToTimestamp(calendar, TIMESTAMP_OFFSETTED) + 3*24*60*60) / (7*24*60*60);
    }
    
    /**
     * Convert the given calendar into a monthstamp.
     * @param calendar the calendar
     * @return the monthstamp corresponding to the given calendar
     */
    public static long calendarToMonthstamp(Calendar calendar) {
        return (12 * (calendar.get(Calendar.YEAR) - 1970)) + calendar.get(Calendar.MONTH);
    }
}
