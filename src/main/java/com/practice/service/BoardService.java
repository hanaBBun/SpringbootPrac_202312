package com.practice.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.practice.entity.BoardFileEntity;
import com.practice.repository.BoardFileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.practice.dto.BoardDTO;
import com.practice.entity.BoardEntity;
import com.practice.repository.BoardRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

// DTO -> Entity (Entity Class)
// Entity -> DTO  (DTO Class)
// ; 엔티티의 경우 최대한 서비스 클래스까지만 오도록!

@Service
@RequiredArgsConstructor
public class BoardService {

	private final BoardRepository boardRepository;
	private final BoardFileRepository boardFileRepository;
	
	public void save(BoardDTO boardDTO) throws IOException {
		// 파일 첨부 여부에 따라 로직 분리
		if(boardDTO.getBoardFile().isEmpty()) {
			// 첨부 파일 없음.
			BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
			boardRepository.save(boardEntity);
		} else {
			// 첨부 파일 있음.
			/*
				1. DTO에 담긴 파일을 꺼냄
				2. 파일의 이름 가져옴 (ex. 내사진.jpg)
				3. 서버 저장용 이름을 만듦 (ex. 938463280_내사진.jpg)
				4. 저장 경로 설정
				5. 해당 경로에 파일 저장
				6. board_table에 해당 데이터 save 처리
				7. board_file_table에 해당 데이터 save 처리
			 */
			/** 파일이 한 개만 첨부되었을 경우 순서
			MultipartFile boardFile = boardDTO.getBoardFile();  // 1
			String originalFileName = boardFile.getOriginalFilename(); // 2
			String storedFileName = System.currentTimeMillis() + "_" + originalFileName; // 3
			//String savePath = "C:/springboot_img/" + storedFileName; 지정한 경로가 미리 만들어져 있어야!
			String savePath = "/Users/hanabbun/app/temp_springboot_img/" + storedFileName; // 4
			boardFile.transferTo(new File(savePath)); // 5 파일 저장까지 끝!

			BoardEntity boardEntity = BoardEntity.toSaveFileEntity(boardDTO);
			Long savedId = boardRepository.save(boardEntity).getId();
			// 이 작업을 거치는 이유는, boardEntity를 생성할 땐 id 값을 가지지 않기 때문이다.
			// board 테이블에 저장 후 다시 해당 객체를 가져왔다.
			BoardEntity board = boardRepository.findById(savedId).get(); // 6

			BoardFileEntity boardFileEntity =
					BoardFileEntity.toBoardFileEntity(board, originalFileName, storedFileName);
			boardFileRepository.save(boardFileEntity); // 7
			 */
			// 파일이 여러 개 첨부되면 부모 board 객체부터 먼저 만들어줘야 함.
			BoardEntity boardEntity = BoardEntity.toSaveFileEntity(boardDTO);
			Long savedId = boardRepository.save(boardEntity).getId();
			BoardEntity board = boardRepository.findById(savedId).get();
			for (MultipartFile boardFile : boardDTO.getBoardFile()) {
				//MultipartFile boardFile = boardDTO.getBoardFile();  // 1
				String originalFileName = boardFile.getOriginalFilename(); // 2
				String storedFileName = System.currentTimeMillis() + "_" + originalFileName; // 3
				//String savePath = "C:/springboot_img/" + storedFileName; 지정한 경로가 미리 만들어져 있어야!
				String savePath = "/Users/hanabbun/app/temp_springboot_img/" + storedFileName; // 4
				boardFile.transferTo(new File(savePath)); // 5 파일 저장까지 끝!

				BoardFileEntity boardFileEntity =
						BoardFileEntity.toBoardFileEntity(board, originalFileName, storedFileName);
				boardFileRepository.save(boardFileEntity); // 7
			}
		}
	}
	
