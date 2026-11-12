# Changelog

## Version 5
- Added reference-counted product subscriptions in `ShoppingCart`.
- Added `removeAt(int)` with safe observer unsubscription.
- Kept one subscription per cart/product pair even when the product appears in multiple cart lines.
- Added `ObserverPatternTest` covering notifications, duplicate subscriptions, live cart pricing, removal, and clear.
- Documented the Observer lifecycle and test coverage in README.

## Version 5.2 — Checkout consistency and simplification

- Checkout repository returns committed stock levels from the same transaction.
- Facade synchronizes in-memory Product objects from committed database values rather than subtracting from potentially stale stock.
- Observer callback failures after commit are reported without misrepresenting a committed order as failed.
- Every purchased physical product, including a standalone product, is stored as an order component snapshot.
- Removed the unused non-transactional InventoryService and unused duplicate OrderRepository implementation.
- Removed the unused order repository variable from Main.
- Added a successful checkout integration test that verifies committed stock, persisted order and standalone component snapshot.

## v5.3

- Removed unused premium-packaging decorator and redundant stock/product access methods.
- Kept stock deduction exclusively inside the checkout database transaction.
- Added input validation to Factory Method creators and explicit null/negative warranty-fee validation.
- Added GPU power-supply and motherboard/case form-factor compatibility checks to the Builder.
- Simplified cart item replacement and formatted the console entry point into small methods.
- Added Factory Method tests and a successful checkout transaction integration test.
- Removed IntelliJ module metadata and Git internals from the distributable archive.
