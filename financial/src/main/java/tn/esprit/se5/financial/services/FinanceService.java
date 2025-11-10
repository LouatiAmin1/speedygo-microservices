package tn.esprit.se5.financial.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import com.stripe.param.ChargeCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.se5.financial.dto.CommissionDTO;
import tn.esprit.se5.financial.dto.PaymentRequestDTO;
import tn.esprit.se5.financial.entities.Commission;
import tn.esprit.se5.financial.entities.Partners;
import tn.esprit.se5.financial.entities.Payment;
import tn.esprit.se5.financial.entities.Promotions;
import tn.esprit.se5.financial.repositories.CommissionRepository;
import tn.esprit.se5.financial.repositories.PartnersRepository;
import tn.esprit.se5.financial.repositories.PaymentRepository;
import tn.esprit.se5.financial.repositories.PromotionsRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinanceService {

    private final PaymentRepository paymentRepository;
    private final CommissionRepository commissionRepository;
    private final PartnersRepository partnersRepository;
    private final PromotionsRepository promotionsRepository;
    /*private final TripRepository tripRepository;
    private final ParcelRepository parcelRepository;
    private final SimpleUserRepository simpleUserRepository;
    private final UserRepository userRepository;*/

    private static final BigDecimal SILVER_THRESHOLD = new BigDecimal("10000.00");
    private static final BigDecimal GOLD_THRESHOLD = new BigDecimal("20000.00");
    private static final BigDecimal COMMISSION_INCREASE_SILVER = new BigDecimal("0.05");
    private static final BigDecimal COMMISSION_INCREASE_GOLD = new BigDecimal("0.05");

    @PostConstruct
    public void init() {
        Stripe.apiKey = "sk_test_51Qx4HqRtzrEMIcCe68S8vL8kuWICgv7rI2hga1OI7oDdcv9aRamQbSMmsYI5qLFG0oSWq9KyoblmZgL2TIAUuBMc00jPaF2SSB";
    }

    // ===================== PAYMENT LOGIC =====================

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Integer id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
    }

    public void deletePayment(Integer id) {
        paymentRepository.deleteById(id);
    }

    @Transactional
    public Payment processPayment(PaymentRequestDTO request) {
        log.info("Processing payment: {}, partnerId: {}", request, request.getPartnerId());

        if (request.getPaymentAmount() == null || request.getPaymentAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Payment amount must be positive");
        if (request.getPaymentMethod() == null)
            throw new IllegalArgumentException("Payment method is required");

        //   Trip trip = request.getTripId() != null
        //   ? tripRepository.findById(request.getTripId()).orElse(null) : null;

        //   Parcel parcel = request.getParcelId() != null
        // ? parcelRepository.findById(request.getParcelId()).orElse(null) : null;

        // SimpleUser user = request.getUserId() != null
        //    ? simpleUserRepository.findById(request.getUserId()).orElse(null) : null;

        // Partners partner = resolvePartnerForPayment(request, user);

        Payment payment = new Payment();
        payment.setPaymentAmount(request.getPaymentAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentDate(new Date());
        payment.setLastUpdated(new Date());
        payment.setStripeChargeId(request.getStripePaymentMethodId());
        // payment.setTrip(trip);
        // payment.setParcel(parcel);
        // payment.setPartner(partner);
        // payment.setUser(user);
        payment.setCommissionCalculated(false);

        Payment saved = paymentRepository.save(payment);
        log.info("✅ Payment processed: {}", saved.getPaymentId());
        return saved;
    }

   /* private Partners resolvePartnerForPayment(PaymentRequestDTO request, SimpleUser user) {
        Partners partner;
        if (request.getPartnerId() != null) {
            partner = partnersRepository.findById(request.getPartnerId())
                    .orElseThrow(() -> new RuntimeException("Partner not found with ID: " + request.getPartnerId()));
        } else if (user != null && user.getPartners() != null) {
            partner = user.getPartners();
        } else {
            partner = partnersRepository.findById(1)
                    .orElseThrow(() -> new RuntimeException("Default partner not found"));
        }
        return partner;
    }*/

    public String createPaymentIntent(PaymentRequestDTO request) throws StripeException {
        BigDecimal amount = request.getPaymentAmount();
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).longValueExact())
                .setCurrency("USD")
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build()
                )
                .build();

        PaymentIntent intent = PaymentIntent.create(params);
        return intent.getClientSecret();
    }

    // ===================== COMMISSION LOGIC =====================

    @Transactional
    public Commission createCommission(Integer partnerId, BigDecimal amount, String description) {
        Partners partner = partnersRepository.findById(partnerId)
                .orElseThrow(() -> new IllegalArgumentException("Partner not found with ID: " + partnerId));

        Commission commission = new Commission();
        commission.setPartner(partner);
        commission.setAmount(amount);
        commission.setCalculatedAt(LocalDateTime.now());
        commission.setPaidOut(false);
        commission.setDescription(description);

        partner.setTotalCommission(
                partner.getTotalCommission() == null ? amount : partner.getTotalCommission().add(amount)
        );
        partnersRepository.save(partner);
        return commissionRepository.save(commission);
    }

    @Scheduled(cron = "0 0 0 * * ?") // daily at midnight
    @Transactional
    public void calculateDailyCommissions() {
        log.info("Running daily commission calculation...");
        List<Payment> payments = paymentRepository.findByCommissionCalculatedFalse();

        for (Payment payment : payments) {
            Partners partner = payment.getPartner();
            if (partner == null || payment.getPaymentAmount() == null || partner.getCommissionRate() == null) {
                payment.setCommissionCalculated(true);
                paymentRepository.save(payment);
                continue;
            }

            BigDecimal commissionAmount = payment.getPaymentAmount()
                    .multiply(partner.getCommissionRate())
                    .setScale(2, RoundingMode.HALF_UP);

            Commission commission = new Commission();
            commission.setPartner(partner);
            commission.setPayment(payment);
            commission.setAmount(commissionAmount);
            commission.setCalculatedAt(LocalDateTime.now());
            commission.setPaidOut(false);
            commissionRepository.save(commission);

            partner.setTotalCommission(
                    partner.getTotalCommission() == null ? commissionAmount : partner.getTotalCommission().add(commissionAmount)
            );
            partnersRepository.save(partner);

            payment.setCommissionCalculated(true);
            paymentRepository.save(payment);
        }
        log.info("✅ Daily commission calculation completed");
    }

    public List<CommissionDTO> getCommissionsForPartner(Integer partnerId) {
        return commissionRepository.findByPartnerId(partnerId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CommissionDTO toDTO(Commission c) {
        CommissionDTO dto = new CommissionDTO();
        dto.setCommissionId(c.getCommissionId());
        dto.setPartnerId(c.getPartner() != null ? c.getPartner().getPartnerId() : null);
        dto.setPaymentId(c.getPayment() != null ? c.getPayment().getPaymentId() : null);
        dto.setAmount(c.getAmount());
        dto.setPaidOut(c.isPaidOut());
        dto.setDescription(c.getDescription());
        dto.setCalculatedAt(c.getCalculatedAt());
        dto.setUpdatedAt(c.getUpdatedAt());
        return dto;
    }

    // ===================== PARTNER / PROMOTION LOGIC =====================

    public Partners checkAndPromotePartner(Integer partnerId) {
        Partners partner = partnersRepository.findById(partnerId)
                .orElseThrow(() -> new RuntimeException("Partner not found"));
        BigDecimal total = partner.getTotalCommission();

        BigDecimal baseRate = partner.getCommissionRate() == null ? BigDecimal.ZERO : partner.getCommissionRate();
        Promotions promo = partner.getPromotions();

        if (total.compareTo(GOLD_THRESHOLD) >= 0) {
            partner.setCommissionRate(baseRate.add(COMMISSION_INCREASE_SILVER).add(COMMISSION_INCREASE_GOLD));
            promo = createOrUpdatePromotion(promo, "Gold Tier Promotion", 15.0f);
        } else if (total.compareTo(SILVER_THRESHOLD) >= 0) {
            partner.setCommissionRate(baseRate.add(COMMISSION_INCREASE_SILVER));
            promo = createOrUpdatePromotion(promo, "Silver Tier Promotion", 10.0f);
        }

        partner.setPromotions(promo);
        return partnersRepository.save(partner);
    }

    private Promotions createOrUpdatePromotion(Promotions current, String title, float discount) {
        Promotions promo = current == null ? new Promotions() : current;
        promo.setPromotionTitle(title);
        promo.setPromotionDescription("Automatic promotion based on total commission");
        promo.setPromotionDiscountPercentage(discount);
        promo.setPromotionStartDate(new Date());
        promo.setPromotionEndDate(Date.from(LocalDate.now().plusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        return promotionsRepository.save(promo);
    }

    public Map<String, Double> getMonthlyRevenue() {
        Map<String, Double> monthly = new HashMap<>();
        for (int i = 1; i <= 12; i++) monthly.put(String.valueOf(i), 0.0);
        for (Payment p : paymentRepository.findAll()) {
            if (p.getPaymentDate() == null || p.getPaymentAmount() == null) continue;
            int month = p.getPaymentDate().toInstant().atZone(ZoneId.systemDefault()).getMonthValue();
            monthly.put(String.valueOf(month),
                    monthly.getOrDefault(String.valueOf(month), 0.0) + p.getPaymentAmount().doubleValue());
        }
        return monthly;
    }
}
