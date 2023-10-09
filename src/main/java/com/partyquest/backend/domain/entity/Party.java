package com.partyquest.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity(name = "tb_party")
public class Party {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    private String accessCode;
    @Column
    private String title;
    @Column
    private String description;
    @Column
    private int capabilities;
    @Column
    private Boolean isPublic;

    @OneToMany(mappedBy = "party", orphanRemoval = true)
    private Set<UserParty> userParties = new LinkedHashSet<>();

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private User partyMaster;

    @OneToMany(mappedBy = "party", orphanRemoval = true)
    private Set<File> files = new LinkedHashSet<>();

}
