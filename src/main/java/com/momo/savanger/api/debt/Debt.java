package com.momo.savanger.api.debt;

import com.momo.savanger.api.budget.Budget;
import com.momo.savanger.api.common.model.Audit;
import com.momo.savanger.constants.EntityGraphs;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "debts")
@Getter
@Setter
@ToString
@NamedEntityGraph(name = EntityGraphs.DEBT_ALL, attributeNodes = {
        @NamedAttributeNode("receiverBudget"),
        @NamedAttributeNode("lenderBudget"),
})
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

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiverBudgetId", insertable = false, updatable = false)
    private Budget receiverBudget;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lenderBudgetId", insertable = false, updatable = false)
    private Budget lenderBudget;

}
