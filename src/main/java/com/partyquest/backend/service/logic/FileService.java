package com.partyquest.backend.service.logic;

import com.partyquest.backend.domain.type.FileUploadType;

public interface FileService {
    /**
     * 파일 메타데이터를 데이터베이스에 저장하는 Method
     * @param uploadType 파일 저장하는 업로드 타입, 연관관계 타입에 따라 다른 플로우로 진행
     * @param data 파일 메타데이터와 연관관계를 가지는 데이터
     * @param fileName 파일 이름
     */
    void fileMetaDataUpload(FileUploadType uploadType, Object data, String fileName);
}
