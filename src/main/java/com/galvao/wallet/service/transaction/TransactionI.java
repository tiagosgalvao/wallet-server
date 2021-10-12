package com.galvao.wallet.service.transaction;

import com.galvao.wallet.grpc.Transaction;

public interface TransactionI {
	void process(Transaction transaction);
}