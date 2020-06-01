package com.galvao.wallet.infrastructure.repository;

import com.galvao.wallet.infrastructure.entity.impl.DepositHistoricEntity;

import org.springframework.data.repository.CrudRepository;

public interface DepositHistoricRepository extends CrudRepository<DepositHistoricEntity, Long> {
}