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

package vincent.moulin.vocab.libraries.enumdata;

/**
 * The EnumDataItem class represents an item of a set of enumerated data.
 * 
 * @author Vincent MOULIN
 */
public abstract class EnumDataItem
{
    protected static EnumDataLoader enumDataLoader = EnumDataLoader.getInstance();
    
    protected static String[] fields = {"id", "name"};
    
    private int id;
    private String name;
    
    public EnumDataItem(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
}
