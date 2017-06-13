package edistance;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Takes in a directory of sorting data of speakers of one language
 * and two text filed containing the partition defined by the modal naming 
 * terms of two different languages. 
 * 
 * Outputs the average edit distance to the modal terms for each language and
 * the average difference score for those speakers. 
 * 
 * @author Vanessa Matalon
 */
public class LanguageCompare {
    
    public static int[][] calculateDifferenceScores(String directory1, String modalA, String modalB) {
        
        File dir1 = new File(directory1);
        String[] children1 = dir1.list();
        
        if (children1 == null) {
            System.out.println("Empty Directory for first argument");
            return null;
        } else {
            int[][] diffScores = new int[3][children1.length+2];
            for (int i=0; i<children1.length; i++) {
                try {
                    String file1 = directory1.concat("\\");
                    file1 = file1.concat(children1[i]);
                    int eDistA = new EditDistance(file1, modalA, "0").calcEditDist();
                    int eDistB = new EditDistance(file1, modalB, "0").calcEditDist();

                    diffScores[1][i] = eDistA;
                    diffScores[2][i] = eDistB;
                    
                    diffScores[0][0] += eDistA;
                    diffScores[0][1] += eDistB;
                    
                    diffScores[0][i+2] = eDistA - eDistB;
                } catch (Exception ex) {
                    Logger.getLogger(LanguageCompare.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return diffScores;
        }
    }
      
    public static void main(String args[]) {
        String directory1 = args[0], modalA = args[1],  modalB = args[2];
        
        //Speakers of Language 1
        int[][] results1 = calculateDifferenceScores(directory1, modalA, modalB);
        int numSubjects1 = results1[0].length-2;
        double avEditDist1A = results1[0][0]/(double)numSubjects1;
        System.out.println("A = "+modalA+"\nB = "+modalB);
        System.out.println("n= "+numSubjects1);
        System.out.println("Average EditDistance to A: "+avEditDist1A);
        double avEditDist1B = results1[0][1]/(double)numSubjects1;
        System.out.println("Average EditDistance to B: "+avEditDist1B);

        int temp = 0;
        for (int i=0; i<numSubjects1; i++) {
            temp += results1[0][i+2];
        }
                
        double avDiffScore1 = temp/(double)numSubjects1;
        System.out.println("\nAverage DiffScore A-B: " + avDiffScore1);
        
        if (avDiffScore1 != (avEditDist1A-avEditDist1B)) {
            System.out.println(avDiffScore1+"//"+(avEditDist1A-avEditDist1B));
        }
        
        System.out.println("\nA");
        for (int i=0; i<numSubjects1; i++) {
            System.out.println(results1[1][i]);
        }
        System.out.println("\nB");
        for (int i=0; i<numSubjects1; i++) {
            System.out.println(results1[2][i]);
        }
    }
}
