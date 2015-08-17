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

import java.util.Set;
import java.util.Map;
import java.util.HashMap;

/**
 * The ConstantsHashMap class is used to define a HashMap of constants.
 * 
 * @author Vincent MOULIN
 */
public final class ConstantsHashMap
{
    private HashMap<String, Integer> constants = new HashMap<String, Integer>();
    
    public void put(String name, Integer id) {
        this.constants.put(name, id);
    }
    
    public Set<Map.Entry<String, Integer>> entrySet() {
        return this.constants.entrySet();
    }
    
    /**
     * Get the id corresponding to the given name in the constants HashMap.
     * @param name the name of the constant
     * @return the id corresponding to the given name in the constants HashMap
     */
    public int getId(String name) {
        return this.constants.get(name);
    }
    
    /**
     * Get the name corresponding to the given id in the constants HashMap.
     * @param id the id of the constant
     * @return the name corresponding to the given id in the constants HashMap or null if the id is not found
     */
    public String getName(int id) {
        for (Map.Entry<String, Integer> entry : this.constants.entrySet()) {
            if (entry.getValue() == id) {
                return entry.getKey();
            }
        }
        
        return null;
    }
}
