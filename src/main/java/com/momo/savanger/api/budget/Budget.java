package com.momo.savanger.api.budget;

import com.momo.savanger.api.user.User;
import com.momo.savanger.constants.EntityGraphs;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
@Table(name = "budgets")
@Getter
@Setter
@ToString
@NamedEntityGraph(name = EntityGraphs.BUDGET_ALL, includeAllAttributes = true)
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String budgetName;

    @Column(nullable = false)
    private String recurringRule;

    @Column(nullable = false)
    private LocalDateTime dateStarted;

    @Column(nullable = false)
    private LocalDateTime dueDate;

    @Column(nullable = false)
    private Boolean active;

    @Column(nullable = false)
    private BigDecimal balance;

    private BigDecimal budgetCap;

    @Column(nullable = false)
    private Boolean autoRevise;

    @Column(nullable = false)
    private Long ownerId;

    @Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "budgets_participants",
            joinColumns = @JoinColumn(name = "budget_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private List<User> participants;

}
