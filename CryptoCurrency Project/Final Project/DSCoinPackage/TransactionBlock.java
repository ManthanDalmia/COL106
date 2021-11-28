package DSCoinPackage;
import HelperClasses.MerkleTree;

public class TransactionBlock {

  public Transaction[] trarray;
  public TransactionBlock previous;
  public MerkleTree Tree;
  public String trsummary;
  public String nonce;
  public String dgst;

  TransactionBlock(Transaction[] t) {
      Transaction[] b = t.clone();
      this.trarray=b;
      this.previous =null;
      this.Tree = new MerkleTree();
      this.trsummary = this.Tree.Build(trarray);
      this.dgst = null;
      
  }

  public boolean checkpresence(Transaction t){
      for (int i = 0; i < this.trarray.length; i++){
            if(t == this.trarray[i]){
                return true;
            }
      }
      return false;
  }
  public  boolean checkisthere(Transaction t){
    for (int i = 0; i < this.trarray.length; i++){
          if(t.coinID.equals(trarray[i].coinID)){
              return true;
          }
    }
    return false;
}
 
  public boolean checkTransaction (Transaction t) {
     
      if(t.coinsrc_block == null){
          return true;
      }
      if(!t.coinsrc_block.checkisthere(t)){
          return false;
      }

      TransactionBlock temp = this;
      while(temp != t.coinsrc_block){

        for (int i = 0; i < temp.trarray.length; i++){
            if(t.coinID.equals(temp.trarray[i].coinID) && (t != temp.trarray[i])){
                return false;
            }
        }
        temp = temp.previous;

      }
      return true;
     
}
}
