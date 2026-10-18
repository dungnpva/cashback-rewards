package com.serenitydojo.cashback_rewards.domain.exception;

public class MerchantAlreadyRegisteredException extends RuntimeException {

    public MerchantAlreadyRegisteredException(String merchantName) {
        super("Merchant " + merchantName + " is already registered");
    }
}
