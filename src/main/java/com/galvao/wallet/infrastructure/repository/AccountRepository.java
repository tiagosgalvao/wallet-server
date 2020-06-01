package com.galvao.wallet.infrastructure.repository;

import com.galvao.wallet.infrastructure.entity.impl.AccountEntity;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<AccountEntity, Long> {
	@Query(value = "SELECT a FROM AccountEntity as a JOIN a.users as u WHERE u.id = :userId")
	Optional<AccountEntity> findAccountEntityByUserId(@Param(value = "userId") Long userId);
}