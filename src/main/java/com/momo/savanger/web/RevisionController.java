package com.momo.savanger.web;

import com.momo.savanger.api.revision.CreateRevisionDto;
import com.momo.savanger.api.revision.RevisionDto;
import com.momo.savanger.api.revision.RevisionMapper;
import com.momo.savanger.api.revision.RevisionService;
import com.momo.savanger.constants.Endpoints;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@PreAuthorize("isFullyAuthenticated()")
public class RevisionController {

    private final RevisionService revisionService;

    private final RevisionMapper revisionMapper;

    @PostMapping(Endpoints.REVISIONS)
    public RevisionDto create(@Valid @RequestBody CreateRevisionDto dto) {

        return this.revisionMapper.toRevisionDto(this.revisionService.create(dto));
    }
}
