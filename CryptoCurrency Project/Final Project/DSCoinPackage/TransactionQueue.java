package DSCoinPackage;


public class TransactionQueue {

  public Transaction firstTransaction;
  public Transaction lastTransaction;
  public int numTransactions;

    public void AddTransactions (Transaction transaction) {
      if(numTransactions == 0){
            this.numTransactions++;
            this.firstTransaction = transaction;
            this.lastTransaction = transaction;
            transaction.previous=null;
            transaction.next = null;
        }
      else{
          numTransactions++;
          transaction.previous = lastTransaction;
          transaction.next = null;
          lastTransaction.next = transaction;
          this.lastTransaction = transaction;
          
      }
  }
  
  public Transaction RemoveTransaction () throws EmptyQueueException {
      if(this.numTransactions ==0 ){
           throw new EmptyQueueException();
      }else if(numTransactions == 1){
          Transaction temp = this.firstTransaction;
          this.firstTransaction = null;
          
          this.lastTransaction = null;
          numTransactions--;
          temp.previous = null;
          temp.next =null;
          return temp;
      }else{
        Transaction temp = this.firstTransaction;

        this.firstTransaction = temp.next;
        this.firstTransaction.previous=null;
        temp.next=null;
        temp.previous=null;
        this.numTransactions--;
        return temp;
      }
  }

  public int size() {
      return this.numTransactions;
  }
}
