package com.practice.dto;

import com.practice.entity.CommentEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class CommentDTO {
    private Long id;
    private String commentWriter;
    private String commentContents;
    private Long boardId;  // 중요!!
    private LocalDateTime commentCreatedTime;

    public static CommentDTO toCommentDTO(
            CommentEntity commentEntity, Long boardId) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(commentEntity.getId());
        commentDTO.setCommentWriter(commentEntity.getCommentWriter());
        commentDTO.setCommentContents(commentEntity.getCommentContents());
        commentDTO.setCommentCreatedTime(commentEntity.getCreatedTime());
        // 인수에 boardId 가 없을 경우 아래처럼 가져올 수 있다.
        // 그러러면 Service 메서드에 @Transactional을 반드시 붙여야 한다.
        // 이유는, commentEntity를 로딩하려면 db 트랜잭션 내에서 해당 앤티티를 로딩해야 하기 때문
        // => 해당 메소드가 트랜잭션 내에서 호출되어야 된다는 의미!
        // commentDTO.setBoardId(commentEntity.getBoardEntity().getId());
        commentDTO.setBoardId(boardId);

        return commentDTO;
    }
}
