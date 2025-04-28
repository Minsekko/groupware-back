package org.codenova.groupware.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class AddEmployee {
    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @PastOrPresent
    private LocalDate hireDate;  //입사예정자 data를 못넣게 하기 위해서

    private String position;

    @NotNull
    private Integer departmentId;
}
