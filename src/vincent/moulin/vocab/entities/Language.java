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

package vincent.moulin.vocab.entities;

import java.util.Arrays;

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
    
    public static Language find(int id) {
        return (Language) enumDataLoader.getById(tableName, fields, id);
    }
    
    public static Language findByName(String name) {
        return (Language) enumDataLoader.getByName(tableName, fields, name);
    }
    
    public static Language[] findAll() {
        EnumDataItem[] enumDataItems = enumDataLoader.getAll(tableName, fields);
        return Arrays.copyOf(enumDataItems, enumDataItems.length, Language[].class);
    }

    public static int findId(String name) {
        return findByName(name).getId();
    }
    
    public static String findName(int id) {
        return find(id).getName();
    }
}
