package org.codenova.groupware.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
//@Table(name="serial")  //엔티티명과Table명이 일치 하면 생략하면 된다
public class Serial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id") //컬럼명과 필드명이 일치하면 생략
    private Integer id;
    private String ref;
    //생략시엔 카멜필드같은 경우 자동으로 underscore 형태로 연결됨
    private Long lastNumber;
}
