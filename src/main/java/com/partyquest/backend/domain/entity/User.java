package com.partyquest.backend.domain.entity;

import com.partyquest.backend.domain.type.LoginType;
import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity(name = "tb_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    private String email;
    @Column
    private String password;
    @Column
    private String nickname;
    @Column
    private String birth;
    @Column
    private String sns;


    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<UserParty> userParties = new LinkedList<>();

}
