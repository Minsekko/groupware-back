package org.codenova.groupware.repository;

import org.codenova.groupware.entity.Serial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SerialRepository extends JpaRepository<Serial, Integer> {
    public Optional<Serial> findByRef(String ref); //정해져있는 패턴을 가지고 JPA 에서 만들기 때문에 이용해서 사용해야한다

}
