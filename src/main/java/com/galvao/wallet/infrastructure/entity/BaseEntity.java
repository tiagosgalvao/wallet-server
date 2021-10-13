package com.galvao.wallet.infrastructure.entity;

import com.galvao.wallet.infrastructure.entity.enums.Status;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
@DynamicUpdate
public abstract class BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;
	@CreationTimestamp
	@Column(name = "create_date", nullable = false)
	private LocalDateTime createDate;
	@UpdateTimestamp
	@Column(name = "update_date", nullable = false)
	private LocalDateTime updateDate;

	@Column
	@Builder.Default
	private String status = Status.ACTIVE.name();
}