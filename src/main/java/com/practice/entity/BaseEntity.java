package com.practice.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

// 생성, 수정 시간을 다루는 DTO 
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class BaseEntity {
	@CreationTimestamp
	@Column(updatable = false)  // 수정 시에는 관여 x
	private LocalDateTime createdTime;
	
	@UpdateTimestamp
	@Column(insertable = false) // 입력 시에는 관여 x
	private LocalDateTime updatedTime;
}
