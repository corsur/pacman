/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edistance;

import java.io.File;
import java.io.IOException;

/**
 * Takes in a directory of sorting data and a modal naming file as arguments
 * and prints all editDistances from each subject to the modal naming
 * @author andromeda
 */
public class PrintEditDistances {
    
    protected static String[] funct(String directory1, String modal) throws Exception {
        File dir1 = new File(directory1);
        String[] children1 = dir1.list();
        String[] results = new String[children1.length];
        
        for(int i = 0; i<children1.length; i++) {
            String file1 = directory1.concat("\\");
            file1 = file1.concat(children1[i]);
            Integer temp = new Integer(new EditDistance(file1,modal, "0").calcEditDist());
            results[i] = file1+": "+temp.toString();
        }
        return results;
    }
    
    public static void main(String args[]) throws IOException, Exception {
        String directory1 = args[0];
        String modal = args[1];
        String[] results = funct(directory1,modal);
        for (String result:results) {
            System.out.println(result);
        }
    }
}
