package com.practice.controller;

import java.io.IOException;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.practice.dto.BoardDTO;
import com.practice.service.BoardService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@Slf4j
// "/board" 라는 주소로 먼저 받고 각 메소드는 그 하위의 경로로 이어진다!
@RequestMapping("/board")
public class BoardController {
	
	// 생성자 생성 방식으로 의존성 주입!
	private final BoardService boardService;
	
	@GetMapping("/save") 
	public String saveForm() {
		return "save";
	}
	
	@PostMapping("/save")
	public String save(@ModelAttribute BoardDTO boardDTO) throws IOException {
		System.out.println("boardDTO = " + boardDTO);
		boardService.save(boardDTO);
		return "index";
	}
	
	@GetMapping("/")
	// Model : 데이터를 담아가는 객체
	public String findAll(Model model) {
		// DB에서 전체 게시글 데이터를 가져와서 list.html에 보여준다.
		List<BoardDTO> boardDTOList = boardService.findAll();
		model.addAttribute("boardList", boardDTOList);
		return "list";
	}
	
	@GetMapping("/{id}")
	// 경로 상에 있는 값을 가져올 때 사용하는 어노테이션!
	// 페이지 값이 없는 경우를 위해서 default 값을 1로 넣었다.
	public String findById(@PathVariable Long id, Model model,
						   @PageableDefault(page=1) Pageable pageable) {
		/*
		 * 해당 게시물에 조회수를 하나 올리고
		 * 게시글 데이터를 가져와서 detail.html에 출력하기!
		 */
		boardService.updateHits(id);
		BoardDTO boardDTO = boardService.findById(id);
		model.addAttribute("board", boardDTO);
		model.addAttribute("page", pageable.getPageNumber());
		return "detail";
	}
	
	@GetMapping("/update/{id}")
	public String updateForm(@PathVariable Long id, Model model) {
		BoardDTO boardDTO = boardService.findById(id);
		model.addAttribute("boardUpdate", boardDTO);
		return "update";
	}
	
	@PostMapping("/update")
	public String updateById(@ModelAttribute BoardDTO boardDTO, Model model) {
		BoardDTO board = boardService.update(boardDTO);
		model.addAttribute("board", board);
		// 수정 뒤 수정이 반영된 상세페이지를 보여주고 있음.
		return "detail";
		// return "redirect:/board/" + boardDTO.getId(); -> 조회수 반영되버리기 때문에 이렇게 안쓴다.
	}
	
	@GetMapping("/delete/{id}")
	public String deleteById(@PathVariable Long id) {
		boardService.delete(id);
		// 게시글 목록 페이지로 돌아감.
		return "redirect:/board/";
	}
	
	// /board/paging?page=1
	@GetMapping("/paging")
	// 페이징 처리할 때 유용한 어노테이션과 객체 
	// 한 페이지에 몇 개씩 보느냐에 따라서 몇 개의 페이지가 될지 바뀐다. 
	// Pageable 객체는 경로의 page라고 지정된 파라미터 값을 받아온다.(요청된 페이지 번호에 해당하는 데이터를 가져옴)
	// 경로에 ?page=[숫자] 없더라도 default로 1의 값을 가진다는 의미.
	public String paging(
			@PageableDefault(page = 1) Pageable pageable, Model model) {
//		pageable.getPageNumber();
		Page<BoardDTO> boardList = boardService.paging(pageable);
		// 화면 하단에 페이징 처리되어 한 번에 보이는 페이지 개수
		int blockLimit = 3;
		// blockLimit에서 가장 처음 보이는 숫자; 1 4 7 10 ...되도록 startPage 계산 방법
		// Math.ceil -> 올림! 
//		int startPage =
//				(((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1;
		// 내가 startPage로부터 계산한 endPage가 전체 페이지 수보다 크다면 
		// 계산된 endPage가 아닌 그냥 전체페이지수(마지막숫자)가 endPage가 되도록!
//		int endPage = ((startPage + blockLimit - 1) < boardList.getTotalPages()) ?
//				startPage + blockLimit - 1 : boardList.getTotalPages();

		/*
		현재 페이지 번호가 바뀌어도(1, 2, 3) startPage가 고정인 게(1) 만족스럽지 않아서 코드를 고쳤다.
		Math 클래스의 max()는 둘 중에 큰 숫자를, min()은 둘 중에 작은 숫자를 반환한다.
		 */
		int totalPages = boardList.getTotalPages();
		int currentPage = pageable.getPageNumber();
		//int startPage = Math.max(1, currentPage - (int)Math.floor(blockLimit / 2));
		// currentPage가 4일 때는 startPage가 2가 되어야 함...
		// currentPage가 1이면 1, totalPages 면 totalPage - blockLimit + 1
		int startPage;
		if (currentPage == 1) {
			startPage = 1;
		} else if (currentPage == totalPages) {
			startPage = totalPages - blockLimit + 1;
		} else {
			startPage = currentPage - (int)Math.floor(blockLimit / 2);
		}

		int endPage = Math.min(totalPages, startPage + blockLimit - 1);

		log.info("currentPage : " + currentPage);

		model.addAttribute("boardList", boardList);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		return "paging";
	}
}
