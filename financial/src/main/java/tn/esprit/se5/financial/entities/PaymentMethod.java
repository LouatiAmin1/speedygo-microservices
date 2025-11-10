package tn.esprit.se5.financial.entities;

import com.stripe.model.Charge;

public enum PaymentMethod {
    CASH,
    STRIPE,
    CREDIT_CARD;

}
