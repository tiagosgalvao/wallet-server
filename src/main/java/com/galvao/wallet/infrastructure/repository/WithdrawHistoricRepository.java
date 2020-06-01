package com.galvao.wallet.infrastructure.repository;

import com.galvao.wallet.infrastructure.entity.impl.WithdrawHistoricEntity;

import org.springframework.data.repository.CrudRepository;

public interface WithdrawHistoricRepository extends CrudRepository<WithdrawHistoricEntity, Long> {
}