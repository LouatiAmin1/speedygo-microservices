package tn.esprit.se5.financial.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paymentId;

    private Date paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private tn.esprit.se5.financial.entities.PaymentMethod paymentMethod;

    @Column(precision = 19, scale = 2)
    @NotNull
    private BigDecimal paymentAmount;

    private String stripeChargeId;



    @ManyToOne
    @JoinColumn(name = "partner_id")
    private Partners partner;


    @Column(name = "commission_calculated")
    private boolean commissionCalculated = false;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;
}