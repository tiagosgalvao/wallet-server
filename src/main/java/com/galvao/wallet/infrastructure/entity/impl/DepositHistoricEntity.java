package com.galvao.wallet.infrastructure.entity.impl;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "deposit_historic")
public class DepositHistoricEntity extends TransactionHistoricEntity {
}