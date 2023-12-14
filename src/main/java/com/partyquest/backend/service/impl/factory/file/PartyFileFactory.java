package com.partyquest.backend.service.impl.factory.file;

import com.partyquest.backend.domain.entity.File;
import com.partyquest.backend.domain.entity.Party;
import com.partyquest.backend.domain.type.FileType;

public class PartyFileFactory extends FileFactory{
    @Override
    public File createFile(String fileName, Object data) {
        Party party = (Party) data;
        return File.builder()
                .fileName(fileName)
                .type(FileType.PARTY_THUMBNAIL)
                .party(party)
                .build();
    }

    @Override
    public void addFileToObject(Object data, File file) {
        if(data instanceof Party) ((Party) data).getFiles().add(file);
    }
}
