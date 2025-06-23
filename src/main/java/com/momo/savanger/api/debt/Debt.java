package com.momo.savanger.api.debt;

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
@Table(name = "debts")
@Getter
@Setter
@ToString
public class Debt extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private Long receiverBudgetId;

    @Column(nullable = false)
    private Long lenderBudgetId;

    @Column(nullable = false)
    private BigDecimal amount;

}
