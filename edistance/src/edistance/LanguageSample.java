package edistance;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * @author andromeda
 */
public class LanguageSample {

    protected static HashMap allEditDistances(String directory3, String modalA, String modalB) {
        HashMap dictionary = new HashMap();
        File dir3 = new File(directory3);
        String[] children3 = dir3.list();
        for (String child : children3) {
            try {
                String file = directory3.concat("\\");
                file = file.concat(child);
                int value;
                int temp1 = new EditDistance(file, modalA, "0").calcEditDist();
                int temp2 = new EditDistance(file, modalB, "0").calcEditDist();
                value = temp1-temp2;
                dictionary.put(file.substring(10), value);
            }catch (Exception ex) {
                Logger.getLogger(LanguageSample.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return dictionary;
    }
    
    
    protected static Object[][] sampleRandom(int n, int length1, String directory3, HashMap dictionary) {
        File dir = new File(directory3);
        String[] children = dir.list();
        String file = directory3.concat("\\");
        
        Object[][] results = new Object[4][n];
        String[] names1 = new String[n];
        String[] names2 = new String[n];
        Double[] diffScores1 = new Double[n];
        Double[] diffScores2 = new Double[n];
        
        for (int i=0; i<n; i++) {
            ArrayList<Integer> sortedIndeces = SamplingEditDist.RandomSequence(children.length);
            String nameList1 = new String(), nameList2 = new String();
            int total1 = 0, total2 = 0;
            for (int j=0; j<length1; j++) {
                int length2 = children.length-length1;
                if (j>=length2) { //only bigger group
                    String file1 = file.concat(children[sortedIndeces.get(j).intValue()]);
                    nameList1 += file1.substring(10)+"/";
                    total1 += (int)dictionary.get(file1.substring(10));
                } else { //both
                    String file1 = file.concat(children[sortedIndeces.get(j).intValue()]);
                    String file2 = file.concat(children[sortedIndeces.get(j+length1).intValue()]);
                    nameList1 += file1.substring(10)+"/";
                    nameList2 += file2.substring(10)+"/";
                    total1 += (int)dictionary.get(file1.substring(10));
                    total2 += (int)dictionary.get(file2.substring(10));
                }
            }
            names1[i] = nameList1;
            diffScores1[i] = new Double(total1/(double)length1);
            names2[i] = nameList2;
            diffScores2[i] = new Double(total2/(double)(children.length-length1));
        }
        results[0] = names1;
        results[1] = diffScores1;
        results[2] = names2;
        results[3] = diffScores2;
        return results;
    }
    
    public static void main(String args[]) {
        String directory = args[0];
        int length1 = 29;
        
        String modalA = args[1], modalB = args[2];
        
        HashMap dictionary = allEditDistances(directory, modalA, modalB);
        
        Object[][] sampleResults = sampleRandom(1000,length1,directory,dictionary);
        
        for (int i=0; i<1000; i++) {
            System.out.print("\n"+sampleResults[0][i]+",");
            System.out.print(sampleResults[1][i]+",");
            System.out.print(sampleResults[2][i]+",");
            System.out.print(sampleResults[3][i]);
        }
     }
}
