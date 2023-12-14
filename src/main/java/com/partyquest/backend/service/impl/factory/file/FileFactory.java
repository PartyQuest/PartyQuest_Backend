package com.partyquest.backend.service.impl.factory.file;

import com.partyquest.backend.domain.entity.File;

public abstract class FileFactory {
    public abstract File createFile(String fileName, Object data);
    public abstract void addFileToObject(Object data, File file);
}