	// JPA가 자동생성한 리포지토리 메소드는 이미 트랜잭션 범위 내에서 실행하므로 
	// @Transactional 어노테이션이 굳이 필요하지 않다.. 고 했는데 붙이자!!
	@Transactional
	public List<BoardDTO> findAll() {
		// 리포지토리에서는 무조건 엔티티 형태로 넘어온다.
		// 서비스는 엔티티로 넘어온 객체를 dto 객체로 옮겨담아서 컨트롤러로 넘겨줘야!
		List<BoardEntity> boardEntityList = boardRepository.findAll();
		List<BoardDTO> boardDTOList = new ArrayList<>();
		for(BoardEntity boardEntity:boardEntityList) {
			boardDTOList.add(BoardDTO.toBoardDTO(boardEntity));
		}
		return boardDTOList;
	}

	// 리포지토리가 자동 생성한 메소드가 아니라 직접 생성한 쿼리를 활용한 메소드를 사용할 때!
	// @Transactional을 사용하지 않으면 에러남! 
	// 해당 메소드 내에서 트랜잭션을 시작하고 종료하도록 하는 어노테이션!
	// -> 데이터의 일관성과 ACID 특정 유지 위한 중요한 요소!
	@Transactional
	public void updateHits(Long id) {
		boardRepository.updateHits(id);
	}

	@Transactional
	public BoardDTO findById(Long id) {
		Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(id);
		if (optionalBoardEntity.isPresent()) {
			BoardEntity boardEntity = optionalBoardEntity.get();
			BoardDTO boardDTO = BoardDTO.toBoardDTO(boardEntity);
			return boardDTO;
		} else {
			return null;
		}
	}

	public BoardDTO update(BoardDTO boardDTO) {
		// JPA는 update를 위한 메소드를 별도 제공하고 있지 않기 때문에,
		// 엔티티에 기존 id값을 부여하는 메소드를 하나 더 만들고 save()를 활용해서 작업한다.
		BoardEntity boardEntity = BoardEntity.toUpdateEntity(boardDTO);
		boardRepository.save(boardEntity);
		// 리포지토리에서 findById하면 optional로 가져와서 귀찮으니까
		// 같은 서비스 클래스의 findById()를 활용해줌!
		return findById(boardDTO.getId());
	}

	public void delete(Long id) {
		boardRepository.deleteById(id);
	}

	public Page<BoardDTO> paging(Pageable pageable) {
		// 요청한 페이지 번호 (PageRequest에 매개변수로 넣어주기 위해 -1 해줌)
		int page = pageable.getPageNumber() - 1;
		// 한 페이지에 보여줄 글 개수
		int pageLimit = 3;
		// 페이징 처리된 데이터 처리!
		// 한 페이지당 3개씩 글을 보여주고 정렬 기준은 id 기준으로 내림차순 정렬
		// page 자리의 값은 0부터 시작하기 때문에, 위에서 page 변수 선언할 때 -1을 해줬다.
		Page<BoardEntity> boardEntities = 
				boardRepository.findAll(
						PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));
		
		System.out.println("boardEntities.getContent() = " + boardEntities.getContent()); // 요청 페이지에 해당하는 글
        System.out.println("boardEntities.getTotalElements() = " + boardEntities.getTotalElements()); // 전체 글갯수
        System.out.println("boardEntities.getNumber() = " + boardEntities.getNumber()); // DB로 요청한 페이지 번호
        System.out.println("boardEntities.getTotalPages() = " + boardEntities.getTotalPages()); // 전체 페이지 갯수
        System.out.println("boardEntities.getSize() = " + boardEntities.getSize()); // 한 페이지에 보여지는 글 갯수
        System.out.println("boardEntities.hasPrevious() = " + boardEntities.hasPrevious()); // 이전 페이지 존재 여부
        System.out.println("boardEntities.isFirst() = " + boardEntities.isFirst()); // 첫 페이지 여부
        System.out.println("boardEntities.isLast() = " + boardEntities.isLast()); // 마지막 페이지 여부
        
        // Page 객체가 제공해주는 메서드 map() 활용; forEach()처럼 하나씩 꺼내서 처리 가능
        // 목록 : id, writer, title, hits, createdTime
        Page<BoardDTO> boardDTOs = boardEntities.map(
        		board -> new BoardDTO(
        				board.getId(), board.getBoardWriter(), 
        				board.getBoardTitle(), board.getBoardHits(), 
        				board.getCreatedTime()));
		return boardDTOs;
	}
	
}
