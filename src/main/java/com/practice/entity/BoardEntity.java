package com.practice.entity;

import com.practice.dto.BoardDTO;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

// DB 테이블 역할을 하는 클래스
@Entity
@Getter
@Setter
@Table(name = "board_table")
// BoardEntity가 BaseEntity의 성질도 갖게 됨! (생성/수정 시간)
public class BoardEntity extends BaseEntity {
	@Id // pk 컬럼 지정. 필수 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(length = 20, nullable = false) //크기 20, not null
	private String boardWriter;
	
	@Column // 크기 255, null 가능 
	private String boardPass;
	
	@Column
	private String boardTitle;
	
	@Column(length = 500)
	private String boardContents;
	
	@Column
	private int boardHits;

	@Column
	private int fileAttached;  // 1 or 0

	@OneToMany(
			mappedBy = "boardEntity",  // 자식엔티티에서 연결해준 이름
			cascade = CascadeType.REMOVE,  // cascade DELETE 설정과 같음
			orphanRemoval = true,
			fetch = FetchType.LAZY)
	private List<BoardFileEntity> boardFileEntityList = new ArrayList<>();

	// 파일이 없는 경우 - board 테이블은 파일의 유무 정보만 저장하면 된다.
	public static BoardEntity toSaveEntity(BoardDTO boardDTO) {
		BoardEntity boardEntity = new BoardEntity();
		boardEntity.setBoardWriter(boardDTO.getBoardWriter());
		boardEntity.setBoardPass(boardDTO.getBoardPass());
		boardEntity.setBoardContents(boardDTO.getBoardContents());
		boardEntity.setBoardTitle(boardDTO.getBoardTitle());
		boardEntity.setBoardHits(0);
		boardEntity.setFileAttached(0); // 파일 없음.
		return boardEntity;
	}

	// id값과 조회수를 부여하는 데서 toSaveEntity와 차이남! 
	public static BoardEntity toUpdateEntity(BoardDTO boardDTO) {
		BoardEntity boardEntity = new BoardEntity(); 
		boardEntity.setId(boardDTO.getId());
		boardEntity.setBoardWriter(boardDTO.getBoardWriter());
		boardEntity.setBoardPass(boardDTO.getBoardPass());
		boardEntity.setBoardContents(boardDTO.getBoardContents());
		boardEntity.setBoardTitle(boardDTO.getBoardTitle());
		boardEntity.setBoardHits(boardDTO.getBoardHits());
		return boardEntity;
	}

	// 파일이 있는 경우
	public static BoardEntity toSaveFileEntity(BoardDTO boardDTO) {
		BoardEntity boardEntity = new BoardEntity();
		boardEntity.setBoardWriter(boardDTO.getBoardWriter());
		boardEntity.setBoardPass(boardDTO.getBoardPass());
		boardEntity.setBoardContents(boardDTO.getBoardContents());
		boardEntity.setBoardTitle(boardDTO.getBoardTitle());
		boardEntity.setBoardHits(0);
		boardEntity.setFileAttached(1); // 파일 있음.
		return boardEntity;
	}
}
