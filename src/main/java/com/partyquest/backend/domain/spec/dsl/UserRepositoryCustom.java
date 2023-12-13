package com.partyquest.backend.domain.spec.dsl;

import com.partyquest.backend.domain.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryCustom {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndPassword(String email, String password);

    boolean isUser(List<Long> userID);
    boolean updateIsDeleteFromUserID(Long userID);

}
