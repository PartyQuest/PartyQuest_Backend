package com.partyquest.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity(name = "tb_party")
public class Party extends DataCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
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

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserParty> userParties = new java.util.ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "party", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 100)
    private List<File> files = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Quest> quests = new ArrayList<>();

    public void setIsDeleted(boolean isDeleted) {
        super.setIsDelete(isDeleted);
    }

}
