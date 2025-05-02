package org.codenova.groupware.controller;

import jakarta.persistence.Id;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codenova.groupware.entity.Board;
import org.codenova.groupware.entity.Employee;
import org.codenova.groupware.repository.BoardRepository;
import org.codenova.groupware.repository.EmployeeRepository;
import org.codenova.groupware.request.AddBoard;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.Writer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/board")
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardRepository boardRepository;
    private final EmployeeRepository employeeRepository;

    @GetMapping
    public ResponseEntity<List<Board>> getBoardHandle(){
        List<Board> list = boardRepository.findAll(Sort.by("id").descending());
        return ResponseEntity.status(200).body(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Board> getBoardHandle(@PathVariable Long id){
        Optional<Board> board = boardRepository.findById(id);
        if(board.isEmpty()){
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.status(200).body(board.get());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> postBoardHandle(@RequestAttribute String subject,@RequestBody @Valid
                                             AddBoard addBoard, BindingResult result){

        if (result.hasErrors()) {
            return ResponseEntity.status(400).body("입력 값 오류");
        }
        Optional<Employee> WriterId = employeeRepository.findById(subject);
        if (WriterId.isEmpty()) {
            return ResponseEntity.status(400).body("유효하지 않은 작성자 ID 입니다.");
        }
        Board board = Board.builder()
                .writer(WriterId.get())
                .title(addBoard.getTitle())
                .content(addBoard.getContent())
                .viewCount(0)
                .wroteAt(LocalDateTime.now())
                .build();

        boardRepository.save(board);
        return ResponseEntity.status(201).body(board);
    }
}
