package com.momo.savanger.web;

import com.momo.savanger.api.prepayment.CreatePrepaymentDto;
import com.momo.savanger.api.prepayment.PrepaymentDto;
import com.momo.savanger.api.prepayment.PrepaymentMapper;
import com.momo.savanger.api.prepayment.PrepaymentQuery;
import com.momo.savanger.api.prepayment.PrepaymentService;
import com.momo.savanger.api.prepayment.constraints.ValidPrepayment;
import com.momo.savanger.constants.Endpoints;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@PreAuthorize("isFullyAuthenticated()")
public class PrepaymentController {

    private final PrepaymentService prepaymentService;

    private final PrepaymentMapper prepaymentMapper;

    @PostMapping(Endpoints.PREPAYMENTS)
    public PrepaymentDto create(@Valid @RequestBody CreatePrepaymentDto dto) {
        return this.prepaymentMapper.toPrepaymentDto(this.prepaymentService.create(dto));
    }

    @GetMapping(Endpoints.PREPAYMENT)
    public PrepaymentDto getPrepayment(
            @PathVariable @ValidPrepayment(allowCompleted = true) Long id) {
        return this.prepaymentMapper.toPrepaymentDto(
                this.prepaymentService.findById(id)
        );
    }

    @PutMapping(Endpoints.PREPAYMENT)
    public PrepaymentDto editPrepayment(
            @PathVariable @ValidPrepayment Long id,
            @Valid @RequestBody CreatePrepaymentDto dto) {
        return this.prepaymentMapper.toPrepaymentDto(
                this.prepaymentService.edit(id, dto)
        );
    }

    @PostMapping(Endpoints.PREPAYMENTS_SEARCH)
    public PagedModel<PrepaymentDto> searchPrepayments(
            @Valid @RequestBody PrepaymentQuery query) {
        return new PagedModel<>(
                this.prepaymentService
                        .search(query)
                        .map(this.prepaymentMapper::toPrepaymentDto)
        );
    }

}
