package com.momo.savanger.api.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category findById(Long id) {
        return this.categoryRepository.findById(id).orElse(null);
    }
}
