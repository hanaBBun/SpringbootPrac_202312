package com.practice.service;

import com.practice.dto.CommentDTO;
import com.practice.entity.BoardEntity;
import com.practice.entity.CommentEntity;
import com.practice.repository.BoardRepository;
import com.practice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.xml.stream.events.Comment;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    public Long save(CommentDTO commentDTO) {  // id 값 반환
        /* 부모 엔티티(BoardEntity) 조회 */
        Optional<BoardEntity> optionalBoardEntity =
                boardRepository.findById(commentDTO.getBoardId());

        if(optionalBoardEntity.isPresent()) {
            BoardEntity boardEntity = optionalBoardEntity.get();

            /* CommentEntity commentEntity = new CommentEntity() 식으로
           객체를 만들수도 있겠지만 엔티티는 그야말로 db 그 자체를 말하고
           dp에 직접 접근하는 것은 위험하기 때문에 클래스의 메소드를 활용해서 저장 및 접근하도록 한다.  */
            /* 엔티티의 메서드 대신 @Builder 어노테이션을 활용해 빌더 패턴을 사용하기도 한다. */
            CommentEntity commentEntity = CommentEntity.toSaveEntity(commentDTO, boardEntity);
            return commentRepository.save(commentEntity).getId();

        } else {  // 혹시 부모 board데이터가 조회되지 않을 때
            return null;
        }
    }

    public List<CommentDTO> findAll(Long boardId) {
        // select * from comment_table where board_id=? order by id desc; (최신순)
        // 아래는 Optional을 바로 꺼낸 코드
        BoardEntity boardEntity = boardRepository.findById(boardId).get();
        List<CommentEntity> commentEntityList =
                commentRepository.findAllByBoardEntityOrderByIdDesc(boardEntity);

        /* EntityList -> DTOList */
        List<CommentDTO> commentDTOList = new ArrayList<>();
        for (CommentEntity commentEntity : commentEntityList) {
            CommentDTO commentDTO = CommentDTO.toCommentDTO(commentEntity, boardId);
            commentDTOList.add(commentDTO);
        }
        return commentDTOList;
    }
}
