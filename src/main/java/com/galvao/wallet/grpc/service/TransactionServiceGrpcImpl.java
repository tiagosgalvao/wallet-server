package com.galvao.wallet.grpc.service;

import com.google.protobuf.Empty;

import com.galvao.wallet.grpc.BalanceRequest;
import com.galvao.wallet.grpc.BalanceResponse;
import com.galvao.wallet.grpc.TransactionRequest;
import com.galvao.wallet.grpc.TransactionServiceGrpc;
import com.galvao.wallet.grpc.service.dto.BalanceDto;
import com.galvao.wallet.grpc.service.dto.TransactionDto;
import com.galvao.wallet.infrastructure.exception.BusinessException;
import com.galvao.wallet.service.convert.MessageConverter;
import com.galvao.wallet.service.enums.TransactionType;
import com.galvao.wallet.service.impl.AccountService;
import com.galvao.wallet.service.impl.TransactionManagerService;

import net.devh.boot.grpc.server.service.GrpcService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import static com.galvao.wallet.service.enums.TransactionType.DEPOSIT;
import static com.galvao.wallet.service.enums.TransactionType.WITHDRAW;

@Slf4j
@GrpcService
public class TransactionServiceGrpcImpl extends TransactionServiceGrpc.TransactionServiceImplBase {
	private static final String RPC_CALL_FAIL = "RPC failed: {}";

	private final AccountService accountService;
	private final TransactionManagerService transactionManagerService;

	@Autowired
	public TransactionServiceGrpcImpl(AccountService accountService, TransactionManagerService transactionManagerService) {
		this.accountService = accountService;
		this.transactionManagerService = transactionManagerService;
	}

	@Override
	public void deposit(TransactionRequest request, StreamObserver<com.google.protobuf.Empty> responseObserver) {
		log.info("got grpc call - deposit {}", request);
		executeTransaction(DEPOSIT, request, responseObserver);
	}

	@Override
	public void withdraw(TransactionRequest request, StreamObserver<com.google.protobuf.Empty> responseObserver) {
		log.info("got grpc call - withdraw {}", request);
		executeTransaction(WITHDRAW, request, responseObserver);
	}

	private void executeTransaction(TransactionType transactionType, TransactionRequest request, StreamObserver<Empty> responseObserver) {
		try {
			TransactionDto transactionDto = getTransactionDto(request);
			transactionManagerService.start(transactionType, transactionDto);
			responseObserver.onNext(Empty.newBuilder().build());
			responseObserver.onCompleted();
		} catch (BusinessException e) {
			log.error(RPC_CALL_FAIL, e.getMessage());
			responseObserver.onError(Status.ABORTED.withCause(e).withDescription(e.getMessage()).asRuntimeException());
		} catch (Exception e) {
			log.error(RPC_CALL_FAIL, e.getMessage());
			responseObserver.onError(Status.UNKNOWN.withCause(e).withDescription(e.getMessage()).asRuntimeException());
		}
	}

	private TransactionDto getTransactionDto(TransactionRequest request) {
		if (request.getAmount() <= 0) {
			throw new BusinessException("Amount must be greater than 0.");
		}
		if (request.getUserId() == 0) {
			throw new BusinessException("UserId is mandatory.");
		}
		return validateGrpcRequest(MessageConverter.createDtoFromGrpc(request));
	}

	private TransactionDto validateGrpcRequest(TransactionDto transactionDto) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<TransactionDto>> violations = validator.validate(transactionDto);
		if (!CollectionUtils.isEmpty(violations)) {
			violations.forEach(v -> {
				throw new BusinessException(v.getMessage());
			});
		}
		return transactionDto;
	}

	@Override
	public void balance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
		log.info("got grpc call - balance {}", request);
		try {
			BalanceDto balance = accountService.getBalance(request.getUserId());
			BalanceResponse balanceResponse = BalanceResponse.newBuilder().setUserId(request.getUserId()).setGbpAmount(balance.getGbpAmount().doubleValue()).
					setEurAmount(balance.getEurAmount().doubleValue()).setUsdAmount(balance.getUsdAmount().doubleValue()).build();
			responseObserver.onNext(balanceResponse);
			responseObserver.onCompleted();
		} catch (BusinessException e) {
			log.error(RPC_CALL_FAIL, e.getMessage());
			responseObserver.onError(Status.ABORTED.withCause(e).withDescription(e.getMessage()).asRuntimeException());
		} catch (Exception e) {
			log.error(RPC_CALL_FAIL, e.getMessage());
			responseObserver.onError(Status.UNKNOWN.withCause(e).withDescription(e.getMessage()).asRuntimeException());
		}
	}

}