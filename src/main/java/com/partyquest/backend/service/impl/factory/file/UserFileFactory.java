package com.partyquest.backend.service.impl.factory.file;

import com.partyquest.backend.domain.entity.File;
import com.partyquest.backend.domain.entity.User;
import com.partyquest.backend.domain.type.FileType;

public class UserFileFactory extends FileFactory{
    @Override
    public File createFile(String fileName, Object data) {
        User user = (User) data;
        return File.builder()
                .type(FileType.USER_THUMBNAIL)
                .fileName(fileName)
                .user(user)
                .build();
    }

    @Override
    public void addFileToObject(Object data, File file) {
        if (data instanceof User) {
            ((User) data).getFiles().add(file);
        }
    }
}
