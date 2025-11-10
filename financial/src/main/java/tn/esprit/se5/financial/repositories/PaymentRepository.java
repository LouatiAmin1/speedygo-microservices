package tn.esprit.se5.financial.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.se5.financial.entities.Partners;
import tn.esprit.se5.financial.entities.Payment;

import java.util.Date;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByCommissionCalculatedFalse();
    List<Payment> findAllByOrderByPaymentDateDesc();

    //@Query("SELECT p FROM Payment p WHERE p.user = :user ORDER BY p.paymentDate DESC")
   // List<Payment> findByUserOrderByPaymentDateDesc(@Param("user") User user);
    List<Payment> findByPartner(Partners partner);
    List<Payment> findByPaymentDateBetween(Date startDate, Date endDate);
    //List<Payment> findByUser(SimpleUser user);

   // Payment findByParcelParcelId(Integer parcelId);
}
