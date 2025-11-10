package tn.esprit.se5.financial.controllers;

import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import tn.esprit.se5.financial.dto.CommissionDTO;
import tn.esprit.se5.financial.dto.PaymentRequestDTO;
import tn.esprit.se5.financial.entities.Commission;
import tn.esprit.se5.financial.entities.Partners;
import tn.esprit.se5.financial.entities.Payment;
import tn.esprit.se5.financial.services.CommissionService;
import tn.esprit.se5.financial.services.PartnersService;
import tn.esprit.se5.financial.services.PaymentService;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/finance")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class FinanceController {

    private final PaymentService paymentService;
    private final CommissionService commissionService;
    private final PartnersService partnersService;

//payment

    @GetMapping("/payments")
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/payments/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Integer id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @PostMapping("/payments/process")
    public ResponseEntity<?> processPayment(
            @Valid @RequestBody PaymentRequestDTO paymentRequest,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getAllErrors().forEach(error ->
                    errors.put(error.getObjectName(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            Payment payment = paymentService.processPayment(paymentRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(payment);
        } catch (Exception e) {
            log.error("Payment processing failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/payments/create-intent")
    public ResponseEntity<?> createPaymentIntent(
            @Valid @RequestBody PaymentRequestDTO paymentRequest,
            BindingResult bindingResult) {
        try {
            String clientSecret = paymentService.createPaymentIntent(paymentRequest);
            return ResponseEntity.ok(Map.of("clientSecret", clientSecret));
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

   // @GetMapping("/payments/user/{userId}/history")
    //public ResponseEntity<List<Payment>> getUserPaymentHistory(@PathVariable Integer userId) {
      //  return ResponseEntity.ok(paymentService.getUserPaymentHistory(userId));
   // }

//commission

    @GetMapping("/commissions")
    public List<CommissionDTO> getAllCommissions() {
        return commissionService.getAllCommissions();
    }

    @GetMapping("/commissions/partner/{partnerId}")
    public List<CommissionDTO> getCommissionsByPartner(@PathVariable Integer partnerId) {
        return commissionService.getCommissionsForPartner(partnerId);
    }

    @GetMapping("/commissions/partner/{partnerId}/summary")
    public Map<String, BigDecimal> getCommissionSummary(@PathVariable Integer partnerId) {
        return commissionService.getCommissionSummary(partnerId);
    }

    @PostMapping("/commissions")
    public Commission createCommission(@RequestBody Commission commission) {
        return commissionService.createCommission(
                commission.getPartnerId(),
                commission.getAmount(),
                commission.getDescription()
        );
    }

    @PatchMapping("/commissions/{commissionId}/status")
    public Commission updateCommissionStatus(
            @PathVariable Integer commissionId,
            @RequestBody Map<String, Boolean> request) {
        Commission existing = commissionService.getById(commissionId);
        existing.setPaidOut(request.get("paidOut"));
        existing.setUpdatedAt(LocalDateTime.now());
        return commissionService.save(existing);
    }

 //partner

    @PostMapping("/partners")
    public ResponseEntity<Partners> createPartner(@RequestBody Partners partner) {
        return ResponseEntity.ok(partnersService.createPartner(partner));
    }

    @GetMapping("/partners")
    public ResponseEntity<List<Partners>> getAllPartners() {
        return ResponseEntity.ok(partnersService.getAllPartners());
    }

    @GetMapping("/partners/{id}")
    public ResponseEntity<Partners> getPartnerById(@PathVariable Integer id) {
        return ResponseEntity.ok(partnersService.getPartnerById(id));
    }

    @PutMapping("/partners/{id}")
    public ResponseEntity<Partners> updatePartner(@PathVariable Integer id, @RequestBody Partners partner) {
        partner.setPartnerId(id);
        return ResponseEntity.ok(partnersService.updatePartner(partner));
    }

    @DeleteMapping("/partners/{id}")
    public ResponseEntity<Void> deletePartner(@PathVariable Integer id) {
        partnersService.deletePartner(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/partners/{partnerId}/check-promotion")
    public ResponseEntity<?> checkPartnerPromotion(@PathVariable Integer partnerId) {
        Partners updatedPartner = partnersService.checkAndPromotePartner(partnerId);
        String promotionTier = updatedPartner.getPromotions() != null
                ? updatedPartner.getPromotions().getPromotionTitle()
                : "No Promotion";

        return ResponseEntity.ok(Map.of(
                "partner", updatedPartner,
                "newCommissionRate", updatedPartner.getCommissionRate(),
                "promotionTier", promotionTier
        ));
    }

 //dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getFinanceDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("totalPayments", paymentService.getAllPayments().size());
        dashboard.put("totalCommissions", commissionService.getAllCommissions().size());
        dashboard.put("totalPartners", partnersService.getAllPartners().size());
        dashboard.put("monthlyRevenue", partnersService.getMonthlyPaymentRevenue());
        return ResponseEntity.ok(dashboard);
    }
}
