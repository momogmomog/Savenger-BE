package com.momo.savanger.api.prepayment;

import com.momo.savanger.api.common.model.Audit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "prepayments")
@Getter
@Setter
@ToString
public class Prepayment extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime paidUntil;

    @Column(nullable = false)
    private Boolean completed;

    @Column(nullable = false)
    private BigDecimal remainingAmount;

    @Column(nullable = false)
    private Long budgetId;

}
