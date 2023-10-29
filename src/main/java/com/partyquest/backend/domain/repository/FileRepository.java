package com.partyquest.backend.domain.repository;

import com.partyquest.backend.domain.entity.File;
import com.partyquest.backend.domain.spec.dsl.FileRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File,Long>, FileRepositoryCustom {
}
