package DSCoinPackage;


import HelperClasses.*;
//javac DSCoinPackage/*.java HelperClasses/*.java
// java DSCoinPackage/DriverCode
// java DSCoinPackage/Tester

public class BlockChain_Malicious {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock[] lastBlocksList;

  public static boolean checkTransactionBlock (TransactionBlock tB) {
      CRF c =  new CRF(64);
      
      if(tB.previous == null){
          if(!(tB.dgst.substring(0,4).equals("0000"))){
              return false;
          }
          if(!(tB.dgst.equals(c.Fn(start_string + "#" + tB.trsummary + "#" + tB.nonce)))){
              return false;
          }
          MerkleTree M = new MerkleTree();
          if(!(M.Build(tB.trarray).equals(tB.trsummary))){
              return false;
          }

          for(int i =0;i<tB.trarray.length;i++){
              if(!tB.checkTransaction(tB.trarray[i])){
                  return false;
              }
          }

      }
      else{
          if(!(tB.dgst.substring(0,4).equals("0000"))){
              return false;
          }
          if(!(tB.dgst.equals(c.Fn(tB.previous.dgst+ "#" + tB.trsummary + "#" + tB.nonce)))){
              return false;
          }
          MerkleTree M = new MerkleTree();
          
          if(!(M.Build(tB.trarray).equals(tB.trsummary))){
              return false;
          }
          for(int i =0;i<tB.trarray.length;i++){
              if(!tB.checkTransaction(tB.trarray[i])){
                  return false;
              }
          }
      }
    return true;
  }

  public TransactionBlock FindChain(TransactionBlock tB){
      if(tB==null){
          return null;
      }
         TransactionBlock temp = tB;
         TransactionBlock itr = tB;
         while(itr.previous != null){
             if(checkTransactionBlock(itr)){
                 itr= itr.previous;
             }
             else{
                 temp = itr.previous;
                 itr=itr.previous;
             }
         }
         return temp;
  }
             
  public int FindlengthValid(TransactionBlock tB){
    //   TransactionBlock temp = tB;
    if(tB==null){
        return 0;
    }
                int i=0;
                    TransactionBlock itr = tB;
                    while(itr != null){
                        if(checkTransactionBlock(itr)){
                            itr = itr.previous;
                            i++;
                        }
                        else{
                            itr=itr.previous;
                            i=0;
                        }
                    }
                return i;
    }
             
  public TransactionBlock FindLongestValidChain () {
      if(lastBlocksList[0]==null){
          return null;
      }
     int[] lengthofchain =  new int[lastBlocksList.length];
     for(int i=0;i<lastBlocksList.length;i++){
         lengthofchain[i] = this.FindlengthValid(lastBlocksList[i]);
     }
     int maxind = 0;
     int max=lengthofchain[0];
     for(int i=0;i<lastBlocksList.length;i++){
         if(lengthofchain[i]>max){
             max = lengthofchain[i];
             maxind = i;
         }
     }
     return FindChain(lastBlocksList[maxind]);
  }
             
  public void InsertBlock_Malicious (TransactionBlock newBlock) {
      
        CRF c =  new CRF(64);
        TransactionBlock LastBlock = this.FindLongestValidChain();
        newBlock.previous = LastBlock;
        String nonce_itr = "1000000001";
        
        
        if(LastBlock==null){
            while(!((c.Fn(start_string + "#" + newBlock.trsummary + "#" + nonce_itr).substring(0,4)).equals("0000"))){
                int i = Integer.parseInt(nonce_itr);
                i++;
                nonce_itr = String.valueOf(i);
            }
            newBlock.nonce = nonce_itr;
            newBlock.dgst = c.Fn(start_string + "#" + newBlock.trsummary + "#" + newBlock.nonce);
        }else{
            while(!((c.Fn(LastBlock.dgst + "#" + newBlock.trsummary + "#" + nonce_itr).substring(0,4)).equals("0000"))){
                  int i = Integer.parseInt(nonce_itr);
                  i++;
                  nonce_itr = String.valueOf(i);
            }
            newBlock.nonce = nonce_itr;
            newBlock.dgst = c.Fn(LastBlock.dgst + "#" + newBlock.trsummary + "#" + newBlock.nonce);
        }
        
        if(lastBlocksList[0]==null){
            lastBlocksList[0] = newBlock;
            return;
        }
              boolean found = false;
        for(int i=0;i<lastBlocksList.length; i++){
            if(LastBlock == lastBlocksList[i] ){
                lastBlocksList[i] = newBlock;
                found = true;
            }
        }
              if(!found){
                for(int i=0;i<100;i++){
                    if(lastBlocksList[i] == null){
                        lastBlocksList[i] = newBlock;
                        break;
                    }
                }
              }
  }
}
