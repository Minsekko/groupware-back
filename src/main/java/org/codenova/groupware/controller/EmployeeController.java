package org.codenova.groupware.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
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
import org.codenova.groupware.response.LoginResult;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.AlgorithmConstraints;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employee")
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {

    private final EmployeeRepository employeeRepository;
    private final SerialRepository serialRepository;
    private final DepartmentRepository departmentRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Value("${secret}") //springframwork 패키지의 value 어노테이션
    private String secret;

    @GetMapping
    public ResponseEntity<List<Employee>> getEmployeeHandle(){
        List<Employee> list = employeeRepository.findAll();
        return ResponseEntity.status(200).body(list);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Employee> postEmployeeHandle(@RequestBody @Valid  //@RequestBody json 형태를 받을때 써준다
                                                            AddEmployee addEmployee,
                                                        BindingResult result ){
        if(result.hasErrors()) {
            return ResponseEntity.status(400).body(null); // bad request : 서버가 클라이언트 오류를 감지해 요청을 처리할수 없는 코드
        }

        //1.사원번호 생성, 부서객체
        Optional<Serial> serial = serialRepository.findByRef("employee"); //jpa 만들때 optional만든다
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

    @PostMapping("/verify")
    public ResponseEntity<LoginResult> verifyHandle(@RequestBody @Valid LoginRequest loginRequest, BindingResult result) {

        if (result.hasErrors()){
            return ResponseEntity.status(400).body(null);
        }

        Optional<Employee> employeeOptional = employeeRepository.findById(loginRequest.getId());//password 수정중
        if(employeeOptional.isEmpty() || !BCrypt.checkpw(loginRequest.getPassword(), employeeOptional.get().getPassword())) {
            return ResponseEntity.status(401).body(null);
        }
        Employee employee = employeeOptional.get();

        String token = JWT.create().withIssuer("groupware") //토큰 발급처 - 프로젝트 이름
                .withSubject(employeeOptional.get().getId()) //토큰발부대상 - 로그인 승인자 아이디
                .sign(Algorithm.HMAC256(secret)); //위조변증에 사용될 알고리즘(암호키)

        LoginResult loginResult = LoginResult.builder().token(token).employee(employeeOptional.get()).build();
        simpMessagingTemplate.convertAndSend("/notice",employeeOptional.get().getId()+" 로그인 하였습니다.");
        return ResponseEntity.status(200).body(loginResult);
    }
}
