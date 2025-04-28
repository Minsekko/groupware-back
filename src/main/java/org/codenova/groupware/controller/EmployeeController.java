package org.codenova.groupware.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codenova.groupware.entity.Department;
import org.codenova.groupware.entity.Employee;
import org.codenova.groupware.entity.Serial;
import org.codenova.groupware.repository.DepartmentRepository;
import org.codenova.groupware.repository.EmployeeRepository;
import org.codenova.groupware.repository.SerialRepository;
import org.codenova.groupware.request.AddEmployee;
import org.codenova.groupware.request.LoginRequest;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employee")
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {
    private final EmployeeRepository employeeRepository;
    private final DepartmentController departmentController;
    private final SerialRepository serialRepository;
    private final DepartmentRepository departmentRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<Employee> postEmployeeHandle(@ModelAttribute @Valid
                                                            AddEmployee addEmployee,
                                                        BindingResult result ){
        if(result.hasErrors()) {
            return ResponseEntity.status(400).body(null); // bad request : 서버가 클라이언트 오류를 감지해 요청을 처리할수 없는 코드
        }

        //1.사원번호 생성, 부서객체
        Optional<Serial> serial = serialRepository.findById(1); //jpa 만들때 optional만든다
        //jpa에서 id로 찾는걸 기본으로 제공을 해주는데, 결과가 optional 객체가 나옴

        Optional<Department> department = departmentRepository.findById(addEmployee.getDepartmentId()); //부서에서 ID를 찾아 부서를 만들어줘야 해서 find해온다
        if(department.isEmpty()) {
            return ResponseEntity.status(400).body(null); //400으로 응답을 보내 버린다
        }

        //정상적으로 사용한다면 serial.isPresent() 이걸 확인해서 뽑아서 써야 됨.
        Serial found = serial.get();

        //2. 사원객체 생성 및 저장
        Employee employee = Employee.builder()
                .id("GW-" + (found.getLastNumber()+1))
                .password(BCrypt.hashpw("0000",BCrypt.gensalt()))
                .name(addEmployee.getName())
                .active("N")
                .email(addEmployee.getEmail())
                .hireDate(addEmployee.getHireDate())
                .position(addEmployee.getPosition())
                .department(department.get())
                .build();

        employeeRepository.save(employee);

        // 시리얼 테이블의 last_number 를 업데이트 쳐줘야 함.
        found.setLastNumber(found.getLastNumber()+1);
        serialRepository.save(found);  //jpa 수정할때 따른 메서드가 존재 하지 않고 알아서 update 명령문이 돌아간다

        return ResponseEntity.status(201).body(employee);  //201 create 응답 상태코드
    }

    @GetMapping("{id}")
    public ResponseEntity<Employee> getEmployeeHandle(@PathVariable String id){
        Optional<Employee> employee = employeeRepository.findById(id);
        if(employee.isEmpty()){
            return ResponseEntity.status(404).body(null); //Not found: 서버가 요청 받은 리소스를 찾을 수 없다는 것 의미
        }
        return ResponseEntity.status(200).body(employee.get());
    }

    @PostMapping("/login")
    public ResponseEntity<Employee> loginEmployeeHandle(@RequestBody LoginRequest loginRequest) {
        Optional<Employee> employeeOptional = employeeRepository.findById(loginRequest.getId());//password 수정중
        if(employeeOptional.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }

        Employee employee = employeeOptional.get();

        if(!employee.getPassword().equals(loginRequest.getPassword())) { //비밀번호가 로그인 리퀘스트 번호랑 일치 하지 않으면
            return ResponseEntity.status(401).body(null); //비밀번호 불일치시
        }
        return ResponseEntity.status(200).body(employee);
    }
}
