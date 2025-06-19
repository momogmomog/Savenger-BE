package com.momo.savanger.api.revision;

public interface RevisionService {

    Revision findById(Long id);

    Revision create(CreateRevisionDto dto);

}
