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

package vincent.moulin.vocab;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application
{
    private static MyApplication instance;
    
    public static MyApplication getInstance() {
        return instance;
    }
    
    public static Context getContext() {
        return instance;
    }
    
    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}
