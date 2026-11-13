# PC Store — Console E-Commerce Application

A Java console application for a PC store. The project demonstrates six course patterns:
Builder, Factory Method, Strategy, Observer, Decorator and Facade.

## Technology

- Java 17
- SQLite (via SQLite JDBC)
- JUnit 5 for automated tests
- Maven for dependency management and repeatable test/build commands

Maven is a project tooling choice, not a design pattern and not a requirement stated in the Final Project Defense Requirements. It is used here to download SQLite JDBC and JUnit and to run tests consistently.

## Run in IntelliJ IDEA

1. Open the `PCStore` folder (the folder containing `pom.xml`).
2. Allow IntelliJ IDEA to load the Maven project.
3. Run `com.pcstore.app.Main`.

The SQLite file `pcstore.db` is created in the application's working directory. The demo seeds products when the catalog is empty.

## Run tests

From the `PCStore` directory:

```bash
mvn clean test
```

Alternatively, run the test classes from `src/test/java` directly in IntelliJ IDEA.

## Demo flow

1. Browse products.
2. Build a preset PC configuration with `PCConfigurationDirector`.
3. Add it to the cart.
4. Add an optional warranty decorator.
5. Select a discount strategy (the cart resets to no discount after checkout).
6. Checkout through `CheckoutFacade`.

## Pattern responsibilities

- **Builder:** assembles a PC configuration from selected physical products.
- **Factory Method:** creates CPU, GPU and RAM objects through concrete creators.
- **Strategy:** selects the discount calculation algorithm.
- **Observer:** the cart subscribes to products and receives price/stock change events.
- **Decorator:** adds the optional extended warranty without changing the wrapped product.
- **Facade:** provides one checkout operation and coordinates order creation with the transactional repository.

The patterns are used for separate responsibilities and cooperate in the PC purchase workflow. They are not added merely to increase class count. The Builder checks required components (including a CPU cooler), stock, CPU/motherboard socket, CPU/cooler socket, RAM type, GPU-to-PSU wattage and motherboard-to-case form factor. These are the compatibility rules represented by the demo data, not a complete hardware compatibility database.

## Order history and transaction boundary

A purchased cart line is stored in `order_items`. Its physical component details are stored as immutable checkout-time snapshots in `order_item_components`, including a standalone product (one component snapshot) or a PC configuration (multiple component snapshots). These descriptive snapshots are not added to the order subtotal a second time.

SQLite performs stock deductions, order insertion, line insertion and component snapshot insertion in one transaction. The repository returns the committed stock levels; the in-memory Product objects are synchronized from those values only after commit. If a required item is unavailable, the database transaction rolls back and no order is persisted.

## Team Contributions

### Nurdaulet Absultanov
- Builder Pattern
- Strategy Pattern
- Observer Pattern

### Aisamal Tolemuratova
- Factory Method Pattern
- Decorator Pattern
- Facade Pattern

