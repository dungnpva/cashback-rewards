---
description: Write failing acceptance tests for ALL rules in a spec
argument-hint: "@doc/specs/<feature>.md"
---
Write failing acceptance tests for EVERY rule in: $ARGUMENTS

Read CLAUDE.md for project conventions before writing anything.
Re-read the spec file to understand every rule, its examples, and its counter-examples.

## Structure

One outer class per feature, named <Feature>AcceptanceIT.
If the class already exists, add to it — do not recreate it.
One @Nested inner class per rule — name it after the rule.
One @Test per example from the spec.
When a rule's examples are a table, use @ParameterizedTest with one row per example.

Use @DisplayName with the spec's exact business language:
- Class: the rule name
- Method: "The one where..." text from the spec

Skip any rule that already has a @Nested class — never rewrite existing tests.

## How to test

Test through the REST API using @SpringBootTest + MockMvc.
Send real HTTP requests. Assert real HTTP responses.
NEVER call services or domain objects directly —
this is an acceptance test, not a unit test.

Reuse the endpoints and request/response shapes the existing tests already use.
When a rule needs an endpoint no test uses yet, design it consistently and list it in the report.

Assert exact values from the spec examples.
For money: .andExpect(jsonPath("$.amount").value("1.60"))
or use isEqualByComparingTo with BigDecimal.

Each test creates its own data with unique merchant names and customer ids —
tests share one application context and database.

## What NOT to do

Do NOT write production code. Every new test MUST FAIL.
A passing test means you tested nothing.
Do NOT invent examples beyond what the spec provides.
The spec is the contract.
Skip any rule that still has open questions — list it in the report instead of guessing.
Do NOT use mocks in acceptance tests.
Wire the full stack: controller → service → domain → persistence.

## When you're done

Run the tests. Confirm each new test fails for the RIGHT reason:
- Missing endpoint → 404 or compilation error (good)
- Wrong value → not yet, the endpoint shouldn't exist
- Test passes → something is wrong, investigate

Report, per rule: number of examples, failure reason, and any new endpoint you designed.
List skipped rules and why (already covered, or open questions).

STOP. Do not proceed to implementation.
