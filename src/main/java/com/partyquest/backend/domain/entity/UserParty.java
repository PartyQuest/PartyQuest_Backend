package com.partyquest.backend.domain.entity;

import com.partyquest.backend.domain.type.PartyMemberType;
import com.sun.istack.NotNull;
import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity(name = "tb_user_party")
public class UserParty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    @Enumerated(EnumType.STRING)
    PartyMemberType memberGrade;

    @Column
    private boolean partyAdmin;

    @Column
    private boolean registered;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "party_id")
    private Party party;


}
