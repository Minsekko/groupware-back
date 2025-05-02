package org.codenova.groupware.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codenova.groupware.entity.Board;
import org.codenova.groupware.entity.Employee;
import org.codenova.groupware.entity.Note;
import org.codenova.groupware.entity.NoteStatus;
import org.codenova.groupware.repository.EmployeeRepository;
import org.codenova.groupware.repository.NoteRepository;
import org.codenova.groupware.repository.NoteStatusRepository;
import org.codenova.groupware.request.AddNote;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/note")
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
public class NoteController {
    private final NoteRepository noteRepository;
    private final EmployeeRepository employeeRepository;
    private final NoteStatusRepository noteStatusRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<?> postNoteHandle(@RequestAttribute String subject,
                                            @RequestBody @Valid AddNote addNote, BindingResult result) {
        if (result.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "필수인자누락(내용필수, 최소 1명 이상 수신자 설정 필수");
        }

        Employee subjectEmployee = employeeRepository.findById(subject).orElseThrow(() -> {
            return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "미인증 사원");
        });

        Note note = Note.builder()
                .content(addNote.getContent())
                .sendAt(LocalDateTime.now())
                .isDelete(false)
                .sender(subjectEmployee)
                .build();
        noteRepository.save(note);

        List<Employee> receivers = employeeRepository.findAllById(addNote.getReceiverIds());

        List<NoteStatus> noteStatus = receivers.stream().map((item)-> {
            return NoteStatus.builder().note(note).isRead(false).receiver(item).isDelete(false).build();
        }).toList();
        noteStatusRepository.saveAll(noteStatus);

        return ResponseEntity.status(203).body(null);
    }

    @GetMapping("/receive")
    public ResponseEntity<?> getReceiveNote(@RequestAttribute String subject){
        //noteStatus 들중에 receiver 가 현재 로그인 하고 있는 사용자로 되어 있는 데이터만 가져와야 함.
        Employee subjectEmployee = employeeRepository.findById(subject).orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"미인증 상태"));
//        NoteStatus example = NoteStatus.builder().receiver(subjectEmployee).build();
//        List<NoteStatus> noteStatusList = noteStatusRepository.findAll(Example.of(example));

        List<NoteStatus> noteStatusList = noteStatusRepository.findAllByReceiver(subjectEmployee);

        return ResponseEntity.status(200).body(noteStatusList);
    }
}
