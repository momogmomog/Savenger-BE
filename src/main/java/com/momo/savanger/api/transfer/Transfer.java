package com.momo.savanger.api.transfer;

import com.momo.savanger.api.budget.Budget;
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
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "transfers")
@Getter
@Setter
@NamedEntityGraph(name = EntityGraphs.TRANSFER_ALL, attributeNodes = {
        @NamedAttributeNode("receiverBudget"),
        @NamedAttributeNode("sourceBudget"),
})
@NamedEntityGraph(name = EntityGraphs.TRANSFER_RECEIVER_BUDGET, attributeNodes = {
        @NamedAttributeNode("receiverBudget")
})
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private Long sourceBudgetId;

    @Column(nullable = false)
    private Long receiverBudgetId;

    @Column(nullable = false)
    private Boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sourceBudgetId", insertable = false, updatable = false)
    private Budget sourceBudget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiverBudgetId", insertable = false, updatable = false)
    private Budget receiverBudget;
}
