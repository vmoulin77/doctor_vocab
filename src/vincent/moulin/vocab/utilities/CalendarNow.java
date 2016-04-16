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

package vincent.moulin.vocab.utilities;

import java.util.Calendar;

/**
 * The CalendarNow class is a singleton that represents the current date and time
 * 
 * @author Vincent MOULIN
 */
public final class CalendarNow
{
    private static CalendarNow instance = null;
    
    private Calendar calendar;
    
    private CalendarNow() {
        reinitialize();
    }
    
    public static CalendarNow getInstance() {
        if (instance == null) {
            instance = new CalendarNow();
        }
        
        return instance;
    }

    /**
     * Get the value of the raw timestamp equivalent to the calendar attribute.
     * @return the value of the raw timestamp equivalent to the calendar attribute
     */
    public long getRawTimestamp() {
        return this.calendar.getTimeInMillis() / 1000;
    }
    
    /**
     * Get the value of the offsetted timestamp equivalent to the calendar attribute.
     * The offsetted timestamp = the raw timestamp + the zone offset (the raw offset from GMT) + the DST offset (the daylight saving offset)
     * @return the value of the offsetted timestamp equivalent to the calendar attribute
     */
    public long getOffsettedTimestamp() {
        return (this.calendar.getTimeInMillis() + this.calendar.get(Calendar.ZONE_OFFSET) + this.calendar.get(Calendar.DST_OFFSET)) / 1000;
    }
    
    /**
     * Get the value of the daystamp equivalent to the calendar attribute.
     * @return the value of the daystamp equivalent to the calendar attribute
     */
    public long getDaystamp() {
        return this.getOffsettedTimestamp() / (24*60*60);
    }
    
    /**
     * Get the value of the weekstamp equivalent to the calendar attribute.
     * @return the value of the weekstamp equivalent to the calendar attribute
     */
    public long getWeekstamp() {
        return (this.getOffsettedTimestamp() + 3*24*60*60) / (7*24*60*60);
    }

    /**
     * Get the value of the monthstamp equivalent to the calendar attribute.
     * @return the value of the monthstamp equivalent to the calendar attribute
     */
    public long getMonthstamp() {
        return (12 * (this.calendar.get(Calendar.YEAR) - 1970)) + this.calendar.get(Calendar.MONTH);
    }
    
    /**
     * Reinitialize the value of the calendar attribute.
     */
    public void reinitialize() {
        this.calendar = Calendar.getInstance();
    }
}
