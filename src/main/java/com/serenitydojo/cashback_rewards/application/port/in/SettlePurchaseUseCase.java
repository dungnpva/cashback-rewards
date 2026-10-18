package com.serenitydojo.cashback_rewards.application.port.in;

import java.util.UUID;

public interface SettlePurchaseUseCase {

    void settlePurchase(UUID purchaseId);
}
