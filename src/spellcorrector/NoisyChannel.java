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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import javax.naming.spi.DirStateFactory.Result;
import org.omg.PortableInterceptor.DISCARDING;

/**
 *
 * @author A. K. Jindal
 */

class ValueComparator implements Comparator {

Map map;

public ValueComparator(Map map) {
    this.map = map;
}

public int compare(Object o1, Object o2) {

    return ((Long) map.get(o2)).compareTo((Long) map.get(o1));
}
}

public class NoisyChannel {
    String before,after;
    
    NoisyChannel() {
        before = "";
        after = "";
    }
    
    void ProcessWrongSpellings(Parser parser, StringAligner Aligner, DoubleMetaphone Pronounce, ArrayList<String> EngWords, String Misspelled_File, String outFile, HashMap<String, Double> GramCount, HashMap<String, Double> WordCount)  {
        File testFile = new File(Misspelled_File);
        File NoisyOutputFile = new File(outFile);
        HashMap<String, Long> Result00 = new HashMap<String, Long>();
        HashMap<String, Long> Result01 = new HashMap<String, Long>();
        HashMap<String, Long> Result10 = new HashMap<String, Long>();
        HashMap<String, Long> Result11 = new HashMap<String, Long>();
        HashMap<String, Long> Split = new HashMap<String, Long>();
        HashMap<String, Long> ResultOther = new HashMap<String, Long>();
        List<HashMap<String, Long>> Set = new ArrayList<HashMap<String, Long>>();
        String testWord = "", DictWord = "";
        String testCode = "", DictCode = "";
            
        try {
            FileWriter NoisyChannelWriter = new FileWriter(NoisyOutputFile.getAbsoluteFile());
            BufferedWriter NoisyWriter = new BufferedWriter(NoisyChannelWriter);
            BufferedReader TestReader = new BufferedReader(new FileReader(testFile));
            long startTime,startTime1;   
            // ... the code being measured ...    
            long estimatedTime,estimatedTime1=0;
            
            while((testWord = TestReader.readLine()) != null) {
                //if(!EngWords.contains(testWord))    {
                    /*Split = wordBreak(testWord, EngWords, Split);
                 
                    if(Split.size()>0)  {
                        Result00 = Split;
                    }
                    else    {
                 *  */
                        int pos = checkForSplit(testWord,EngWords,WordCount);
                        if(pos!=-1) {
                            long score = (long)Math.round((WordCount.get(testWord.substring(0, pos))+WordCount.get(testWord.substring(pos)))/2.0);
                            Result00.put(testWord.substring(0, pos)+" "+testWord.substring(pos), score);
                            Result00.put(testWord.substring(0, pos)+"-"+testWord.substring(pos), score-1);
                        }
                        for(int i=0; i<EngWords.size(); i++)    {
                            DictWord = EngWords.get(i); 
                            if(Math.abs(testWord.length() - DictWord.length()) >2 || DictWord.length() <(int)Math.ceil(0.6*testWord.length()) || DictWord.length() >(int)(Math.ceil(1.4*testWord.length())))
                                continue;
                            //startTime1 = System.nanoTime();
                            int NoConsonantChange = NoDiffConsonant(testWord, DictWord, Aligner);
                            if(NoConsonantChange<2) {
                                testCode = Pronounce.doubleMetaphone(testWord);
                                DictCode = Pronounce.doubleMetaphone(DictWord);
                                int priority = EditOneMismatchType(testCode, DictCode, 1);
                                int NEdit = EditOneMismatchType(testWord, DictWord, 0);
                                if(priority == 0)   {
                                    //System.out.println("P0");
                                    Set  = EditMultiMismatchType(parser, Aligner, testWord, DictWord, GramCount, WordCount, Result00, Result01);
                                    Result00 = Set.get(0);
                                    Result01 = Set.get(1);
                                }
                                else if(priority ==1)   {
                                    //System.out.println("P1");
                                    Set  = EditMultiMismatchType(parser, Aligner, testWord, DictWord, GramCount, WordCount, Result10, Result11);
                                    Result10 = Set.get(0);
                                    Result11 = Set.get(1);
                                }
                                else if(priority ==-1)   {
                                    if(NEdit==1)    {
                                        Set  = EditMultiMismatchType(parser, Aligner, testWord, DictWord, GramCount, WordCount, Result00, Result01);
                                        Result00 = Set.get(0);
                                        Result01 = Set.get(1);
                                    }
                                }
                            }
                        }
                    //}
                    //estimatedTime = System.nanoTime() - startTime;
                    //System.out.println(estimatedTime+","+estimatedTime1);
                    estimatedTime1 = 0;
                    NoisyWriter = Sort_Write(testWord, Result00, Result01, Result10, Result11, NoisyWriter);
                    Result00.clear();
                    Result01.clear();
                    Result10.clear();
                    Result11.clear();
                    Set.clear();
                    //Split.clear();
                //}
            }
            NoisyWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    int checkForSplit(String testWord, ArrayList<String> EngWords,HashMap<String, Double> WordCount)    {
        int pos = -1;
        Double maxval = Double.MIN_VALUE;
        for(int i=1; i<testWord.length(); i++)  {
            String first = testWord.substring(0, i);
            String second = testWord.substring(i);
            if(EngWords.contains(first) && EngWords.contains(second))    {
                if((WordCount.containsValue(first) && WordCount.containsValue(first)))   
                    if((WordCount.get(first)+WordCount.get(second))/2.0 > maxval) {
                    pos = i;
                    maxval = (WordCount.get(first)+WordCount.get(second))/2;
                }   
            }
        }
        return pos;
    }
    
    int NoDiffConsonant(String AligntestCode,String AlignDictCode, StringAligner Aligner)  {
        String AlignedSeq[] = Aligner.process(AlignDictCode, AligntestCode);
        Aligner.reset();
        String CorrString = AlignedSeq[0];
        String IncorrString = AlignedSeq[1];
        System.out.println(IncorrString+" "+CorrString);       
        int count = 0;
        Boolean flag1 = false, flag2 = false;
        for(int i=0; i<CorrString.length(); i++) {
            int k1 = (int)(IncorrString.charAt(i));
            int k2 = (int)(CorrString.charAt(i));
            if(IncorrString.charAt(i)!=CorrString.charAt(i))    {
                if(k1>=97 && k1<=122)   {
                    if(k1!=97 && k1!=101 && k1!=105 && k1!=111 && k1!=117)
                        flag1 = true;
                }
                if(k2>=97 && k2<=122)   {
                    if(k2!=97 && k2!=101 && k2!=105 && k2!=111 && k2!=117)
                        flag2 = true;
                }
                //System.out.println(flag1+" "+flag2);
                System.out.println(IncorrString+" "+CorrString);       
            
                if(flag1==true || flag2==true)  {
                    if(i+2<CorrString.length() && new StringBuffer(IncorrString.substring(i,i+3)).reverse().toString().equals(CorrString.substring(i, i+3)))
                        i = i+2;
                    count++;
                    flag1 = false;  flag2 = false;
                }
            }
        }
        return count;
    }
    
    BufferedWriter Sort_Write(String testWord, HashMap Result00, HashMap Result01, HashMap Result10, HashMap Result11, BufferedWriter NoisyWriter)   {
        try   {
            int totalCount = 0;
            String out = "";
            int count = 0;
            out += testWord;
            ValueComparator comp=new ValueComparator(Result00);
            Map<String,Long> newMap = new TreeMap(comp);
            newMap.putAll(Result00);
            Iterator entries = newMap.entrySet().iterator();
            while (entries.hasNext()) {
            	Map.Entry entry = (Map.Entry) entries.next();
                String key = (String)entry.getKey();
                long value = (Long)entry.getValue();
                //System.out.println(key+","+value);
                out += "," + key + " : " + value; 
                totalCount++;
                if(totalCount>=3) 
                break;
            }
            
            comp=new ValueComparator(Result01);
            newMap = new TreeMap(comp);
            newMap.putAll(Result01);
            entries = newMap.entrySet().iterator();
            while (entries.hasNext()) {
            	Map.Entry entry = (Map.Entry) entries.next();
                String key = (String)entry.getKey();
                long value = (Long)entry.getValue();
                //System.out.println(key+","+value);
                out += "," + key + " : " + value; 
                totalCount++;
                if(totalCount>=5) 
                break;
            }
            
            comp=new ValueComparator(Result10);
            newMap = new TreeMap(comp);
            newMap.putAll(Result10);
            entries = newMap.entrySet().iterator();
            while (entries.hasNext()) {
            	Map.Entry entry = (Map.Entry) entries.next();
                String key = (String)entry.getKey();
                long value = (Long)entry.getValue();
                //System.out.println(key+","+value);
                out += "," + key + " : " + value; 
                totalCount++;
                if(totalCount>=6) 
                break;
            }
            
            comp=new ValueComparator(Result11);
            newMap = new TreeMap(comp);
            newMap.putAll(Result11);
            entries = newMap.entrySet().iterator();
            while (entries.hasNext()) {
            	Map.Entry entry = (Map.Entry) entries.next();
                String key = (String)entry.getKey();
                long value = (Long)entry.getValue();
                //System.out.println(key+","+value);
                out += "," + key + " : " + value; 
                totalCount++;
                if(totalCount>=7) 
                break;
            }
            
            NoisyWriter.write(out+"\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return NoisyWriter;
    }
    
    List<HashMap<String, Long>> EditMultiMismatchType(Parser parser, StringAligner Aligner, String X, String W, HashMap<String, Double> GramCount, HashMap<String, Double> WordCount,  HashMap<String, Long> Results0, HashMap<String, Long> Results1)   {
        List<HashMap<String, Long>> Set = new ArrayList<HashMap<String, Long>>();
        Set.add(0, Results0);
        Set.add(1, Results1);
        
        String AlignedSeq[] = Aligner.process(W, X);
        Aligner.reset();
        String CorrString = AlignedSeq[0];
        String IncorrString = AlignedSeq[1];
        int Total_Edits = 0;
        
        //System.out.println(X+","+W);
        //System.out.println(CorrString+","+IncorrString);
        
        /*int cIns =0, cDel =0, cSubs =0, cTrans =0;
        for(int i=0; i<CorrString.length(); i++)    {
            if(CorrString.charAt(i)!=IncorrString.charAt(i))    {
            if(CorrString.charAt(i)=='-')
                cIns++;
            else if(IncorrString.charAt(i)=='-')
                cDel++;
            else if(i<IncorrString.length()-1)   {
                //System.out.println("F");
                if(IncorrString.charAt(i)== CorrString.charAt(i+1) && IncorrString.charAt(i+1)== CorrString.charAt(i))  {
                    i = i+1;
                    cTrans++;
                }
                else 
                    cSubs++;
           }
           else if(i==IncorrString.length()-1)
                cSubs++;
            }
        } 
        int eachMax = (int)Math.floor(Math.log(W.length())/Math.log(2));
        */
        for(int i=0; i<CorrString.length(); i++)    {
            if(CorrString.charAt(i)!=IncorrString.charAt(i))    {
            if(CorrString.charAt(i)=='-' || IncorrString.charAt(i)=='-')
                Total_Edits++;
            else if(i<IncorrString.length()-1)   {
                //System.out.println("F");
                if(IncorrString.charAt(i)== CorrString.charAt(i+1) && IncorrString.charAt(i+1)== CorrString.charAt(i))  {
                    i = i+1;
                }
                Total_Edits++;
           }
           else if(i==IncorrString.length()-1)
                Total_Edits++;
            }
            if(Total_Edits >Math.floor(Math.log(W.length()-1)/Math.log(3))+1 || Total_Edits>2)
            return Set;
        } 
        
        long Score = 0;
        int NoEdits = 0;
        parser.k = 0;
        if(CorrString.length() == IncorrString.length())    {
            for(int i=0; i<CorrString.length(); i++)    {
                if(CorrString.charAt(i)=='-')   {
                    //System.out.println("D");
                    if(i>0) {
                        before = IncorrString.substring(i-1, i+1);
                        after = CorrString.substring(i-1, i);
                    }
                    else if(i==0)   {
                        before = IncorrString.substring(i, i+1);
                        after = "";
                    }
                    parser.k = 1;
                }
                else if(IncorrString.charAt(i)=='-')   {
                    //System.out.println("E");
                    if(i>0) {
                        before = IncorrString.substring(i-1, i);
                        after = CorrString.substring(i-1, i+1);
                    }
                    else if(i==0)   {
                        before = "";
                        after = CorrString.substring(i, i+1);
                    }
                    parser.k = 2;
                }
                else if(CorrString.charAt(i)!= IncorrString.charAt(i))   {
                    if(i<IncorrString.length()-1) {
                        //System.out.println("F");
                        if(IncorrString.charAt(i)== CorrString.charAt(i+1) && IncorrString.charAt(i+1)== CorrString.charAt(i))  {
                            before = IncorrString.substring(i, i+2);
                            after = CorrString.substring(i, i+2);
                            i = i+1;
                            parser.k = 4;
                        }
                        else    {
                            before = IncorrString.substring(i, i+1);
                            after = CorrString.substring(i, i+1);
                            parser.k = 3;
                        }
                    }
                    else if(i==IncorrString.length()-1) {
                        //System.out.println("G");
                        before = IncorrString.substring(i, i+1);
                        after = CorrString.substring(i, i+1);
                        parser.k = 3;
                    }
                }
                
                if(parser.k!=0) {
                    parser.findIndexes(before, after, before.length(), after.length());
                    int Numerator = parser.AccessValueatIndex(parser.k);
                    parser.count = Numerator;
                    Double Demoninator = GramCount.get(after);
                    Double Word_Count = WordCount.get(W);
                    parser.k = 0;
                    NoEdits++;
                    if(Demoninator !=null && Word_Count !=null)  {
                        Double Final_Prob = Math.pow(10.0, 5)*((100*Numerator*Word_Count)/(double)Demoninator);
                        Score += (long)Math.round(Final_Prob);
                    }
                }
            }
                if(NoEdits ==1) {
                    Results0.put(W,Score);
                }
                else if(NoEdits==2)   {
                    Score /= (NoEdits);
                    Results1.put(W, Score);
                }
            Set.set(0, Results0);
            Set.set(1, Results1);
        }
        return Set;
    }
    
    int EditOneMismatchType(String X, String W, int flag)    {
        if(flag==1) {
            if(Math.abs(X.length() - W.length()) >1)
                return -1;
        }
        
        int pos_i = -1, pos_j = -1;
        for(int i=0, j=0; i<X.length() && j<W.length(); ) {
            if(X.charAt(i) == W.charAt(j))  {
                i++; j++;
                pos_i = i;
                pos_j = j;
                
            }
            else    {
                pos_i = i;
                pos_j = j;
                break;
            }
        }
        
        if(pos_i ==X.length() || pos_j ==W.length())    {
            if(pos_i == X.length() && pos_j ==W.length()-1) {
                return 1;
            }
            else if(pos_i == X.length()-1 && pos_j ==W.length())    {
                return 1;
            }
            else if(pos_i == X.length() && pos_j ==W.length())  {
                return 0;
            }
            else    {
                return -1;
            }
                
        }
        else    {
            if(pos_j>=0 && pos_i>=0 && pos_j<W.length()-1 && (X.substring(pos_i).equals(W.substring(pos_j+1))))  {
                    return 1;
            }
            else if(pos_j>=0 && pos_i>=0 && pos_i<X.length()-1 && X.substring(pos_i+1).equals(W.substring(pos_j)))    {
                return 1;
            }
            else if(pos_i ==X.length()-1 && pos_j == W.length()-1)  {
                return 1;
            }
            else if(X.substring(pos_i+1).equals(W.substring(pos_j+1)))    {
                return 1;
            }
            else if(pos_i+1<X.length() && pos_j+1<W.length() && X.charAt(pos_i) == W.charAt(pos_j+1) && X.charAt(pos_i+1) == W.charAt(pos_j))    {
                if(pos_i+2<X.length() && pos_j+2<W.length())    {
                    if(X.substring(pos_i+2).equals(W.substring(pos_j+2)))   {
                        return 1;
                    }
                    else    {
                        return -1;
                    }
                }
                else if(pos_i+2==X.length() && pos_j+2==W.length()) {
                    return 1;
                }
                else    {
                    return -1;
                }
            }
            else    {
                return -1;
            }
        }
    }
}






