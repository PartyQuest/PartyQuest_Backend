package com.partyquest.backend.domain.repository;

import com.partyquest.backend.domain.entity.User;
import com.partyquest.backend.domain.spec.dsl.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long>, UserRepositoryCustom {
//    Optional<User> findByEmail(String email);
//    Optional<User> findByEmailAndPassword(String email, String password);
}
