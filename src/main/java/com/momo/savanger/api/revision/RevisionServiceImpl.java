package com.momo.savanger.api.revision;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RevisionServiceImpl implements RevisionService {

    private final RevisionRepository revisionRepository;

    @Override
    public Revision findById(Long id) {
        return this.revisionRepository.findById(id).orElse(null);
    }
}
