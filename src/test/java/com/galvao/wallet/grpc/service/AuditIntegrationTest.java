package com.galvao.wallet.grpc.service;

import com.galvao.wallet.BaseTest;
import com.galvao.wallet.grpc.TransactionRequest;
import com.galvao.wallet.grpc.service.dto.TransactionDto;
import com.galvao.wallet.infrastructure.kafka.message.TransactionKafkaDto;
import com.galvao.wallet.service.convert.MessageConverter;
import com.galvao.wallet.service.impl.KafkaService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit4.SpringRunner;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import static com.galvao.wallet.grpc.TransactionRequest.Currency.GBP;
import static com.galvao.wallet.service.convert.MessageConverter.createKafkaDtoFromDto;
import static com.galvao.wallet.service.enums.TransactionType.DEPOSIT;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@SpringJUnitConfig
@AutoConfigureMockMvc
@DirtiesContext
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("TEST")
public class AuditIntegrationTest extends BaseTest {
	@MockBean
	KafkaService kafkaService;

	@Test
	@DirtiesContext
	public void shouldSendAsyncMsgWhenErrorOnProcessingSyncTransaction() {

		// given
		TransactionRequest request = TransactionRequest.newBuilder().setUserId(user.getId()).setCurrency(GBP).setAmount(ONE_HUNDRED).build();
		TransactionDto transactionDto = MessageConverter.createDtoFromGrpc(request);
		TransactionKafkaDto message = createKafkaDtoFromDto(DEPOSIT, user.getAccount().getId(), transactionDto);
		when(kafkaService.sendSynchronousMessage(message)).thenThrow(new RuntimeException("Forced kafka error"));

		// when
		Throwable t = catchThrowable(() -> stub.deposit(request));

		// then
		org.assertj.core.api.Assertions.assertThat(t).isInstanceOf(StatusRuntimeException.class);
		org.assertj.core.api.Assertions.assertThat(((StatusRuntimeException) t).getStatus().getCode()).isEqualTo(Status.UNKNOWN.getCode());
		org.assertj.core.api.Assertions.assertThat(((StatusRuntimeException) t).getStatus().getDescription()).isEqualTo("Forced kafka error");
	}

}