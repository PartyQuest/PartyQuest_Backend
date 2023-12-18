package com.partyquest.backend.domain.entity;

import com.partyquest.backend.domain.type.QuestType;
import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity(name = "tb_quest")
public class Quest extends DataCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    private String startTime;
    @Column
    private String endTime;
    @Column
    private String title;
    @Column
    private String description;
    @Column
    private Boolean deleteHide;

    @Column
    private Boolean complete;

    @Column
    @Enumerated(EnumType.STRING)
    private QuestType type;

    @ManyToOne
    @JoinColumn(name = "quest_id")
    private Quest quest;

    @OneToMany(mappedBy = "quest", orphanRemoval = true)
    private List<Quest> quests;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "party")
    private Party party;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

}
