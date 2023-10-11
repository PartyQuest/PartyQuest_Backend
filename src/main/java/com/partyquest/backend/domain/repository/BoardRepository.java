package com.partyquest.backend.domain.repository;

import com.partyquest.backend.domain.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board,Long> {
}
