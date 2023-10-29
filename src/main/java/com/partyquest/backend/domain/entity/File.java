package com.partyquest.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity(name = "tb_file")
public class File extends DataCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    private String type;
    @Column
    private String fileAttachChngName;
    @Column
    private String filePath;
    @Column
    private String fileOriginalName;
    @Column
    private long fileSize;
    @Column
    private String errMsg;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne
    @JoinColumn(name = "party_id")
    private Party party;

}
