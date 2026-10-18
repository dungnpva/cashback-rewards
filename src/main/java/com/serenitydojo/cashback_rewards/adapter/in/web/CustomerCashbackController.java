package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.application.port.in.ListCustomerCashbackUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
class CustomerCashbackController {

    private final ListCustomerCashbackUseCase listCustomerCashbackUseCase;

    CustomerCashbackController(ListCustomerCashbackUseCase listCustomerCashbackUseCase) {
        this.listCustomerCashbackUseCase = listCustomerCashbackUseCase;
    }

    @GetMapping("/api/customers/{customerId}/cashback")
    List<CashbackRecordResponse> list(@PathVariable String customerId) {
        return listCustomerCashbackUseCase.listCashback(customerId).stream()
                .map(cashbackRecord -> new CashbackRecordResponse(cashbackRecord.merchantName(), cashbackRecord.cashback()))
                .toList();
    }

    record CashbackRecordResponse(String merchantName, BigDecimal cashback) {
    }
}
