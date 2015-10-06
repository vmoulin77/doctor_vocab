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

import vincent.moulin.vocab.libraries.enumdata.EnumDataItem;

/**
 * The Frequency class represents a frequency.
 * 
 * @author Vincent MOULIN
 */
public final class Frequency extends EnumDataItem
{
    private static String tableName = "frequency";
    
    public Frequency(int id, String name) {
        super(id, name);
    }
    
    public static Frequency getById(int id) {
        return (Frequency) enumDataLoader.getById(tableName, fields, id);
    }
    
    public static Frequency getByName(String name) {
        return (Frequency) enumDataLoader.getByName(tableName, fields, name);
    }
    
    public static Frequency[] all() {
        return (Frequency[]) enumDataLoader.getAll(tableName, fields);
    }

    public static int getIdOf(String name) {
        return getByName(name).getId();
    }
    
    public static String getNameOf(int id) {
        return getById(id).getName();
    }
}
