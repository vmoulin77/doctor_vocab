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
        return color;
    }
    
    public Status clone() throws CloneNotSupportedException {
        return (Status) super.clone();
    }
    
    public static Status getById(int id) {
        return (Status) enumDataLoader.getById(tableName, fields, id);
    }
    
    public static Status getByName(String name) {
        return (Status) enumDataLoader.getByName(tableName, fields, name);
    }
    
    public static Status[] all() {
        return (Status[]) enumDataLoader.getAll(tableName, fields);
    }

    public static int getIdOf(String name) {
        return getByName(name).getId();
    }
    
    public static String getNameOf(int id) {
        return getById(id).getName();
    }
    
    public static String getColorOf(String name) {
        return getByName(name).getColor();
    }
    
    public static String getColorOf(int id) {
        return getById(id).getColor();
    }
}
