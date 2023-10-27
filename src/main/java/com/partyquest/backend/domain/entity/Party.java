package com.partyquest.backend.domain.entity;

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
    private List<UserParty> userParties = new LinkedList<>();

    @OneToMany(mappedBy = "party", orphanRemoval = true)
    private List<File> files = new LinkedList<>();

}
