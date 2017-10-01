/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package spellcorrector;

/**
 *
 * @author Chuniya
 */
public class StringAligner {
        String mSeqA;
        String mSeqB;
        int[][] mD;
        String mAlignmentSeqA = "";
        String mAlignmentSeqB = "";
        
        void reset() {
            mAlignmentSeqA = "";
            mAlignmentSeqB = "";
            mSeqA = "";
            mSeqB = "";
        }
        String[] process(String seqA, String seqB) {
                mSeqA = seqA;
                mSeqB = seqB;
                mD = new int[mSeqA.length() + 1][mSeqB.length() + 1];
                for (int i = 0; i <= mSeqA.length(); i++) {
                        for (int j = 0; j <= mSeqB.length(); j++) {
                                if (i == 0) {
                                        mD[i][j] = -j;
                                } else if (j == 0) {
                                        mD[i][j] = -i;
                                } else {
                                        mD[i][j] = 0;
                                }
                        }
                }
                
                for (int i = 1; i <= mSeqA.length(); i++) {
                        for (int j = 1; j <= mSeqB.length(); j++) {
                                int scoreDiag = mD[i-1][j-1] + weight(i, j);
                                int scoreLeft = mD[i][j-1] - 1;
                                int scoreUp = mD[i-1][j] - 1;
                                mD[i][j] = Math.max(Math.max(scoreDiag, scoreLeft), scoreUp);
                        }
                }
                return backtrack();
        }
        
        String[] backtrack() {
                int i = mSeqA.length();
                int j = mSeqB.length();
                while (i > 0 || j > 0) {                        
                        if (i>0 && j>0 && mD[i][j] == mD[i-1][j-1] + weight(i, j)) {                          
                                mAlignmentSeqA += mSeqA.charAt(i-1);
                                mAlignmentSeqB += mSeqB.charAt(j-1);
                                i--;
                                j--;
                        } else if (j>0 && mD[i][j] == mD[i][j-1] - 1) {
                                mAlignmentSeqA += "-";
                                mAlignmentSeqB += mSeqB.charAt(j-1);
                                j--;
                        } else if (i>0 && mD[i][j] == mD[i-1][j] - 1) {
                                mAlignmentSeqA += mSeqA.charAt(i-1);
                                mAlignmentSeqB += "-";
                                i--;
                        }
                }
                
                /*(if(i==0 || j==0)    {
                    if(i!=0 && j==0)    {
                        while(i>0)    {
                            mAlignmentSeqA += mSeqA.charAt(i-1);
                            mAlignmentSeqB += "-";
                            i--;
                        }
                    }
                    else if(i==0 && j!=0)   {
                        while(j>0)    {
                            mAlignmentSeqA += "-";
                            mAlignmentSeqB += mSeqB.charAt(j-1);
                            j--;
                        }
                    }
                }*/
                mAlignmentSeqA = new StringBuffer(mAlignmentSeqA).reverse().toString();
                mAlignmentSeqB = new StringBuffer(mAlignmentSeqB).reverse().toString();
                return printAlignments();
        }
        
        private int weight(int i, int j) {
                if (mSeqA.charAt(i - 1) == mSeqB.charAt(j - 1)) {
                        return 1;
                } else {
                        return -1;
                }
        }
        
       /* void printMatrix() {
                System.out.println("D =");
                for (int i = 0; i < mSeqA.length() + 1; i++) {
                        for (int j = 0; j < mSeqB.length() + 1; j++) {
                                System.out.print(String.format("%4d ", mD[i][j]));
                        }
                        System.out.println();
                }
                System.out.println();
        } */
        
        String[] printAlignments() {
                return new String[]{mAlignmentSeqA, mAlignmentSeqB};
        }       
}
