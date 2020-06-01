package com.galvao.wallet;

import com.galvao.wallet.grpc.TransactionServiceGrpc;
import com.galvao.wallet.infrastructure.entity.impl.AccountEntity;
import com.galvao.wallet.infrastructure.entity.impl.UserAccountEntity;
import com.galvao.wallet.infrastructure.repository.AccountRepository;
import com.galvao.wallet.infrastructure.repository.UserAccountRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.testcontainers.containers.KafkaContainer;

import java.math.BigDecimal;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public abstract class BaseTest {

	protected static final Double ZERO = 0d;
	protected static final Double ONE_HUNDRED = 100d;
	protected static final Double TWO_HUNDRED = 200d;
	protected static final String INSUFFICIENT_FUNDS = "insufficient_funds";
	private static final String SERVER_ADDRESS_NAME = "localhost";
	protected static TransactionServiceGrpc.TransactionServiceBlockingStub stub;
	private static ManagedChannel channel;
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	@Rule
	public KafkaContainer kafka = new KafkaContainer();
	@Value("${grpc.server.port}")
	protected int grpcMockPort;

	@Autowired
	protected AccountRepository accountRepository;
	@Autowired
	protected UserAccountRepository userAccountRepository;

	protected UserAccountEntity user;

	@Before
	public void before() {
		AccountEntity account = AccountEntity.builder()
				.eurTotalAmount(BigDecimal.ZERO)
				.gbpTotalAmount(BigDecimal.ZERO)
				.usdTotalAmount(BigDecimal.ZERO)
				.build();
		account = accountRepository.save(account);
		user = UserAccountEntity.builder().account(account).name("John").build();
		user = userAccountRepository.save(user);

		channel = ManagedChannelBuilder.forAddress(SERVER_ADDRESS_NAME, grpcMockPort).usePlaintext().build();
		stub = TransactionServiceGrpc.newBlockingStub(channel);
	}

	@After
	public void after() {
		userAccountRepository.deleteAll();
		accountRepository.deleteAll();
		channel.shutdown();
	}

}