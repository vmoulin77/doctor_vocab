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
 * The Status class represents a word status.
 * 
 * @author Vincent MOULIN
 */
public final class Status extends EnumDataItem implements Cloneable
{
    private static String tableName = "status";
    private static String[] fields = {"id", "name", "color"};
    
    private String color;
    
    public Status(int id, String name, String color) {
        super(id, name);
        this.color = color;
    }

    public String getColor() {
        return this.color;
    }
    
    public Status clone() throws CloneNotSupportedException {
        return (Status) super.clone();
    }
    
    public static Status find(int id) {
        return (Status) enumDataLoader.getById(tableName, fields, id);
    }
    
    public static Status findByName(String name) {
        return (Status) enumDataLoader.getByName(tableName, fields, name);
    }
    
    public static Status[] findAll() {
        EnumDataItem[] enumDataItems = enumDataLoader.getAll(tableName, fields);
        return Arrays.copyOf(enumDataItems, enumDataItems.length, Status[].class);
    }

    public static int findId(String name) {
        return findByName(name).getId();
    }
    
    public static String findName(int id) {
        return find(id).getName();
    }
    
    public static String findColor(int id) {
        return find(id).getColor();
    }
    
    public static String findColorByName(String name) {
        return findByName(name).getColor();
    }
}
