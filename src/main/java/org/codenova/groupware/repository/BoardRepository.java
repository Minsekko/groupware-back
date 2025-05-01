package org.codenova.groupware.repository;

import org.codenova.groupware.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> { //기본 CRUD는 된다
}
