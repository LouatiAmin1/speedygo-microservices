package tn.esprit.se5.financial.services;



import tn.esprit.se5.financial.dto.PaymentRequestDTO;
import tn.esprit.se5.financial.entities.Payment;
import tn.esprit.se5.financial.entities.PaymentRequest;

import java.util.List;

public interface IPaymentService {
    Payment createPayment(Payment payment, String sourceId);
    List<Payment> getAllPayments();
    Payment getPaymentById(Integer id);
    void deletePayment(Integer id);
    //Payment assignOrCreatePaymentToTrip(Integer tripId, Payment paymentDetails);
   // Payment assignParcelToPayment(Integer paymentId, Integer parcelId);
    Payment processTestPayment(PaymentRequest paymentRequest);
    String createPaymentIntent(PaymentRequestDTO request) throws com.stripe.exception.StripeException;
    Payment processPayment(PaymentRequestDTO request);
   // List<Payment> getUserPaymentHistory(Integer userId); // Ensure this is present
}