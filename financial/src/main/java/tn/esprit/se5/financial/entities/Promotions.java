package tn.esprit.se5.financial.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Promotions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer promotionId;
    private String promotionTitle;
    private String promotionDescription;
    private Float promotionDiscountPercentage;
    private Date promotionStartDate;
    private Date promotionEndDate;

    @OneToMany(mappedBy = "promotions", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("promotions")
    private Set<Partners> partnerses = new LinkedHashSet<>();

}