package com.practice.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.practice.entity.BoardEntity;

import com.practice.entity.BoardFileEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

// Data Transfer Object
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {
	private Long id;
	private String boardWriter;
	private String boardPass;
	private String boardTitle;
	private String boardContents;
	private int boardHits;  // 조회수
	private LocalDateTime boardCreatedTime;
	private LocalDateTime boardUpdatedTime;

	// MultipartFile : 실제 파일을 담아줄 수 있는 스프링의 인터페이스
	// 파일이 하나면 MultipartFile 객체 한 개 선언, 여러 개면 List로 선언
	private List<MultipartFile> boardFile; // save.html -> Controller 할 때 파일 담는 용도
	private List<String> originalFileName; // 원본 파일 이름
	private List<String>  storedFileName;   // 서버 저장용 파일 이름
	private int fileAttached;		// 파일 첨부 여부(첨부 1, 미첨부 0)
									// byte도 상관없으나, boolean은 db에서 고려할 일이 많아 숫자가 편함.
	
	public BoardDTO(Long id, String boardWriter, String boardTitle, int boardHits, LocalDateTime boardCreatedTime) {
		this.id = id;
		this.boardWriter = boardWriter;
		this.boardTitle = boardTitle;
		this.boardHits = boardHits;
		this.boardCreatedTime = boardCreatedTime;
	}
	
	public static BoardDTO toBoardDTO(BoardEntity boardEntity) {
		BoardDTO boardDTO = new BoardDTO();
		boardDTO.setId(boardEntity.getId());
		boardDTO.setBoardWriter(boardEntity.getBoardWriter());
		boardDTO.setBoardPass(boardEntity.getBoardPass());
		boardDTO.setBoardTitle(boardEntity.getBoardTitle());
		boardDTO.setBoardContents(boardEntity.getBoardContents());
		boardDTO.setBoardHits(boardEntity.getBoardHits());
		boardDTO.setBoardCreatedTime(boardEntity.getCreatedTime());
		boardDTO.setBoardUpdatedTime(boardEntity.getUpdatedTime());
		if(boardEntity.getFileAttached() == 0) {
			boardDTO.setFileAttached(boardEntity.getFileAttached()); // 0
		} else {
			// (파일 여러 개일 때) 중간 전달 역할 위한 리스트 객체
			List<String> originalFileNameList = new ArrayList<>();
			List<String> storedFileNameList = new ArrayList<>();
			boardDTO.setFileAttached(boardEntity.getFileAttached()); // 1
			// 파일 이름을 가져가야 함
			// originalFileName, storedFileName : board_file_table에 있음 (BoardFileEntity)
			// join
			// select * from board_table b, board_file_table bf
			// where b.id = bf.board_id and where b.id=?

			// 첨부파일 하나면 인덱스 0, 여러 개면 반복문 필요
			/** 첨부파일이 하나일 때 코드
			 * boardDTO.setOriginalFileName(  // originalFileName은 굳이 사용할 일 없음.
			 * 			boardEntity.getBoardFileEntityList().get(0).getOriginalFileName());
			 * boardDTO.setStoredFileName(
			 * 			boardEntity.getBoardFileEntityList().get(0).getStoredFileName());
			 */
			for(BoardFileEntity boardFileEntity : boardEntity.getBoardFileEntityList()) {
				originalFileNameList.add(boardFileEntity.getOriginalFileName());
				storedFileNameList.add(boardFileEntity.getStoredFileName());
			}
			boardDTO.setOriginalFileName(originalFileNameList);
			boardDTO.setStoredFileName(storedFileNameList);
		}
		return boardDTO;
	}



	
}
