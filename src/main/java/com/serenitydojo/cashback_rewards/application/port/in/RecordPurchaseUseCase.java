package com.serenitydojo.cashback_rewards.application.port.in;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RecordPurchaseUseCase {

    /**
     * @param purchasedAt when the purchase happened; {@code null} means now
     * @return the purchase id, or empty when the merchant is not a partner and the purchase is ignored
     */
    Optional<UUID> recordPurchase(String customerId, String merchantName, BigDecimal amount, Instant purchasedAt);
}
