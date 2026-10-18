package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.application.port.in.ViewRewardsBalanceUseCase;
import com.serenitydojo.cashback_rewards.domain.model.RewardsBalance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
class RewardsBalanceController {

    private final ViewRewardsBalanceUseCase viewRewardsBalanceUseCase;

    RewardsBalanceController(ViewRewardsBalanceUseCase viewRewardsBalanceUseCase) {
        this.viewRewardsBalanceUseCase = viewRewardsBalanceUseCase;
    }

    @GetMapping("/api/customers/{customerId}/rewards-balance")
    RewardsBalanceResponse rewardsBalance(@PathVariable String customerId) {
        RewardsBalance rewardsBalance = viewRewardsBalanceUseCase.rewardsBalance(customerId);
        return new RewardsBalanceResponse(rewardsBalance.available(), rewardsBalance.pending());
    }

    record RewardsBalanceResponse(BigDecimal available, BigDecimal pending) {
    }
}
