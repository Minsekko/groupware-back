package org.codenova.groupware.controller;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.codenova.groupware.entity.Department;
import org.codenova.groupware.repository.DepartmentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin //외부에서 접속하면 붙여 줘야하는 어노테이션
@RestController //Resonsebody 안붙여도 된다
@RequestMapping("/api/department")
@RequiredArgsConstructor  //변수만 생성하고 사용하는 생성자가 필요해서 (final생성일때) AllArgs도 가능하다
public class DepartmentController {

    private final DepartmentRepository departmentRepository;  //final 붙이는 이유는 생성자를 받아야 하는 입장일때 사용한다
    @GetMapping
    public ResponseEntity<List<Department>> getDepartmentHandle(){  //ResponseEntity 상태코드도 확인해야 하기 때문데 ResponseEntity 감싸서 넣어야 한다
        List<Department> list = departmentRepository.findAll();
        return ResponseEntity.status(200).body(list); //HTTP 응답 상태 코드 상태코드 설정해서 보내야 한다 200은 ok값
    }
}
