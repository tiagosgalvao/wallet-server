package com.galvao.wallet.infrastructure.repository.audit;

import com.galvao.wallet.infrastructure.entity.audit.AuditLogEntity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends CrudRepository<AuditLogEntity, Long> {
}