package com.momo.savanger.api.transaction.recurring;

import com.momo.savanger.api.common.model.Audit;
import com.momo.savanger.api.tag.Tag;
import com.momo.savanger.api.transaction.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;

@Entity
@Table(name = "recurring_transactions")
@Getter
@Setter
@ToString
public class RecurringTransaction extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false)
    private String recurringRule;

    @Column(nullable = false)
    private LocalDateTime nextDate;

    @Column(nullable = false)
    private Boolean autoExecute;

    @Column(nullable = false)
    private BigDecimal amount;

    private Long prepaymentId;

    @Column(nullable = false)
    private Boolean completed;

    private Long categoryId;

    @Column(nullable = false)
    private Long budgetId;

    private Long debtId;

    @Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "recurring_transactions_tags",
            joinColumns = @JoinColumn(name = "recurring_transaction_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id"))
    private List<Tag> tags;

}
