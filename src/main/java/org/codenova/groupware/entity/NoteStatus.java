package org.codenova.groupware.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="note_status")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Employee receiver;

    @ManyToOne
    private Note note;

    private Boolean isRead;

    private LocalDateTime readAt;

    private Boolean isDelete;

}
