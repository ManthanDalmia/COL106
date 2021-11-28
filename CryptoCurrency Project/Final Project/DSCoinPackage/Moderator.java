package DSCoinPackage;

import HelperClasses.Pair;
import java.util.*;
public class Moderator {
	public void initializeDSCoin(DSCoin_Honest DSObj, int coinCount) {

		Members members = new Members();
		members.UID = "Moderator";

		int itr = 0;
		int k = 100000;
		int mem_list_len = DSObj.memberlist.length;
		while (k < 100000 + coinCount) {
			Transaction latestTran = new Transaction();

			latestTran.Source = members;
			latestTran.Destination = DSObj.memberlist[itr % mem_list_len];
			latestTran.coinID = Integer.toString(k);
			latestTran.coinsrc_block = null;
			itr++;
			k++;
			DSObj.pendingTransactions.AddTransactions(latestTran);
		}

		DSObj.latestCoinID = Integer.toString(k - 1);
		int x = coinCount;
		try {
			while (x > 0) {
				Transaction[] storetrans = new Transaction[DSObj.bChain.tr_count];

				for (int j=0;j < DSObj.bChain.tr_count;j++)  {
					Transaction t = DSObj.pendingTransactions.RemoveTransaction();
					storetrans[j] = t;

				}
				TransactionBlock Y = new TransactionBlock(storetrans);

				for (int j=0;j < DSObj.bChain.tr_count;j++)  {

					storetrans[j].Destination.mycoins.add(new Pair<String, TransactionBlock>(storetrans[j].coinID, Y));
				}
				DSObj.bChain.InsertBlock_Honest(Y);
				x -= DSObj.bChain.tr_count;
			}
		} catch (EmptyQueueException e) {

		}
	}


	public void initializeDSCoin(DSCoin_Malicious DSObj, int coinCount) {

		Members members = new Members();
		members.UID = "Moderator";

		int itr = 0;
		int k = 100000;
		int mem_list_len = DSObj.memberlist.length;
		while (k < 100000 + coinCount) {
			Transaction latestTran = new Transaction();

			latestTran.Source = members;
			latestTran.Destination = DSObj.memberlist[itr % mem_list_len];
			latestTran.coinID = Integer.toString(k);
			latestTran.coinsrc_block = null;
			itr++;
			k++;
			DSObj.pendingTransactions.AddTransactions(latestTran);
		}

		DSObj.latestCoinID = Integer.toString(k - 1);
		int x = coinCount;
		try {
			while (x > 0) {
				Transaction[] storetrans = new Transaction[DSObj.bChain.tr_count];
				for (int j=0;j < DSObj.bChain.tr_count;j++) {
					Transaction t = DSObj.pendingTransactions.RemoveTransaction();
					storetrans[j] = t;
				}
				TransactionBlock Y = new TransactionBlock(storetrans);
				for (int j=0;j < DSObj.bChain.tr_count;j++) {
					storetrans[j].Destination.mycoins.add(new Pair<String, TransactionBlock>(storetrans[j].coinID, Y));
				}
				DSObj.bChain.InsertBlock_Malicious(Y);
				x -= DSObj.bChain.tr_count;
			}
		} catch (EmptyQueueException e) {

		}
	}
}



  

