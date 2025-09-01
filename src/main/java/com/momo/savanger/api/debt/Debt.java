package com.momo.savanger.api.debt;

import com.momo.savanger.api.common.model.Audit;
import com.momo.savanger.constants.EntityGraphs;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "debts")
@Getter
@Setter
@ToString
@NamedEntityGraph(name = EntityGraphs.DEBT_ALL, includeAllAttributes = true)
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
