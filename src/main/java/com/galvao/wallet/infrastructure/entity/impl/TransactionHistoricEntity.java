package com.galvao.wallet.infrastructure.entity.impl;

import com.galvao.wallet.enums.Currency;
import com.galvao.wallet.infrastructure.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@MappedSuperclass
public class TransactionHistoricEntity extends BaseEntity {
	@Column(name = "currency", nullable = false)
	@Enumerated(EnumType.STRING)
	private Currency currency;
	@Column(name = "amount", nullable = false)
	private BigDecimal amount;
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private UserAccountEntity userAccount;
}