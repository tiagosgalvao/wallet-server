package com.galvao.wallet.infrastructure.entity.impl;

import com.galvao.wallet.infrastructure.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "user_account")
public class UserAccountEntity extends BaseEntity {
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "account_id", nullable = false)
	private AccountEntity account;
	@OneToMany(mappedBy = "userAccount", fetch = FetchType.LAZY)
	private List<DepositHistoricEntity> deposits;
	@OneToMany(mappedBy = "userAccount", fetch = FetchType.LAZY)
	private List<WithdrawHistoricEntity> withdraws;
	@Column
	private String name;
}