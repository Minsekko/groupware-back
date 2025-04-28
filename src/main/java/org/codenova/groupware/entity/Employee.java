package org.codenova.groupware.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDate;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    private String id;
    private String password;
    private String name;
    private String email;
    private LocalDate hireDate;

    @ManyToOne //관계설정 어노테이션 다대일
    @JoinColumn(name="department_id") //DB상 department_id 컬럼
    private Department department;

    private String position;

    private String active;

}
