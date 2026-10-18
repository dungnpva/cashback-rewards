package com.serenitydojo.cashback_rewards.acceptance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Basic Cashback Calculation")
class BasicCashbackCalculationAcceptanceIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    @DisplayName("Rule: Must calculate cashback as a percentage of the purchase amount")
    class CalculatesCashbackAsPercentageOfPurchaseAmount {

        @ParameterizedTest(name = "The one where a customer pays ${0} at a {1} merchant and earns ${2}")
        @CsvSource({
                "100.00, 5%,  5.00",
                "250.00, 3%,  7.50",
                "40.00,  10%, 4.00"
        })
        void earnsPercentageOfPurchaseAmount(String purchaseAmount, String merchantRate, String expectedCashback) throws Exception {
            String merchantName = "Merchant at " + merchantRate;
            String customerId = "cust-rate-" + merchantRate;
            String cashbackRate = new BigDecimal(merchantRate.replace("%", "")).movePointLeft(2).toPlainString();

            mockMvc.perform(post("/api/merchants")
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {
                                      "name": "%s",
                                      "cashbackRate": "%s"
                                    }
                                    """.formatted(merchantName, cashbackRate)))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/api/purchases")
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {
                                      "customerId": "%s",
                                      "merchantName": "%s",
                                      "amount": "%s"
                                    }
                                    """.formatted(customerId, merchantName, purchaseAmount)))
                    .andExpect(status().isCreated());

            JsonNode records = cashbackRecordsFor(customerId);

            assertThat(records).hasSize(1);
            assertThat(new BigDecimal(records.get(0).get("cashback").asText())).isEqualByComparingTo(expectedCashback);
        }
    }

    @Nested
    @DisplayName("Rule: Must enforce a $5.00 minimum purchase amount to earn cashback")
    class EnforcesMinimumPurchaseAmount {

        @Test
        @DisplayName("The one where a customer purchases $5.00 at a 10% merchant — earns $0.50 cashback")
        void earnsCashbackAtMinimumPurchaseAmount() throws Exception {
            mockMvc.perform(post("/api/merchants")
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {
                                      "name": "Minimum Mart",
                                      "cashbackRate": "0.10"
                                    }
                                    """))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/api/purchases")
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {
                                      "customerId": "cust-minimum-001",
                                      "merchantName": "Minimum Mart",
                                      "amount": "5.00"
                                    }
                                    """))
                    .andExpect(status().isCreated());

            JsonNode records = cashbackRecordsFor("cust-minimum-001");

            assertThat(records).hasSize(1);
            assertThat(new BigDecimal(records.get(0).get("cashback").asText())).isEqualByComparingTo("0.50");
        }

        @Test
        @DisplayName("The one where a customer purchases $4.99 at a 10% merchant — earns $0.00 cashback")
        void earnsNoCashbackBelowMinimumPurchaseAmount() throws Exception {
            mockMvc.perform(post("/api/merchants")
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {
                                      "name": "Below Minimum Mart",
                                      "cashbackRate": "0.10"
                                    }
                                    """))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/api/purchases")
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {
                                      "customerId": "cust-minimum-002",
                                      "merchantName": "Below Minimum Mart",
                                      "amount": "4.99"
                                    }
                                    """))
                    .andExpect(status().isCreated());

            JsonNode records = cashbackRecordsFor("cust-minimum-002");

            assertThat(records).hasSize(1);
            assertThat(new BigDecimal(records.get(0).get("cashback").asText())).isEqualByComparingTo("0.00");
        }
    }

    @Nested
    @DisplayName("Rule: Must round cashback down to 2 decimal places")
    class RoundsCashbackDownToTwoDecimalPlaces {

        @ParameterizedTest(name = "The one where a customer pays ${0} at a {1} merchant — exact result ${2} — and earns ${3}")
        @CsvSource({
                "33.33, 5%, 1.6665, 1.66",
                "19.99, 3%, 0.5997, 0.59"
        })
        void truncatesFractionalCents(String purchaseAmount, String merchantRate, String exactResult, String expectedCashback) throws Exception {
            String merchantName = "Rounding Merchant at " + merchantRate;
            String customerId = "cust-rounding-" + merchantRate;
            String cashbackRate = new BigDecimal(merchantRate.replace("%", "")).movePointLeft(2).toPlainString();

            mockMvc.perform(post("/api/merchants")
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {
                                      "name": "%s",
                                      "cashbackRate": "%s"
                                    }
                                    """.formatted(merchantName, cashbackRate)))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/api/purchases")
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {
                                      "customerId": "%s",
                                      "merchantName": "%s",
                                      "amount": "%s"
                                    }
                                    """.formatted(customerId, merchantName, purchaseAmount)))
                    .andExpect(status().isCreated());

            JsonNode records = cashbackRecordsFor(customerId);

            assertThat(records).hasSize(1);
            assertThat(new BigDecimal(records.get(0).get("cashback").asText())).isEqualByComparingTo(expectedCashback);
        }
    }

    @Nested
    @DisplayName("Rule: Must credit cashback to the customer's available rewards balance only after the transaction has settled")
    class CreditsAvailableBalanceOnlyAfterSettlement {

        @Test
        @DisplayName("The one where a customer with $10.00 available earns $5.00 on a purchase that then settles — available balance becomes $15.00")
        void creditsAvailableBalanceWhenPurchaseSettles() throws Exception {
            registerMerchant("Settled Mart", "0.05");
            String earlierPurchase = recordPurchase("cust-settle-001", "Settled Mart", "200.00");
            settle(earlierPurchase);
            assertThat(availableBalanceOf("cust-settle-001")).isEqualByComparingTo("10.00");

            String purchase = recordPurchase("cust-settle-001", "Settled Mart", "100.00");
            settle(purchase);

            assertThat(availableBalanceOf("cust-settle-001")).isEqualByComparingTo("15.00");
        }

        @Test
        @DisplayName("The one where the purchase earns $5.00 but has not settled yet — available balance stays $10.00")
        void keepsAvailableBalanceWhilePurchaseIsUnsettled() throws Exception {
            registerMerchant("Unsettled Mart", "0.05");
            String earlierPurchase = recordPurchase("cust-settle-002", "Unsettled Mart", "200.00");
            settle(earlierPurchase);
            assertThat(availableBalanceOf("cust-settle-002")).isEqualByComparingTo("10.00");

            recordPurchase("cust-settle-002", "Unsettled Mart", "100.00");

            assertThat(availableBalanceOf("cust-settle-002")).isEqualByComparingTo("10.00");
        }
    }

    @Nested
    @DisplayName("Rule: Must show cashback from unsettled transactions as \"pending\" on the customer's rewards balance")
    class ShowsUnsettledCashbackAsPending {

        @Test
        @DisplayName("The one where a customer with $10.00 available makes an unsettled $100.00 purchase at 5% — available $10.00, pending $5.00")
        void showsUnsettledCashbackAsPending() throws Exception {
            registerMerchant("Pending Mart", "0.05");
            String earlierPurchase = recordPurchase("cust-pending-001", "Pending Mart", "200.00");
            settle(earlierPurchase);

            recordPurchase("cust-pending-001", "Pending Mart", "100.00");

            assertThat(availableBalanceOf("cust-pending-001")).isEqualByComparingTo("10.00");
            assertThat(pendingBalanceOf("cust-pending-001")).isEqualByComparingTo("5.00");
        }

        @Test
        @DisplayName("The one where that transaction settles — pending drops to $0.00 and available becomes $15.00")
        void movesCashbackFromPendingToAvailableOnSettlement() throws Exception {
            registerMerchant("Pending Settles Mart", "0.05");
            String earlierPurchase = recordPurchase("cust-pending-002", "Pending Settles Mart", "200.00");
            settle(earlierPurchase);
            String purchase = recordPurchase("cust-pending-002", "Pending Settles Mart", "100.00");
            assertThat(pendingBalanceOf("cust-pending-002")).isEqualByComparingTo("5.00");

            settle(purchase);

            assertThat(pendingBalanceOf("cust-pending-002")).isEqualByComparingTo("0.00");
            assertThat(availableBalanceOf("cust-pending-002")).isEqualByComparingTo("15.00");
        }

        @Test
        @DisplayName("The one where a cancellation notification arrives for that unsettled transaction — its cashback stays pending (available $10.00, pending $5.00); resolving it is a manual admin task")
        void keepsCashbackPendingWhenUnsettledPurchaseIsCancelled() throws Exception {
            registerMerchant("Pending Cancelled Mart", "0.05");
            String earlierPurchase = recordPurchase("cust-pending-003", "Pending Cancelled Mart", "200.00");
            settle(earlierPurchase);
            String purchase = recordPurchase("cust-pending-003", "Pending Cancelled Mart", "100.00");

            cancel(purchase);

            assertThat(availableBalanceOf("cust-pending-003")).isEqualByComparingTo("10.00");
            assertThat(pendingBalanceOf("cust-pending-003")).isEqualByComparingTo("5.00");
        }
    }

    @Nested
    @DisplayName("Rule: Must require every merchant to be registered with a cashback rate between 0% and 25% (inclusive)")
    class RequiresCashbackRateWithinAllowedRange {

        @Test
        @DisplayName("The one where a merchant is registered with a 25% rate — accepted")
        void acceptsMaximumRate() throws Exception {
            mockMvc.perform(post("/api/merchants")
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {
                                      "name": "Max Rate Mart",
                                      "cashbackRate": "0.25"
                                    }
                                    """))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("The one where a merchant is registered with a 26% rate — rejected")
        void rejectsRateAboveMaximum() throws Exception {
            mockMvc.perform(post("/api/merchants")
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {
                                      "name": "Too Generous Mart",
                                      "cashbackRate": "0.26"
                                    }
                                    """))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("The one where a merchant is registered with a negative rate — rejected")
        void rejectsNegativeRate() throws Exception {
            mockMvc.perform(post("/api/merchants")
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {
                                      "name": "Negative Rate Mart",
                                      "cashbackRate": "-0.01"
                                    }
                                    """))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("The one where a merchant is registered without a cashback rate — rejected")
        void rejectsMissingRate() throws Exception {
            mockMvc.perform(post("/api/merchants")
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {
                                      "name": "No Rate Mart"
                                    }
                                    """))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("The one where a merchant already registered at 3% is registered again with the same name at 25% — rejected as a conflict, and a $100.00 purchase there still earns $3.00")
        void rejectsDuplicateRegistrationAndKeepsExistingRate() throws Exception {
            mockMvc.perform(post("/api/merchants")
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {
                                      "name": "Duplicate Mart",
                                      "cashbackRate": "0.03"
                                    }
                                    """))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/api/merchants")
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {
                                      "name": "Duplicate Mart",
                                      "cashbackRate": "0.25"
                                    }
                                    """))
                    .andExpect(status().isConflict());

            mockMvc.perform(post("/api/purchases")
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {
                                      "customerId": "cust-duplicate-001",
                                      "merchantName": "Duplicate Mart",
                                      "amount": "100.00"
                                    }
                                    """))
                    .andExpect(status().isCreated());

            JsonNode records = cashbackRecordsFor("cust-duplicate-001");

            assertThat(records).hasSize(1);
            assertThat(new BigDecimal(records.get(0).get("cashback").asText())).isEqualByComparingTo("3.00");
        }
    }

    @Nested
    @DisplayName("Rule: Must accept purchases at merchants with a 0% cashback rate, awarding zero cashback")
    class AcceptsPurchasesAtZeroRateMerchants {

        @Test
        @DisplayName("The one where a customer purchases $100.00 at a merchant with a 0% rate — earns $0.00, purchase is recorded")
        void recordsPurchaseWithZeroCashback() throws Exception {
            registerMerchant("Paused Mart", "0.00");

            recordPurchase("cust-zero-rate-001", "Paused Mart", "100.00");

            JsonNode records = cashbackRecordsFor("cust-zero-rate-001");

            assertThat(records).hasSize(1);
            assertThat(new BigDecimal(records.get(0).get("cashback").asText())).isEqualByComparingTo("0.00");
        }
    }

    @Nested
    @DisplayName("Rule: Must only award cashback for purchases at partner (registered) merchants")
    class OnlyAwardsCashbackAtPartnerMerchants {

        @Test
        @DisplayName("The one where a customer pays $80.00 at GreenGrocer (partner, 3%) and earns $2.40")
        void earnsCashbackAtPartnerMerchant() throws Exception {
            mockMvc.perform(post("/api/merchants")
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {
                                      "name": "GreenGrocer",
                                      "cashbackRate": "0.03"
                                    }
                                    """))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/api/purchases")
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {
                                      "customerId": "cust-partner-001",
                                      "merchantName": "GreenGrocer",
                                      "amount": "80.00"
                                    }
                                    """))
                    .andExpect(status().isCreated());

            JsonNode records = cashbackRecordsFor("cust-partner-001");

            assertThat(records).hasSize(1);
            assertThat(records.get(0).get("merchantName").asText()).isEqualTo("GreenGrocer");
            assertThat(new BigDecimal(records.get(0).get("cashback").asText())).isEqualByComparingTo("2.40");
        }

        @Test
        @DisplayName("The one where a customer pays $80.00 at a non-partner café — no cashback record is created and the purchase is silently ignored by the rewards system")
        void ignoresPurchaseAtNonPartnerMerchant() throws Exception {
            mockMvc.perform(post("/api/purchases")
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {
                                      "customerId": "cust-non-partner-001",
                                      "merchantName": "The Corner Café",
                                      "amount": "80.00"
                                    }
                                    """))
                    .andExpect(status().isAccepted());

            JsonNode records = cashbackRecordsFor("cust-non-partner-001");

            assertThat(records).isEmpty();
        }
    }

    @Nested
    @DisplayName("Rule: Must use the cashback rate that was effective for the merchant at the timestamp of the purchase")
    class UsesRateEffectiveAtPurchaseTimestamp {

        @Test
        @DisplayName("The one where Acme's rate changes from 2% to 5% effective 2026-05-01 14:00:00. A purchase at 13:59:59 earns 2%; a purchase at 14:00:01 earns 5%")
        void appliesRateEffectiveAtPurchaseTimestamp() throws Exception {
            registerMerchant("Acme", "0.02");
            changeRate("Acme", "0.05", Instant.parse("2026-05-01T14:00:00Z"));

            recordPurchaseAt("cust-acme-before", "Acme", "100.00", Instant.parse("2026-05-01T13:59:59Z"));
            recordPurchaseAt("cust-acme-after", "Acme", "100.00", Instant.parse("2026-05-01T14:00:01Z"));

            JsonNode before = cashbackRecordsFor("cust-acme-before");
            JsonNode after = cashbackRecordsFor("cust-acme-after");

            assertThat(new BigDecimal(before.get(0).get("cashback").asText())).isEqualByComparingTo("2.00");
            assertThat(new BigDecimal(after.get(0).get("cashback").asText())).isEqualByComparingTo("5.00");
        }

        @Test
        @DisplayName("The one where an admin updates Acme's rate today — purchases made (and cashback already calculated) yesterday are not recalculated")
        void doesNotRecalculateEarlierPurchasesWhenRateChanges() throws Exception {
            Instant now = Instant.now();
            registerMerchant("Acme Rate Update", "0.02");
            recordPurchaseAt("cust-acme-yesterday", "Acme Rate Update", "100.00", now.minus(Duration.ofDays(1)));

            changeRate("Acme Rate Update", "0.05", now);

            JsonNode records = cashbackRecordsFor("cust-acme-yesterday");

            assertThat(records).hasSize(1);
            assertThat(new BigDecimal(records.get(0).get("cashback").asText())).isEqualByComparingTo("2.00");
        }
    }

    private JsonNode cashbackRecordsFor(String customerId) throws Exception {
        String body = mockMvc.perform(get("/api/customers/{customerId}/cashback", customerId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(body);
    }

    private void registerMerchant(String name, String cashbackRate) throws Exception {
        mockMvc.perform(post("/api/merchants")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "%s",
                                  "cashbackRate": "%s"
                                }
                                """.formatted(name, cashbackRate)))
                .andExpect(status().isCreated());
    }

    private String recordPurchase(String customerId, String merchantName, String amount) throws Exception {
        String body = mockMvc.perform(post("/api/purchases")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "%s",
                                  "merchantName": "%s",
                                  "amount": "%s"
                                }
                                """.formatted(customerId, merchantName, amount)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(body).get("purchaseId").asText();
    }

    private String recordPurchaseAt(String customerId, String merchantName, String amount, Instant purchasedAt)
            throws Exception {
        String body = mockMvc.perform(post("/api/purchases")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "%s",
                                  "merchantName": "%s",
                                  "amount": "%s",
                                  "purchasedAt": "%s"
                                }
                                """.formatted(customerId, merchantName, amount, purchasedAt)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(body).get("purchaseId").asText();
    }

    private void changeRate(String merchantName, String cashbackRate, Instant effectiveFrom) throws Exception {
        mockMvc.perform(post("/api/merchants/{merchantName}/rates", merchantName)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "cashbackRate": "%s",
                                  "effectiveFrom": "%s"
                                }
                                """.formatted(cashbackRate, effectiveFrom)))
                .andExpect(status().isCreated());
    }

    private void settle(String purchaseId) throws Exception {
        mockMvc.perform(post("/api/purchases/{purchaseId}/settlement", purchaseId))
                .andExpect(status().isOk());
    }

    private void cancel(String purchaseId) throws Exception {
        mockMvc.perform(post("/api/purchases/{purchaseId}/cancellation", purchaseId))
                .andExpect(status().isOk());
    }

    private BigDecimal availableBalanceOf(String customerId) throws Exception {
        String body = mockMvc.perform(get("/api/customers/{customerId}/rewards-balance", customerId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return new BigDecimal(objectMapper.readTree(body).get("available").asText());
    }

    private BigDecimal pendingBalanceOf(String customerId) throws Exception {
        String body = mockMvc.perform(get("/api/customers/{customerId}/rewards-balance", customerId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return new BigDecimal(objectMapper.readTree(body).get("pending").asText());
    }
}
