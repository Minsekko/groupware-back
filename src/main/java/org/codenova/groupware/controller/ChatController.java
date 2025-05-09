package org.codenova.groupware.controller;

import jakarta.validation.Valid;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codenova.groupware.entity.Chat;
import org.codenova.groupware.entity.Department;
import org.codenova.groupware.entity.Employee;
import org.codenova.groupware.repository.ChatRepository;
import org.codenova.groupware.repository.DepartmentRepository;
import org.codenova.groupware.repository.EmployeeRepository;
import org.codenova.groupware.request.AddChat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin
@RequiredArgsConstructor
@Builder
@Slf4j
public class ChatController {
    private final DepartmentRepository departmentRepository;
    private final ChatRepository chatRepository;
    private final EmployeeRepository employeeRepository;
    private final SimpMessagingTemplate messagingTemplate;  //메세지 템플릿 사용하기 위해서 와이어링 걸어준다

    @PostMapping("/{departmentId}")
    public ResponseEntity<?> postChatHandle(@RequestAttribute String subject,
                                            @PathVariable Integer departmentId,
                                            @RequestBody @Valid AddChat addChat,
                                            BindingResult result){
        Employee subjectEmployee = employeeRepository.findById(subject).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        Department department = departmentRepository.findById(departmentId).orElseThrow(); //department에 id로 조회한 부서 코드를 가져온다

        Chat chat = Chat.builder()
                .talker(subjectEmployee)
                .message(addChat.getMessage())
                .department(department)
                .build();
        chatRepository.save(chat);
        log.info("새 채팅 등록 요청 처리 완료");

        messagingTemplate.convertAndSend("/chat-department/"+departmentId,"newChat");
        return ResponseEntity.status(201).body(chat);
    }

    @GetMapping("/{departmentId}")
    public ResponseEntity<?> getChatHandle(@PathVariable Integer departmentId) {
        Department department = departmentRepository.findById(departmentId).orElseThrow();

        List<Chat> chatList = chatRepository.findAllByDepartmentOrderById(department);

        return ResponseEntity.status(200).body(chatList);
    }
}
