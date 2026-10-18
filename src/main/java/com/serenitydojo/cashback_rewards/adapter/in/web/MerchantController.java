package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.application.port.in.ChangeMerchantRateUseCase;
import com.serenitydojo.cashback_rewards.application.port.in.RegisterMerchantUseCase;
import com.serenitydojo.cashback_rewards.domain.exception.InvalidCashbackRateException;
import com.serenitydojo.cashback_rewards.domain.exception.MerchantAlreadyRegisteredException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;

@RestController
@RequestMapping("/api/merchants")
class MerchantController {

    private final RegisterMerchantUseCase registerMerchantUseCase;
    private final ChangeMerchantRateUseCase changeMerchantRateUseCase;

    MerchantController(RegisterMerchantUseCase registerMerchantUseCase,
                       ChangeMerchantRateUseCase changeMerchantRateUseCase) {
        this.registerMerchantUseCase = registerMerchantUseCase;
        this.changeMerchantRateUseCase = changeMerchantRateUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    void register(@Valid @RequestBody RegisterMerchantRequest request) {
        registerMerchantUseCase.registerMerchant(request.name(), request.cashbackRate());
    }

    @PostMapping("/{merchantName}/rates")
    @ResponseStatus(HttpStatus.CREATED)
    void changeRate(@PathVariable String merchantName, @Valid @RequestBody ChangeRateRequest request) {
        changeMerchantRateUseCase.changeRate(merchantName, request.cashbackRate(), request.effectiveFrom());
    }

    @ExceptionHandler(InvalidCashbackRateException.class)
    ProblemDetail handleInvalidCashbackRate(InvalidCashbackRateException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(MerchantAlreadyRegisteredException.class)
    ProblemDetail handleMerchantAlreadyRegistered(MerchantAlreadyRegisteredException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    record RegisterMerchantRequest(String name, @NotNull BigDecimal cashbackRate) {
    }

    record ChangeRateRequest(@NotNull BigDecimal cashbackRate, @NotNull Instant effectiveFrom) {
    }
}
