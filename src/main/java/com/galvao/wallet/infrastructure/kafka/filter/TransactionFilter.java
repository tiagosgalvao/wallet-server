package com.galvao.wallet.infrastructure.kafka.filter;

import com.galvao.wallet.grpc.Transaction;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.stereotype.Component;

import static com.galvao.wallet.grpc.Transaction.Type.DEPOSIT;
import static com.galvao.wallet.grpc.Transaction.Type.WITHDRAW;

@Component
public class TransactionFilter implements RecordFilterStrategy<String, Transaction> {
	@Override
	public boolean filter(ConsumerRecord<String, Transaction> consumerRecord) {
		return !(WITHDRAW.name().equals(consumerRecord.value().getType().name())
				|| DEPOSIT.name().equals(consumerRecord.value().getType().name()));
	}
}
