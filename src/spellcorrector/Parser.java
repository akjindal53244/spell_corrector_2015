/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package spellcorrector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author A. K. Jindal
 */
public class Parser {
    static int ins[][] = new int[56][56];
    static int del[][] = new int[56][56];
    static int subs[][] = new int[56][56];
    static int trans[][] = new int[56][56];
    int count, incorIndex,corIndex,k;
        
    Parser()    {
        count = 0;
        incorIndex = -1;
        corIndex = -1;
        k = -1;
    }
    
    void ProcessMisspelledFile(String infile, String outFile)    {
        File testFile = new File(infile);
        File Misspell_Reader = new File(outFile);
        try {
            FileWriter FR = new FileWriter(Misspell_Reader.getAbsoluteFile());
            BufferedWriter BW = new BufferedWriter(FR);
            BufferedReader TBR = new BufferedReader(new FileReader(testFile));
        
            String testLine;
            while((testLine = TBR.readLine()) != null) {
                BW = separateMisspelledWords(testLine, BW);
            }
            BW.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        //return BW;
    }
    
    BufferedWriter separateMisspelledWords(String testLine, BufferedWriter BW)    {
        testLine = testLine.trim();
        int colonIndex = testLine.indexOf(':');
        String Wrong_Words = testLine.substring(colonIndex+2);
        try {
            for(String word: Wrong_Words.split(", "))   {
                word = word.trim();
                BW.write(word+"\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return BW;
    }
            
    HashMap ProcessFrequecyFiles(HashMap<String, Double> GramCount, String subString_Files[]) {
        try {
            String FreqLine, word;
            int spaceIndex = -1;
            File Frequency_Reader;
            BufferedReader TBR;
            for(int i=0; i<subString_Files.length; i++) {
                Frequency_Reader = new File(subString_Files[i]);
                TBR = new BufferedReader(new FileReader(Frequency_Reader));
                while((FreqLine = TBR.readLine()) != null) {
                    FreqLine = FreqLine.trim();
                    spaceIndex = FreqLine.indexOf(' ');
                    if(spaceIndex == -1)
                        spaceIndex = FreqLine.indexOf('\t');
                    word = FreqLine.substring(0, spaceIndex);
                    GramCount.put(word, Double.parseDouble(FreqLine.substring(spaceIndex+1)));
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return GramCount;
    } 
     
    HashMap ProcessWordCountFile(HashMap<String, Double> WordCount, String Word_Count_File)    {
        try {
            String FreqLine, word;
            int spaceIndex = -1;
            File Frequency_Reader;
            BufferedReader TBR;
            Frequency_Reader = new File(Word_Count_File);
            TBR = new BufferedReader(new FileReader(Frequency_Reader));
            while((FreqLine = TBR.readLine()) != null) {
                FreqLine = FreqLine.trim();
                spaceIndex = FreqLine.indexOf(' ');
                if(spaceIndex == -1)
                    spaceIndex = FreqLine.indexOf('\t');
                word = (FreqLine.substring(0, spaceIndex).toLowerCase());
                WordCount.put(word, Double.parseDouble(FreqLine.substring(spaceIndex+1)));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return WordCount;
    } 
      
    void processConfusionFile(String confusionFile)   {
        File Confusion_File = new File(confusionFile);
        try {
            BufferedReader Matrix_BR = new BufferedReader(new FileReader(Confusion_File));
            String Line;
            
            while((Line = Matrix_BR.readLine())!=null) {
                separateFields(Line);
                reset();
            }
            Matrix_BR.close();
        }
        catch (IOException e) {
        e.printStackTrace();
        }
        //printTables(ins, del, subs, trans);
    }        
    
    void reset()    {
        count = 0;
        incorIndex = -1;
        corIndex = -1;
        k = -1;
    }
    void separateFields(String str)     {
        int flag = 0;
        boolean Ignore = false;
        String before = "",after = "";
        //System.out.print(str);
        int del_Index = str.indexOf('|');
        if(del_Index>0)
            before = str.substring(0, del_Index);
        else if(del_Index ==0)
            before = "";
        if(del_Index>=0)    {
            after = str.substring(del_Index+1);
            int countIndex = after.lastIndexOf('\t');
            count = Integer.parseInt(after.substring(countIndex+1));
            if(countIndex>0)
                after = after.substring(0, countIndex);
            else
                after = "";
            if(str.indexOf('>')!=-1)
                Ignore = true;
                
            if(Ignore==false)
            {
                fillEntries(before, after, str);
            }
        }
    }
    
    void fillEntries(String before, String after, String str) {
        int lenBef = before.length();
        int lenAft = after.length();
        k = findTabletoFill(lenBef, lenAft);
        findIndexes(before, after, lenBef, lenAft);
        InsertValueatIndex();
    }
    
    void findIndexes(String before, String after, int lenBef, int lenAft)    {
        int incorNum =0, corNum =0;
        
        if(lenBef >0)
            incorNum = (int)(before.charAt(lenBef-1));
        else
            incorNum = 0;
        
        if(k==1 || k==3)    {
            if(lenAft >0)
                corNum = (int)(after.charAt(0));
            else
                corNum = 0;
        }
        else if(k==2 || k==4)   {
            if(lenAft>0)
                corNum = (int)(after.charAt(lenAft-1));
            else
                corNum = 0;
        }
        
            switch(incorNum)    {
                case 32:    incorIndex = 52;
                            break;
                case 39:    incorIndex = 53;
                            break;
                case 45:    incorIndex = 54;
                            break;
                case 0:     incorIndex = 55;
                            break;
                default:    if(incorNum>=97 && incorNum<=122)
                            incorIndex = incorNum-97;
                            else if(incorNum>=65 && incorNum<=90)
                            incorIndex = incorNum-65+26;
                            else
                            incorIndex = -1;    
                            break;
                    
            }
            switch(corNum)    {
                case 32:    corIndex = 52;
                            break;
                case 39:    corIndex = 53;
                            break;
                case 45:    corIndex = 54;
                            break;
                case 0:     corIndex = 55;
                            break;
                default:    if(corNum>=97 && corNum<=122)
                            corIndex = corNum-97;
                            else if(corNum>=65 && corNum<=90) 
                            corIndex = corNum-65+26;
                            else
                            corIndex = -1;    
                            break;
            }
    }
    
    void InsertValueatIndex() {
        if(incorIndex >=0 && incorIndex <56 && corIndex >=0 && corIndex <56)    {
            if(k==1)
                ins[corIndex][incorIndex] = count;
            else if(k==2)
                del[corIndex][incorIndex] = count;
            else if(k==3)
                subs[corIndex][incorIndex] = count;
            else if(k==4)
                trans[corIndex][incorIndex] = count;
            
        }
    }
    
    int AccessValueatIndex(int type)   {
        if(incorIndex >=0 && incorIndex <56 && corIndex >=0 && corIndex <56)    {
            if(type==1)    return ins[corIndex][incorIndex];
            else if(type==2)    return del[corIndex][incorIndex];
            else if(type==3)    return subs[corIndex][incorIndex];
            else if(type==4)    return trans[corIndex][incorIndex];
            else    return -1;
        }
        else
            return -1;
    }
    
    int findTabletoFill(int lenBef, int lenAft) {
        if(lenBef > lenAft)
            return 1;
        else if(lenBef < lenAft)
            return 2;
        else if(lenBef == lenAft && lenBef == 1)
            return 3;
        else if(lenBef == lenAft && lenBef == 2)
            return 4;
        else
            return -1;
    }
    
    void printTables(int ins[][], int del[][], int subs[][], int trans[][]) {
        for(int i=0; i<56; i++) {
            System.out.print("{");
            for(int j=0; j<56; j++) {
                System.out.print(ins[i][j]+",");
            }
            System.out.print("},");
            System.out.println();
        }
        System.out.println("\n\n");
        for(int i=0; i<56; i++) {
            System.out.print("{");
            for(int j=0; j<56; j++) {
                System.out.print(del[i][j]+",");
            }
            System.out.print("},");
            System.out.println();
        }
        System.out.println("\n\n");
        for(int i=0; i<56; i++) {
            System.out.print("{");
            for(int j=0; j<56; j++) {
                System.out.print(subs[i][j]+",");
            }
            System.out.print("},");
            System.out.println();
        }
        System.out.println("\n\n");
        for(int i=0; i<56; i++) {
            System.out.print("{");
            for(int j=0; j<56; j++) {
                System.out.print(trans[i][j]+",");
            }
            System.out.print("},");
            System.out.println();
        }
    }
}
