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
@Entity(name = "tb_board")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;
    @Column
    private String title;
    @Column
    private String content;
    @Column
    private String type;
    @Column
    private Boolean deleteHide;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @OneToMany(mappedBy = "board", orphanRemoval = true)
    private Set<Board> boards = new LinkedHashSet<>();

    @OneToMany(mappedBy = "board", orphanRemoval = true)
    private Set<File> files = new LinkedHashSet<>();

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "writer_id")
    private User writer;

}
