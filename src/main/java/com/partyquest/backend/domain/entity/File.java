package com.partyquest.backend.domain.entity;

import com.partyquest.backend.domain.type.FileType;
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
    @Enumerated(EnumType.STRING)
    private FileType type;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id")
    private Party party;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
