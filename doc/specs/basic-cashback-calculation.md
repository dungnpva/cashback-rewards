# Basic Cashback Calculation

**User Story:** As a customer, I want to earn cashback on my purchases so that I'm rewarded for shopping with partner merchants.

## Rule 1: Must calculate cashback as a percentage of the purchase amount

Cashback earned = purchase amount × merchant's cashback rate.

| Purchase amount | Merchant rate | Cashback earned |
|----------------:|:-------------:|----------------:|
| $100.00         | 5%            | $5.00           |
| $250.00         | 3%            | $7.50           |
| $40.00          | 10%           | $4.00           |

## Rule 2: Must enforce a $5.00 minimum purchase amount to earn cashback

Purchases below $5.00 earn no cashback.

- **Example:** The one where a customer purchases $5.00 at a 10% merchant — earns $0.50 cashback.
- **Counter-example:** The one where a customer purchases $4.99 at a 10% merchant — earns $0.00 cashback.

## Rule 3: Must round cashback down to 2 decimal places

Fractional cents are always truncated (RoundingMode.DOWN), never rounded up.

| Purchase amount | Rate | Exact result | Cashback earned |
|----------------:|:----:|-------------:|----------------:|
| $33.33          | 5%   | $1.6665      | $1.66           |
| $19.99          | 3%   | $0.5997      | $0.59           |

## Rule 4: Must credit cashback to the customer's available rewards balance only after the transaction has settled

A transaction is settled when the system receives a separate settlement notification for it (e.g. from the payment network or the merchant). Only then is its cashback added to the customer's available balance.

- **Example:** The one where a customer with $10.00 available earns $5.00 on a purchase that then settles — available balance becomes $15.00.
- **Counter-example:** The one where the purchase earns $5.00 but has not settled yet — available balance stays $10.00.

## Rule 5: Must show cashback from unsettled transactions as "pending" on the customer's rewards balance

The rewards balance has two parts: available (settled) and pending (not yet settled).

Before settling, a transaction may instead be cancelled or refunded. The system receives a separate cancellation notification for it (e.g. from the payment network or the merchant), accepts it, but does not adjust the cashback automatically.

- **Example:** The one where a customer with $10.00 available makes an unsettled $100.00 purchase at 5% — available $10.00, pending $5.00.
- **Example:** The one where that transaction settles — pending drops to $0.00 and available becomes $15.00.
- **Counter-example:** The one where a cancellation notification arrives for that unsettled transaction — its cashback stays pending (available $10.00, pending $5.00); resolving it is a manual admin task.

## Rule 6: Must require every merchant to be registered with a cashback rate between 0% and 25% (inclusive)

A cashback rate is mandatory at registration. Missing rates or rates outside this range are rejected.

- **Example:** The one where a merchant is registered with a 25% rate — accepted.
- **Counter-example:** The one where a merchant is registered with a 26% rate — rejected.
- **Counter-example:** The one where a merchant is registered with a negative rate — rejected.
- **Counter-example:** The one where a merchant is registered without a cashback rate — rejected.
- **Counter-example:** The one where a merchant already registered at 3% is registered again with the same name at 25% — rejected as a conflict, and a $100.00 purchase there still earns $3.00.

## Rule 7: Must accept purchases at merchants with a 0% cashback rate, awarding zero cashback

A 0% rate is a deliberate choice (e.g. cashback paused for that merchant). The purchase is recorded but earns $0.00.

- **Example:** The one where a customer purchases $100.00 at a merchant with a 0% rate — earns $0.00, purchase is recorded.

## Rule 8: Must only award cashback for purchases at partner (registered) merchants

Only merchants registered in the system are partners. A purchase at a non-partner merchant is ignored by the rewards system: no cashback record is created and the customer's balance is unchanged.

- **Example:** The one where a customer pays $80.00 at GreenGrocer (partner, 3%) and earns $2.40.
- **Counter-example:** The one where a customer pays $80.00 at a non-partner café — no cashback record is created and the purchase is silently ignored by the rewards system.

## Rule 9: Must use the cashback rate that was effective for the merchant at the timestamp of the purchase

A merchant's rate can change over time. An admin changes a registered merchant's rate by giving the new rate and the timestamp from which it is effective; the new rate must also be between 0% and 25% (inclusive). The rate given at registration applies until the first change takes effect. Each rate applies from its effective timestamp (inclusive) until the next rate takes effect. All timestamps are in UTC. Cashback is calculated once, when the purchase is recorded, and is never recalculated.

- **Example:** The one where Acme's rate changes from 2% to 5% effective `2026-05-01 14:00:00`. A purchase at 13:59:59 earns 2%; a purchase at 14:00:01 earns 5%.
- **Counter-example:** The one where an admin updates Acme's rate today — purchases made (and cashback already calculated) yesterday are not recalculated.

## Out of scope

- Spending the available balance on other purchases — covered by the *Redeem Cashback* story.
