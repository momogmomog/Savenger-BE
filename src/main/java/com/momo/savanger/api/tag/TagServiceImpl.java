package com.momo.savanger.api.tag;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public Tag findById(Long id) {
        return this.tagRepository.findById(id).orElse(null);
    }
}
