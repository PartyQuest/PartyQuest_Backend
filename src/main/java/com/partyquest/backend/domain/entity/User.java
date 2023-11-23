package com.partyquest.backend.domain.entity;

import com.partyquest.backend.domain.type.LoginType;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity(name = "tb_user")
public class User extends DataCheck {
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
    private String sns;


    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<UserParty> userParties = new LinkedList<>();

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<DeviceToken> deviceTokens = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<File> files = new ArrayList<>();

}
