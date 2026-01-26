package com.momo.savanger.api.transaction;

import com.momo.savanger.api.budget.Budget;
import com.momo.savanger.api.category.Category;
import com.momo.savanger.api.tag.Tag;
import com.momo.savanger.api.user.User;
import com.momo.savanger.constants.EntityGraphs;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@ToString
@NamedEntityGraph(name = EntityGraphs.TRANSACTION_DETAILED, attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("user"),
        @NamedAttributeNode("category"),
})
@NamedEntityGraph(name = EntityGraphs.TRANSACTION_TAGS, attributeNodes = {
        @NamedAttributeNode("tags"),
})
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true, updatable = false)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime dateCreated;

    private String comment;

    @Column(nullable = false)
    private Boolean revised;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private User user;

    private Long categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", insertable = false, updatable = false)
    private Category category;

    @Column(nullable = false)
    private Long budgetId;

    private Long debtId;

    private Long prepaymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budgetId", insertable = false, updatable = false)
    private Budget budget;

    @Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "transactions_tags",
            joinColumns = @JoinColumn(name = "transaction_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id"))
    private List<Tag> tags;

}
