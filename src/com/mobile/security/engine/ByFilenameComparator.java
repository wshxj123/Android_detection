package com.mobile.security.engine;

import java.util.Comparator; 
import java.io.File;

public class ByFilenameComparator implements Comparator {  
    public final int compare(Object pFirst, Object pSecond) {  
        String aFirstName = ((File)pFirst).getName().toLowerCase();  
        String aSecondName =((File)pSecond).getName().toLowerCase(); 
        int diff = aFirstName.compareTo( aSecondName);  
        if (diff > 0)  
            return 1;  
        if (diff < 0)  
            return -1;  
        else  
            return 0;  
    }  
}