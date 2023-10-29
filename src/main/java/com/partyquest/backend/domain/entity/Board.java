package com.partyquest.backend.domain.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity(name = "tb_board")
public class Board extends DataCheck{
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

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @OneToMany(mappedBy = "board", orphanRemoval = true)
    private List<Board> boards = new LinkedList<>();

    @OneToMany(mappedBy = "board", orphanRemoval = true)
    private List<File> files = new LinkedList<>();

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "writer_id")
    private User writer;

}
