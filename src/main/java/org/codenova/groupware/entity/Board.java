package org.codenova.groupware.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="board")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  //auto_increment이걸 표기
    private Long id;

    @ManyToOne
    //@JoinColumn(name="writer_id")
    private Employee writer; //join이 되야 하는 외래키라 employee에서 찾아서 사용하는 거라 writer_id부분을 writer빼고 적어주면 찾아서 연결 된다
    private String title;
    private String content;
    private Integer viewCount;

    private LocalDateTime wroteAt;
}
