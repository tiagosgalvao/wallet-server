package com.galvao.wallet.infrastructure.entity.audit;

import com.galvao.wallet.enums.Currency;
import com.galvao.wallet.infrastructure.entity.BaseEntity;
import com.galvao.wallet.service.enums.TransactionType;

import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@DynamicUpdate
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "audit_log")
public class AuditLogEntity extends BaseEntity {
	@Column(name = "currency", nullable = false)
	@Enumerated(EnumType.STRING)
	private Currency currency;
	@Column(name = "amount", nullable = false)
	private BigDecimal amount;
	@Column(name = "user_id")
	private Long userId;
	@Column(name = "transaction_type")
	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;
}