package com.galvao.wallet.infrastructure.entity.impl;

import com.galvao.wallet.infrastructure.entity.BaseEntity;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "account")
public class AccountEntity extends BaseEntity {
	@OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
	@ToString.Exclude
	private List<UserAccountEntity> users;
	@Column(name = "gbp_total_amount", nullable = false)
	private BigDecimal gbpTotalAmount;
	@Column(name = "eur_total_amount", nullable = false)
	private BigDecimal eurTotalAmount;
	@Column(name = "usd_total_amount", nullable = false)
	private BigDecimal usdTotalAmount;
}