package com.practice.repository;

import com.practice.entity.BoardFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardFileRepository
    extends JpaRepository<BoardFileEntity, Long> {
}
