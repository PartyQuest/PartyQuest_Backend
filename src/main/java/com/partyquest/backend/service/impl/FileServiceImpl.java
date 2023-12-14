package com.partyquest.backend.service.impl;

import com.partyquest.backend.domain.entity.File;
import com.partyquest.backend.domain.entity.Party;
import com.partyquest.backend.domain.entity.User;
import com.partyquest.backend.domain.repository.FileRepository;
import com.partyquest.backend.domain.type.FileType;
import com.partyquest.backend.domain.type.FileUploadType;
import com.partyquest.backend.service.impl.factory.file.FileFactory;
import com.partyquest.backend.service.impl.factory.file.PartyFileFactory;
import com.partyquest.backend.service.impl.factory.file.UserFileFactory;
import com.partyquest.backend.service.logic.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    @Autowired
    public FileServiceImpl(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public void fileMetaDataUpload(FileUploadType uploadType, Object data, String fileName) {
        FileFactory fileFactory = null;
        if(uploadType.equals(FileUploadType.PARTY)) {
            fileFactory = new PartyFileFactory();
        } else if(uploadType.equals(FileUploadType.USER)) {
            fileFactory = new UserFileFactory();
        }

        if(fileFactory != null) {
            File file = fileFactory.createFile(fileName,data);
            file = fileRepository.save(file);
            fileFactory.addFileToObject(data,file);
        }
    }
}
