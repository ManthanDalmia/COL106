package DSCoinPackage;

import HelperClasses.*;


public class BlockChain_Honest {

  public int tr_count;
  public static CRF c = new CRF(64);
  public static final String start_string = "DSCoin";
  public TransactionBlock lastBlock;

    
    public static boolean isnonce(String s){
        if(s.substring(0, 4).equals("0000"))
            {
                return true;
            }
        else{
            return false;
        }
    }
    
    
    
    public void InsertBlock_Honest (TransactionBlock newBlock) {
        if(lastBlock == null){
            newBlock.previous = null;
            
            String nonce_itr = "1000000001";
            while(isnonce(c.Fn(start_string + "#" + newBlock.trsummary + "#" + nonce_itr)) == false){
                int i = Integer.parseInt(nonce_itr) ;
                i++;
                nonce_itr = String.valueOf(i);
            }
            newBlock.nonce = nonce_itr;
            newBlock.dgst = c.Fn(start_string + "#" + newBlock.trsummary + "#" + newBlock.nonce);
            lastBlock = newBlock;
        }
        else{
            newBlock.previous = lastBlock;
            String nonce_itr = "1000000001";
            while(isnonce(c.Fn(lastBlock.dgst + "#" + newBlock.trsummary + "#" + nonce_itr)) == false){
                int i = Integer.parseInt(nonce_itr);
                i++;
                nonce_itr = String.valueOf(i);
            }
            newBlock.nonce = nonce_itr;
            newBlock.dgst = c.Fn(lastBlock.dgst + "#" + newBlock.trsummary + "#" + newBlock.nonce);
            lastBlock = newBlock;
        }
    }
}
