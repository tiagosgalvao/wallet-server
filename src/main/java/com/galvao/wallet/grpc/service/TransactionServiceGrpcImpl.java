package com.galvao.wallet.grpc.service;

import com.google.protobuf.Empty;

import com.galvao.wallet.exception.BusinessException;
import com.galvao.wallet.grpc.BalanceRequest;
import com.galvao.wallet.grpc.BalanceResponse;
import com.galvao.wallet.grpc.TransactionRequest;
import com.galvao.wallet.grpc.TransactionServiceGrpc;
import com.galvao.wallet.service.AccountService;
import com.galvao.wallet.service.TransactionManagerService;

import net.devh.boot.grpc.server.service.GrpcService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.galvao.wallet.grpc.Transaction.Type.DEPOSIT;
import static com.galvao.wallet.grpc.Transaction.Type.WITHDRAW;
import static io.grpc.Status.ABORTED;
import static io.grpc.Status.UNKNOWN;

@Slf4j
@GrpcService
@RequiredArgsConstructor
@SuppressWarnings("Duplicates")
public class TransactionServiceGrpcImpl extends TransactionServiceGrpc.TransactionServiceImplBase {

	private static final String RPC_CALL_FAIL = "RPC failed: {}";

	private final AccountService accountService;
	private final TransactionManagerService transactionManagerService;

	@Override
	public void deposit(TransactionRequest request, StreamObserver<Empty> responseObserver) {
		log.info("gRPC call - deposit:{}", request);
		try {
			transactionManagerService.start(DEPOSIT, request);
			responseObserver.onNext(Empty.newBuilder().build());
			responseObserver.onCompleted();
		} catch (BusinessException e) {
			log.error(RPC_CALL_FAIL, e.getMessage());
			responseObserver.onError(ABORTED.withCause(e).withDescription(e.getMessage()).asRuntimeException());
		} catch (Exception e) {
			log.error(RPC_CALL_FAIL, e.getMessage());
			responseObserver.onError(UNKNOWN.withCause(e).withDescription(e.getMessage()).asRuntimeException());
		}
	}

	@Override
	public void withdraw(TransactionRequest request, StreamObserver<Empty> responseObserver) {
		log.info("gRPC call - withdraw:{}", request);
		try {
			transactionManagerService.start(WITHDRAW, request);
			responseObserver.onNext(Empty.newBuilder().build());
			responseObserver.onCompleted();
		} catch (BusinessException e) {
			log.error(RPC_CALL_FAIL, e.getMessage());
			responseObserver.onError(ABORTED.withCause(e).withDescription(e.getMessage()).asRuntimeException());
		} catch (Exception e) {
			log.error(RPC_CALL_FAIL, e.getMessage());
			responseObserver.onError(UNKNOWN.withCause(e).withDescription(e.getMessage()).asRuntimeException());
		}
	}

	@Override
	public void balance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
		log.info("gRPC call - balance {}", request);
		try {
			responseObserver.onNext(accountService.getBalance(request.getUserId()));
			responseObserver.onCompleted();
		} catch (BusinessException e) {
			log.error(RPC_CALL_FAIL, e.getMessage());
			responseObserver.onError(ABORTED.withCause(e).withDescription(e.getMessage()).asRuntimeException());
		} catch (Exception e) {
			log.error(RPC_CALL_FAIL, e.getMessage());
			responseObserver.onError(UNKNOWN.withCause(e).withDescription(e.getMessage()).asRuntimeException());
		}
	}
}