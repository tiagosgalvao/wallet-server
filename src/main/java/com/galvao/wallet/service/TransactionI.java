package com.galvao.wallet.service;

import com.galvao.wallet.grpc.service.dto.TransactionDto;

public interface TransactionI {

	void process(TransactionDto transactionDto);
}