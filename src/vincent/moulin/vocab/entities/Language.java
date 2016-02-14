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

package vincent.moulin.vocab.entities;

import vincent.moulin.vocab.libraries.enumdata.EnumDataItem;

/**
 * The Language class represents a language.
 * 
 * @author Vincent MOULIN
 */
public final class Language extends EnumDataItem
{
    private static String tableName = "language";
    
    public Language(int id, String name) {
        super(id, name);
    }
    
    public static Language getById(int id) {
        return (Language) enumDataLoader.getById(tableName, fields, id);
    }
    
    public static Language getByName(String name) {
        return (Language) enumDataLoader.getByName(tableName, fields, name);
    }
    
    public static Language[] all() {
        return (Language[]) enumDataLoader.getAll(tableName, fields);
    }

    public static int getIdOf(String name) {
        return getByName(name).getId();
    }
    
    public static String getNameOf(int id) {
        return getById(id).getName();
    }
}
