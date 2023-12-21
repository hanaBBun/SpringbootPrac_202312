package com.practice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.practice.entity.BoardEntity;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
	
	// 조회수 증가; update board_table set board_hits = board_hits+1 where id = ?
	// @Query 어노테이션에 nativeQuery = true 를 주면 db를 기준으로 쿼리를 작성해야 한다.
	// 따로 적지 않으면 엔티티와 그것에 정의한 컬럼이름을 기준으로 작성! 
	// @Param : 쿼리 내 콜론이 앞에 붙은 것과 호환됨!
	@Modifying
	@Query(value = "update BoardEntity set boardHits = boardHits + 1 where id =:id")
	void updateHits(@Param("id") Long id);
}
