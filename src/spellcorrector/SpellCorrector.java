/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package spellcorrector;

/**
 *
 * @author Chuniya
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Collections;
import java.util.Comparator;
/**
 *
 * @author A. K. Jindal
 */


class Options   {
    int k[] = new int[3];
    int flag[] = new int[3];
    Options ()
    {
        for(int i=0; i<3; i++)  {
            flag[i] = 0;
            k[i] = Integer.MAX_VALUE;
        }
    }
    void resetScores()
    {
        for(int i=0; i<3; i++)  {
            flag[i] = 0;
            k[i] = Integer.MAX_VALUE;
        }
    }
    void calculateScore(int D[][], int tLen, int dLen, String testWord, String dictWord)   {
        for(int i=0; i<=tLen; i++)  {
            for (int j=0; j<=dLen; j++) {
                if(j-1>=0 && j-1<dLen)   {
                    k[0] = D[i][j-1] + 1;
                    flag[0] = 1;
                }
                if(i-1>=0 && i-1<tLen)   {
                   k[1] = D[i-1][j]+1;
                   flag[1] = 1;
                }
                if(i-1>=0 && i-1<tLen && j-1>=0 && j-1<dLen) {
                   k[2] = D[i-1][j-1];
                   flag[2] = 1;
                   if(testWord.charAt(i-1)!=dictWord.charAt(j-1))
                   k[2] += 2;
                }
                D[i][j] = findMin();
                resetScores();
            }   
        }
    }
    int findMin()    {
        
        int min;
        if(flag[0] == 1) {
            if(flag[1] == 1)
                min = Math.min (k[0],k[1]);
            else
                min = k[0];
            if(flag[2] ==1)
                min = Math.min (min,k[2]);
        }
        else if(flag[1] ==1) {
            if(flag[2] == 1)
                min = Math.min (k[1],k[2]);
            else
                min = k[1];
        }
        else {
            if(flag[2] ==1)
                min = k[2];
            else
                min = 0;
        }
       
        return min;
    }
    
}

class MyComparator implements java.util.Comparator<String> {

    private int referenceLength;

    public MyComparator(String reference) {
        super();
        this.referenceLength = reference.length();
    }

    public int compare(String s1, String s2) {
        int dist1 = Math.abs(s1.length() - referenceLength);
        int dist2 = Math.abs(s2.length() - referenceLength);

        return dist1 - dist2;
    }
}

class calculateDistance {
    File trainFile = new File("EngWords.txt");
    int MaxSuggestion = 7;
    
    public BufferedWriter findDist(String testWord, String criteria, HashMap dist, BufferedWriter BW)   {
        try {
            BufferedReader DBR = new BufferedReader(new FileReader(trainFile));
            String dictWord;
            
            while((dictWord = DBR.readLine())!=null) {
                //System.out.println(testWord+","+dictWord);
                int tLen = testWord.length();
                int dLen = dictWord.length();
                
                if(IgnoreWord(dLen, tLen) ==1)
                    continue;
                
                int D[][] = new int[tLen+1][dLen+1];
                D[0][0] = 0;
                
                findCost(D, tLen, dLen, testWord, dictWord);
                saveWord(dist, D, testWord, dictWord, tLen, dLen);
            }
            Sort_Write(testWord, dist, BW);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return BW;
    }
    
    int IgnoreWord(int dLen, int tLen) {
        if(dLen < (2.0/3)*tLen || dLen > 2*tLen)
            return 1;
        else
            return 0;
    }
    
    void findCost(int D[][], int tLen, int dLen, String testWord, String dictWord)   {
        Options value = new Options(); 
        value.calculateScore(D, tLen, dLen, testWord, dictWord);
    }
            
    void saveWord(HashMap dist, int D[][], String testWord, String dictWord, int tLen, int dLen) {
        
        if(dist.containsKey(D[tLen][dLen])) {
            ArrayList<String> val = new ArrayList<String>();
            val = (ArrayList<String>)dist.get(D[tLen][dLen]);
            val.add(dictWord);
            dist.put(D[tLen][dLen], val);
        }
        else    {
            ArrayList<String> val = new ArrayList<String>();
            val.add(dictWord);
            dist.put(D[tLen][dLen], val);
        }
    }
    
    void Sort_Write(String testWord, HashMap dist, BufferedWriter BW)   {
        try   {
            String out = "";
            Map<Integer, ArrayList<String>> sortedDist = new TreeMap<Integer, ArrayList<String>>(dist);
            int count = 0;
            out += testWord;
            for (Map.Entry<Integer, ArrayList<String>> entry : sortedDist.entrySet()) {
                ArrayList<String> tempList = new ArrayList<String>();
                
                Integer key = entry.getKey();
                tempList = entry.getValue();
                Collections.sort(tempList, new MyComparator(testWord));
                if (tempList != null) {
                    for (String value: tempList) {
                        out += "," + value + " :" + key; 
                        //System.out.println(testWord + ":" + value + "," + key);
                        count++;
                        if(count>=MaxSuggestion) break;
                    }
                    if(count>=MaxSuggestion) break;
                }
            }
            BW.write(out+"\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
        
public class SpellCorrector {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
    String Word_Error_File = "Words_Errors.txt";
    String Misspelled_File = "Temp.txt";
    String confusionFile = "1Edit.txt";
    String subString_Files[] = {"1_Letter_Count.txt", "2_Letter_Count.txt", "3_Letter_Count.txt"};
    String Word_Count_File = "Common_Word_Counts.txt";
    String EngDict_File = "EngWords.txt";
    
    String outFile = "Output_Noisy.txt";
    String criteria = "Norm";
    //ListMultimap<Integer, String> dist = ArrayListMultimap.create();
    //HashMap<Integer, String> dist = new HashMap<Integer,String>();
    HashMap<Integer, ArrayList<String>> dist = new HashMap<Integer, ArrayList<String>>();
    HashMap<String, Double> GramCount = new HashMap<String, Double>();
    HashMap<String, Double> WordCount = new HashMap<String, Double>();
    
    ArrayList<String> EngWords = new ArrayList<String>();
    File DictFile = new File(EngDict_File);
    BufferedReader DictReader = new BufferedReader(new FileReader(DictFile));
    String DictWord;
    while((DictWord = DictReader.readLine()) != null)   {
        EngWords.add(DictWord);
    }
    DictReader.close();
    System.out.println(EngWords.size());              
    calculateDistance calDist = new calculateDistance();
    Parser parser = new Parser();
    StringAligner Aligner = new StringAligner();  // Corerct, Incorrect
    NoisyChannel NChannel = new NoisyChannel();
    DoubleMetaphone Pronounce = new DoubleMetaphone();
    
    //HashMap<String, Long> Split = new HashMap<String, Long>();
    //System.out.println(NChannel.wordBreak("samsungandmangok", EngWords));
    //System.out.println(Pronounce.doubleMetaphone("pluse"));
    //System.out.println(Pronounce.doubleMetaphone("blue"));
    System.out.print(NChannel.NoDiffConsonant("rember","remember", Aligner));
    
    //parser.ProcessMisspelledFile(Word_Error_File, Misspelled_File);
    /*parser.processConfusionFile(confusionFile);
    GramCount = parser.ProcessFrequecyFiles(GramCount, subString_Files);
    WordCount = parser.ProcessWordCountFile(WordCount, Word_Count_File);
    NChannel.ProcessWrongSpellings(parser, Aligner, Pronounce, EngWords, Misspelled_File, outFile, GramCount, WordCount);
    */}
}
