package com.galvao.wallet.grpc.service;

import com.galvao.wallet.BaseTest;
import com.galvao.wallet.grpc.BalanceRequest;
import com.galvao.wallet.grpc.BalanceResponse;
import com.galvao.wallet.grpc.Currency;
import com.galvao.wallet.grpc.TransactionRequest;
import com.galvao.wallet.infrastructure.entity.impl.AccountEntity;
import com.galvao.wallet.infrastructure.repository.DepositHistoricRepository;
import com.galvao.wallet.infrastructure.repository.WithdrawHistoricRepository;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import static com.galvao.wallet.grpc.Currency.EUR;
import static com.galvao.wallet.grpc.Currency.GBP;
import static com.galvao.wallet.grpc.Currency.USD;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@SpringJUnitConfig
@AutoConfigureMockMvc
@DirtiesContext
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class TransactionIntegrationTest extends BaseTest {

	/*
	Integration Test
		1. Make a withdrawal of USD 200 for user with id 1. Must return "insufficient_funds".
		2. Make a deposit of USD 100 to user with id 1.
		3. Check that all balances are correct
		4. Make a withdrawal of USD 200 for user with id 1. Must return "insufficient_funds".
		5. Make a deposit of EUR 100 to user with id 1.
		6. Check that all balances are correct
		7. Make a withdrawal of USD 200 for user with id 1. Must return "insufficient_funds".
		8. Make a deposit of USD 100 to user with id 1.
		9. Check that all balances are correct
		10. Make a withdrawal of USD 200 for user with id 1. Must return "ok".
		11. Check that all balances are correct
		12. Make a withdrawal of USD 200 for user with id 1. Must return "insufficient_funds".
	 */

	@Autowired
	private DepositHistoricRepository depositHistoricRepository;
	@Autowired
	private WithdrawHistoricRepository withdrawHistoricRepository;

	private static void doWithdraw(Long userId, double amount, Currency currency) {
		TransactionRequest request = TransactionRequest.newBuilder().setUserId(userId).setAmount(amount).setCurrency(currency).build();
		stub.withdraw(request);
	}

	private static void doDeposit(Long userId, double amount, Currency currency) {
		TransactionRequest request = TransactionRequest.newBuilder().setUserId(userId).setAmount(amount).setCurrency(currency).build();
		stub.deposit(request);
	}

	private static BalanceResponse getBalance(Long userId) {
		return stub.balance(BalanceRequest.newBuilder().setUserId(userId).build());
	}

	@After
	@Order(1)
	public void after() {
		withdrawHistoricRepository.deleteAll();
		depositHistoricRepository.deleteAll();
	}

	@Test
	@DirtiesContext
	public void runIntegrationTest() {
		BalanceResponse balanceResponse;
		// step 1
		try {
			doWithdraw(user.getId(), TWO_HUNDRED, USD);  // insufficient_funds
		} catch (StatusRuntimeException i) {
			validateInsufficientFunds(i);
		}
		// step 2
		doDeposit(user.getId(), ONE_HUNDRED, USD);  // OK
		// step 3
		balanceResponse = getBalance(user.getId());
		assert balanceResponse != null;
		assertThat(balanceResponse.getGbpAmount(), equalTo(ZERO));        //   0 GBP
		assertThat(balanceResponse.getUsdAmount(), equalTo(ONE_HUNDRED)); // 100 USD
		assertThat(balanceResponse.getEurAmount(), equalTo(ZERO));        //   0 EUR
		// step 4
		try {
			doWithdraw(user.getId(), TWO_HUNDRED, USD);  // insufficient_funds
		} catch (StatusRuntimeException i) {
			validateInsufficientFunds(i);
		}
		// step 5
		doDeposit(user.getId(), ONE_HUNDRED, EUR);  // OK
		// step 6
		balanceResponse = getBalance(user.getId());
		assert balanceResponse != null;
		assertThat(balanceResponse.getGbpAmount(), equalTo(ZERO));        //   0 GBP
		assertThat(balanceResponse.getUsdAmount(), equalTo(ONE_HUNDRED)); // 100 USD
		assertThat(balanceResponse.getEurAmount(), equalTo(ONE_HUNDRED)); // 100 EUR
		// step 7
		try {
			doWithdraw(user.getId(), TWO_HUNDRED, USD); // insufficient_funds
		} catch (StatusRuntimeException i) {
			validateInsufficientFunds(i);
		}
		// step 8
		doDeposit(user.getId(), ONE_HUNDRED, USD); // OK
		// step 9
		balanceResponse = getBalance(user.getId());
		assert balanceResponse != null;
		assertThat(balanceResponse.getGbpAmount(), equalTo(ZERO));        //   0 GBP
		assertThat(balanceResponse.getUsdAmount(), equalTo(TWO_HUNDRED)); // 200 USD
		assertThat(balanceResponse.getEurAmount(), equalTo(ONE_HUNDRED)); // 100 EUR
		// step 10
		doWithdraw(user.getId(), TWO_HUNDRED, USD); // OK
		// step 11
		balanceResponse = getBalance(user.getId());
		assert balanceResponse != null;
		assertThat(balanceResponse.getGbpAmount(), equalTo(ZERO));        //   0 GBP
		assertThat(balanceResponse.getUsdAmount(), equalTo(ZERO));        //   0 USD
		assertThat(balanceResponse.getEurAmount(), equalTo(ONE_HUNDRED)); // 100 EUR
		// step 12
		try {
			doWithdraw(user.getId(), TWO_HUNDRED, USD);  // insufficient_funds
		} catch (StatusRuntimeException i) {
			validateInsufficientFunds(i);
		}
	}

	private void validateInsufficientFunds(StatusRuntimeException i) {
		org.assertj.core.api.Assertions.assertThat(i.getStatus().getCode()).isEqualTo(Status.ABORTED.getCode());
		org.assertj.core.api.Assertions.assertThat(i.getStatus().getDescription()).isEqualTo(INSUFFICIENT_FUNDS);
	}

	@Test
	@DirtiesContext
	public void shouldFailWhenUserNotFound() {
		// given
		TransactionRequest request = TransactionRequest.newBuilder().setUserId(5L).setCurrency(GBP).setAmount(ONE_HUNDRED).build();

		// when
		Throwable t = catchThrowable(() -> stub.deposit(request));

		// then
		org.assertj.core.api.Assertions.assertThat(t).isInstanceOf(StatusRuntimeException.class);
		org.assertj.core.api.Assertions.assertThat(((StatusRuntimeException) t).getStatus().getCode()).isEqualTo(Status.ABORTED.getCode());
		org.assertj.core.api.Assertions.assertThat(((StatusRuntimeException) t).getStatus().getDescription()).isEqualTo("User 5 not found.");
	}

	@Test
	@DirtiesContext
	public void shouldDoWithdrawGbp() {
		// given
		AccountEntity accountEntity = accountRepository.findAccountEntityByUserId(user.getId()).get();
		accountEntity.setGbpTotalAmount(BigDecimal.valueOf(ONE_HUNDRED));
		accountRepository.save(accountEntity);
		TransactionRequest request = TransactionRequest.newBuilder().setUserId(user.getId()).setCurrency(GBP).setAmount(ONE_HUNDRED).build();

		// when
		stub.withdraw(request);

		// then
		accountEntity = accountRepository.findAccountEntityByUserId(user.getId()).get();
		assertEquals(0, accountEntity.getGbpTotalAmount().compareTo(BigDecimal.ZERO));
	}

	@Test
	@DirtiesContext
	public void shouldDoWithdrawEur() {
		// given
		AccountEntity accountEntity = accountRepository.findAccountEntityByUserId(user.getId()).get();
		accountEntity.setEurTotalAmount(BigDecimal.valueOf(ONE_HUNDRED));
		accountRepository.save(accountEntity);

		// when
		doWithdraw(user.getId(), ONE_HUNDRED, EUR);

		// then
		accountEntity = accountRepository.findAccountEntityByUserId(user.getId()).get();
		assertEquals(0, accountEntity.getEurTotalAmount().compareTo(BigDecimal.ZERO));
	}

	@Test
	@DirtiesContext
	public void shouldDoDepositGbp() {
		// given
		TransactionRequest request = TransactionRequest.newBuilder().setUserId(user.getId()).setCurrency(GBP).setAmount(ONE_HUNDRED).build();

		// when
		stub.deposit(request);

		// then
		AccountEntity accountEntity = accountRepository.findAccountEntityByUserId(user.getId()).get();
		assertEquals(0, accountEntity.getGbpTotalAmount().compareTo(BigDecimal.valueOf(ONE_HUNDRED)));
	}

	@Test
	@DirtiesContext
	public void shouldFailWhenInvalidGrpcRequestUserNull() {
		// given
		TransactionRequest request = TransactionRequest.newBuilder().setCurrency(EUR).setAmount(ONE_HUNDRED).build();

		// when
		Throwable t = catchThrowable(() -> stub.deposit(request));

		// then
		org.assertj.core.api.Assertions.assertThat(t).isInstanceOf(StatusRuntimeException.class);
		org.assertj.core.api.Assertions.assertThat(((StatusRuntimeException) t).getStatus().getCode()).isEqualTo(Status.ABORTED.getCode());
		org.assertj.core.api.Assertions.assertThat(((StatusRuntimeException) t).getStatus().getDescription()).isEqualTo("UserId is mandatory.");
	}

	@Test
	@DirtiesContext
	public void shouldFailWhenInvalidGrpcRequestUnknownCurrency() {
		// given
		TransactionRequest request = TransactionRequest.newBuilder().setUserId(user.getId()).setAmount(ONE_HUNDRED).build();

		// when
		Throwable t = catchThrowable(() -> stub.deposit(request));

		// then
		org.assertj.core.api.Assertions.assertThat(t).isInstanceOf(StatusRuntimeException.class);
		org.assertj.core.api.Assertions.assertThat(((StatusRuntimeException) t).getStatus().getCode()).isEqualTo(Status.ABORTED.getCode());
		org.assertj.core.api.Assertions.assertThat(((StatusRuntimeException) t).getStatus().getDescription()).isEqualTo("unknown_currency");
	}

	@Test
	@DirtiesContext
	public void shouldFailWhenInvalidGrpcRequestAmountZero() {
		// given
		TransactionRequest request = TransactionRequest.newBuilder().setCurrency(EUR).setAmount(ZERO).build();

		// when
		Throwable t = catchThrowable(() -> stub.deposit(request));

		// then
		org.assertj.core.api.Assertions.assertThat(t).isInstanceOf(StatusRuntimeException.class);
		org.assertj.core.api.Assertions.assertThat(((StatusRuntimeException) t).getStatus().getCode()).isEqualTo(Status.ABORTED.getCode());
		org.assertj.core.api.Assertions.assertThat(((StatusRuntimeException) t).getStatus().getDescription()).isEqualTo("Amount must be greater than 0.");
	}

	@Test
	@DirtiesContext
	public void shouldFailWhenUserIdNotFound() {
		// given
		TransactionRequest request = TransactionRequest.newBuilder().setUserId(10L).setCurrency(EUR).setAmount(ONE_HUNDRED).build();

		// when
		Throwable t = catchThrowable(() -> stub.deposit(request));

		// then
		org.assertj.core.api.Assertions.assertThat(t).isInstanceOf(StatusRuntimeException.class);
		org.assertj.core.api.Assertions.assertThat(((StatusRuntimeException) t).getStatus().getCode()).isEqualTo(Status.ABORTED.getCode());
		org.assertj.core.api.Assertions.assertThat(((StatusRuntimeException) t).getStatus().getDescription()).isEqualTo("User 10 not found.");
	}

}