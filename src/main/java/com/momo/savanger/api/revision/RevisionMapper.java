package com.momo.savanger.api.revision;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RevisionMapper {

    Revision toRevision(CreateRevisionDto createRevisionDto);

    RevisionDto toRevisionDto(Revision revision);

}
