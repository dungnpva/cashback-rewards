package com.serenitydojo.cashback_rewards.domain.service;

import com.serenitydojo.cashback_rewards.domain.model.Merchant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Cashback calculator")
class CashbackCalculatorTest {

    @Nested
    @DisplayName("Rule: Must only award cashback for purchases at partner (registered) merchants")
    class PartnerMerchants {

        @Test
        @DisplayName("The one where a customer pays $80.00 at GreenGrocer (partner, 3%) and earns $2.40")
        void earnsCashbackAtPartnerMerchantRate() {
            Merchant greenGrocer = new Merchant("GreenGrocer", new BigDecimal("0.03"));

            BigDecimal cashback = new CashbackCalculator().calculate(greenGrocer, new BigDecimal("80.00"));

            assertThat(cashback).isEqualByComparingTo("2.40");
        }
    }
}
