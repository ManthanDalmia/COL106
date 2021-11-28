package DSCoinPackage;
import java.util.*;

import HelperClasses.MerkleTree;
import HelperClasses.Pair;
import HelperClasses.CRF;
import HelperClasses.TreeNode;

public class Members
 {
  public String UID;
  public List<Pair<String, TransactionBlock>> mycoins;
  public Transaction[] in_process_trans;

  public void initiateCoinsend(String destUID, DSCoin_Honest DSobj) {
    Transaction tobj = new Transaction();
    tobj.coinID = this.mycoins.get(0).first;
    tobj.coinsrc_block = this.mycoins.get(0).second;
    this.mycoins.remove(0);
    tobj.Source = this;

    for(int i=0;i<DSobj.memberlist.length;i++){
      if(DSobj.memberlist[i].UID.equals(destUID)){
        tobj.Destination = DSobj.memberlist[i];
        break;
      }
    }

    for(int i=0;i<100;i++){
      if(this.in_process_trans[i] == null){
        this.in_process_trans[i] = tobj;
        break;
      }
    }
    DSobj.pendingTransactions.AddTransactions(tobj);
    return;
  }


  public void initiateCoinsend(String destUID, DSCoin_Malicious DSobj) {
    Transaction tobj = new Transaction();
    tobj.coinID = mycoins.get(0).first;
    tobj.coinsrc_block = mycoins.get(0).second;
    mycoins.remove(0);
    tobj.Source = this;

    for(int i=0;i<DSobj.memberlist.length;i++){
      if(DSobj.memberlist[i].UID.equals(destUID)){
        tobj.Destination = DSobj.memberlist[i];
      }
    }

    for(int i=0;i<100;i++){
      if(this.in_process_trans[i] == null){
        this.in_process_trans[i] = tobj;
        break;
      }
    }
    DSobj.pendingTransactions.AddTransactions(tobj);
    
  }

  public Pair<String , String> siblingNodepair(TreeNode t){
    if(t.parent == null){
        return new Pair<String , String>(t.val,null);
    }else{
        return new Pair<String , String>(t.parent.left.val , t.parent.right.val);
    }
  }

  public List<Pair<String,String>> siblingCoupledPath (TreeNode t){
    List<Pair<String,String>> ans = new ArrayList<Pair<String,String>> ();
    TreeNode itr = t;
    while(itr !=null){
        ans.add(siblingNodepair(itr));
        itr = itr.parent;
    }
    return ans;
  }

  public String get_str(Transaction tr) {
    CRF obj = new CRF(64);
    String val = tr.coinID;
    if (tr.Source == null)
      val = val + "#" + "Genesis"; 
    else
      val = val + "#" + tr.Source.UID;

    val = val + "#" + tr.Destination.UID;

    if (tr.coinsrc_block == null)
      val = val + "#" + "Genesis";
    else
      val = val + "#" + tr.coinsrc_block.dgst;

    return obj.Fn(val);
  }

  public static <T> void revlist(List<T> list)
  {
      // base condition when the list size is 0
      if (list.size() <= 1 || list == null)
          return;

     
      T value = list.remove(0);
     
      // call the recursive function to reverse
      // the list after removing the first element
      revlist(list);

      // now after the rest of the list has been
      // reversed by the upper recursive call,
      // add the first value at the end
      list.add(value);
  }

  public Pair<List<Pair<String, String>>, List<Pair<String, String>>> finalizeCoinsend (Transaction tobj, DSCoin_Honest DSObj) throws MissingTransactionException {

    TransactionBlock temp = DSObj.bChain.lastBlock;
    boolean found = false;
    Transaction[] t = new Transaction[100];
    TransactionBlock tB = null ;
    while(temp != null){
      if(temp.checkpresence(tobj)){
        found = true;
        tB = temp;
        break;
      }
      temp = temp.previous;
    }

    if(!found){
      throw new MissingTransactionException();
    }
    else{
      MerkleTree mT = tB.Tree;
      TreeNode trN = null;
      for(int i=0;i<mT.store.size();i++){
        if(mT.store.get(i).val.equals(get_str(tobj))){
          trN = mT.store.get(i);
          break;
        }
      }
      
      List<Pair<String, String>> ans1 =  siblingCoupledPath(trN);


      TransactionBlock itr = DSObj.bChain.lastBlock;
      List<Pair<String,String>> ans2 = new ArrayList<Pair<String,String>>();
      // ans2.add(new Pair<String,String>(tB.previous.dgst,null));

      while(itr != tB){
        ans2.add(new Pair<String,String>(itr.dgst, itr.previous.dgst + "#" + itr.trsummary + "#" + itr.nonce));
        itr = itr.previous;
      }
      ans2.add(new Pair<String,String>(tB.dgst, tB.previous.dgst + "#" + tB.trsummary + "#" + tB.nonce));
      if(tB.previous ==  null){
        ans2.add(new Pair<String,String>(DSObj.bChain.start_string, null));
      }else{
        ans2.add(new Pair<String,String>(tB.previous.dgst,null));
      }

      revlist(ans2);
      for(int i=0;i<in_process_trans.length;i++){
        if(tobj == in_process_trans[i]){
          in_process_trans[i] = null;
        }
      }


      int i = 0;
      while (Integer.valueOf(tobj.Destination.mycoins.get(i).first) < Integer.valueOf(tobj.coinID)
              && i < tobj.Destination.mycoins.size()) {
        i++;
      }
      tobj.Destination.mycoins.add(i,new Pair<String,TransactionBlock>(tobj.coinID,tB));

      return new Pair<List<Pair<String, String>>, List<Pair<String, String>>>(ans1,ans2);
    }
  }

  public void MineCoin(DSCoin_Honest DSObj) throws EmptyQueueException {

    Transaction[] x = new Transaction[DSObj.bChain.tr_count];
    int i =0;
    Map<String, Integer> dupcheck = new HashMap<String, Integer>();  
    while(i < (DSObj.bChain.tr_count - 1) && (DSObj.pendingTransactions.numTransactions>0)){
      try {
        Transaction T = DSObj.pendingTransactions.RemoveTransaction();
      if( (DSObj.bChain.lastBlock.checkTransaction(T)) )
      {

        if(dupcheck.get(T.coinID)==null){

          x[i] = T;
          
          i++;
          dupcheck.put(T.coinID, 0);
        }
      }
        
      } catch (Exception e) {
        return;
      }
    } 
    Transaction minerRewardTransaction = new Transaction();
    int j = Integer.parseInt(DSObj.latestCoinID);
    j++;
    String newCoinID = Integer.toString(j);

    minerRewardTransaction.coinID = newCoinID;
    minerRewardTransaction.Source = null;
    minerRewardTransaction.coinsrc_block = null;
    minerRewardTransaction.Destination = this;
    x[DSObj.bChain.tr_count -1] = minerRewardTransaction;

    TransactionBlock tB = new TransactionBlock(x);
    DSObj.bChain.InsertBlock_Honest(tB);
    this.mycoins.add(new Pair<String, TransactionBlock>(newCoinID, tB));
    DSObj.latestCoinID = newCoinID;

  }  

  public void MineCoin(DSCoin_Malicious DSObj) throws EmptyQueueException {
    Transaction[] x = new Transaction[DSObj.bChain.tr_count];
    int i =0;
    Map<String, Integer> dupcheck = new HashMap<String, Integer>();  
    TransactionBlock lastBlock = DSObj.bChain.FindLongestValidChain();
    while(i < (DSObj.bChain.tr_count - 1) && (DSObj.pendingTransactions.numTransactions>0)){
      try {
        Transaction T = DSObj.pendingTransactions.RemoveTransaction();
      if( (lastBlock.checkTransaction(T)) )
      {
        if(dupcheck.get(T.coinID)==null){

          x[i] = T;
          
          i++;
          dupcheck.put(T.coinID, 0);
        }
      }
        
      } catch (Exception e) {
        return;
      }
    } 
    Transaction minerRewardTransaction = new Transaction();
    int j = Integer.parseInt(DSObj.latestCoinID);
    j++;
    String newCoinID = Integer.toString(j);

    minerRewardTransaction.coinID = newCoinID;
    minerRewardTransaction.Source = null;
    minerRewardTransaction.coinsrc_block = null;
    minerRewardTransaction.Destination = this;
    x[DSObj.bChain.tr_count -1] = minerRewardTransaction;

    TransactionBlock tB = new TransactionBlock(x);
    DSObj.bChain.InsertBlock_Malicious(tB);
    this.mycoins.add(new Pair<String, TransactionBlock>(newCoinID, tB));
    DSObj.latestCoinID = newCoinID;
    


    

  }  
}
