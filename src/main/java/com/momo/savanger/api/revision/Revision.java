package com.momo.savanger.api.revision;

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
@Table(name = "revisions")
@Getter
@Setter
@ToString
public class Revision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime revisionDate;

    @Column(nullable = false)
    private LocalDateTime budgetStartDate;

    @Column(nullable = false)
    private BigDecimal balance;

    private BigDecimal budgetCap;

    @Column(nullable = false)
    private BigDecimal expensesAmount;

    @Column(nullable = false)
    private BigDecimal earningsAmount;

    private BigDecimal debtLendedAmount;

    private BigDecimal debtReceivedAmount;

    private BigDecimal compensationAmount;

    @Column(nullable = false)
    private Boolean autoRevise;

    private String comment;

    private Long budgetId;

}
