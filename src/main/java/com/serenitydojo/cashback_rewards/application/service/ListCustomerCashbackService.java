package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.application.port.in.ListCustomerCashbackUseCase;
import com.serenitydojo.cashback_rewards.application.port.out.CashbackRecordRepository;
import com.serenitydojo.cashback_rewards.domain.model.CashbackRecord;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListCustomerCashbackService implements ListCustomerCashbackUseCase {

    private final CashbackRecordRepository cashbackRecordRepository;

    public ListCustomerCashbackService(CashbackRecordRepository cashbackRecordRepository) {
        this.cashbackRecordRepository = cashbackRecordRepository;
    }

    @Override
    public List<CashbackRecord> listCashback(String customerId) {
        return cashbackRecordRepository.findByCustomerId(customerId);
    }
}
