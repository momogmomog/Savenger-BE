package com.momo.savanger.web;

import com.momo.savanger.api.prepayment.CreatePrepaymentDto;
import com.momo.savanger.api.prepayment.PrepaymentDto;
import com.momo.savanger.api.prepayment.PrepaymentMapper;
import com.momo.savanger.api.prepayment.PrepaymentService;
import com.momo.savanger.constants.Endpoints;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@PreAuthorize("isFullyAuthenticated()")
public class PrepaymentController {

    private final PrepaymentService prepaymentService;

    private final PrepaymentMapper prepaymentMapper;

    @PostMapping(Endpoints.PREPAYMENTS)
    public PrepaymentDto create(@Valid @RequestBody CreatePrepaymentDto dto)
            throws InvalidRecurrenceRuleException {
        return this.prepaymentMapper.toPrepaymentDto(this.prepaymentService.create(dto));
    }

    @PostMapping(Endpoints.PAY_PREPAYMENT)
    public PrepaymentDto pay(@PathVariable Long id) throws InvalidRecurrenceRuleException {

        return this.prepaymentMapper.toPrepaymentDto(this.prepaymentService.pay(id));
    }

}
