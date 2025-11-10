package tn.esprit.se5.financial.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.se5.financial.entities.Promotions;


public interface PromotionsRepository extends JpaRepository<Promotions, Integer> {
}
