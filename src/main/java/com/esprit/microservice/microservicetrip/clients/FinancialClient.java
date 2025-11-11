package com.esprit.microservice.microservicetrip.clients;

import com.esprit.microservice.microservicetrip.DTO.PaymentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "FINANCE-SERVICE", url = "http://localhost:8079/finance")
public interface FinancialClient {

    @GetMapping("/payments")
    List<PaymentDTO> getAllPayments();

    @GetMapping("/payments/{id}")
    PaymentDTO getPaymentById(@PathVariable("id") Integer id);
}

