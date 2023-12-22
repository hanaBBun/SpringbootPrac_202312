package com.practice.repository;

import com.practice.entity.BoardEntity;
import com.practice.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.xml.stream.events.Comment;
import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    // select * from comment_table where board_id = ? order by board_id desc;
    // 댓글리스트를 구할 때 기준이 되는 board_id가 필요한데,
    // jpa에서는 boardEntity를 넘겨줘야 한다!
    List<CommentEntity> findAllByBoardEntityOrderByIdDesc(BoardEntity boardEntity);
}
