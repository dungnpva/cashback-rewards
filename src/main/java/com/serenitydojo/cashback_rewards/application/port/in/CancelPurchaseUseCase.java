package com.serenitydojo.cashback_rewards.application.port.in;

import java.util.UUID;

public interface CancelPurchaseUseCase {

    void cancelPurchase(UUID purchaseId);
}
