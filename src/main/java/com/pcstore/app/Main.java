package com.pcstore.app;

import com.pcstore.domain.model.CartItem;
import com.pcstore.domain.model.Order;
import com.pcstore.domain.model.PCConfiguration;
import com.pcstore.domain.model.Purchasable;
import com.pcstore.infrastructure.database.Database;
import com.pcstore.infrastructure.database.SqliteCheckoutRepository;
import com.pcstore.infrastructure.database.SqliteProductRepository;
import com.pcstore.patterns.builder.PCConfigurationDirector;
import com.pcstore.patterns.builder.StandardPCConfigurationBuilder;
import com.pcstore.patterns.decorator.ExtendedWarrantyDecorator;
import com.pcstore.patterns.facade.CheckoutFacade;
import com.pcstore.patterns.strategy.PercentageDiscountStrategy;
import com.pcstore.repository.ProductRepository;
import com.pcstore.service.ProductService;
import com.pcstore.service.ShoppingCart;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public final class Main {
    private static final BigDecimal WARRANTY_FEE = new BigDecimal("12000");
    private static final BigDecimal DISCOUNT_PERCENTAGE = new BigDecimal("5");

    private Main() { }

    public static void main(String[] args) {
        Database.initialize();
        ProductRepository productRepository = new SqliteProductRepository();
        ProductService products = new ProductService(productRepository);
        DemoData.seed(products);
        ShoppingCart cart = new ShoppingCart();
        PCConfigurationDirector director = new PCConfigurationDirector(products);

        try (Scanner scanner = new Scanner(System.in)) {
            runMenu(scanner, products, cart, director);
        }
        System.out.println("Goodbye.");
    }

    private static void runMenu(Scanner scanner, ProductService products,
                                ShoppingCart cart, PCConfigurationDirector director) {
        boolean running = true;
        System.out.println("\n=== PC STORE ===");
        while (running) {
            printMenu();
            System.out.print("> ");
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> products.findAll().forEach(System.out::println);
                    case "2" -> addGamingPc(director, cart);
                    case "3" -> addWarrantyToLastItem(cart);
                    case "4" -> {
                        cart.setDiscountStrategy(new PercentageDiscountStrategy(DISCOUNT_PERCENTAGE));
                        System.out.println("5% discount selected.");
                    }
                    case "5" -> showCart(cart);
                    case "6" -> checkout(cart);
                    case "0" -> running = false;
                    default -> System.out.println("Unknown option.");
                }
            } catch (RuntimeException exception) {
                System.out.println("Operation failed: " + exception.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n1. Show catalog\n2. Build gaming PC and add to cart"
                + "\n3. Add warranty to last item\n4. Apply 5% discount"
                + "\n5. Show cart\n6. Checkout\n0. Exit");
    }

    private static void addGamingPc(PCConfigurationDirector director, ShoppingCart cart) {
        PCConfiguration pc = director.createGamingPC(new StandardPCConfigurationBuilder());
        cart.add(pc, 1);
        System.out.println("Gaming PC added.");
    }

    private static void addWarrantyToLastItem(ShoppingCart cart) {
        List<CartItem> items = cart.getItems();
        if (items.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }
        CartItem last = items.get(items.size() - 1);
        if (last.item() instanceof ExtendedWarrantyDecorator) {
            System.out.println("Extended warranty is already applied to this item.");
            return;
        }
        Purchasable decorated = new ExtendedWarrantyDecorator(last.item(), WARRANTY_FEE);
        cart.replaceAt(items.size() - 1, decorated, last.quantity());
        System.out.println("Warranty added to last cart item.");
    }

    private static void showCart(ShoppingCart cart) {
        List<CartItem> items = cart.getItems();
        if (items.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }
        for (CartItem item : items) {
            BigDecimal lineTotal = item.item().getCurrentPrice()
                    .multiply(BigDecimal.valueOf(item.quantity()));
            System.out.println(item.quantity() + " x " + item.item().getDisplayName()
                    + " = " + lineTotal + " KZT");
        }
        System.out.println("Subtotal: " + cart.subtotal() + " KZT | "
                + cart.discountDescription() + ": " + cart.discount() + " KZT | Total: "
                + cart.total() + " KZT");
    }

    private static void checkout(ShoppingCart cart) {
        Order order = new CheckoutFacade(cart, new SqliteCheckoutRepository()).checkout();
        System.out.println("Order created: " + order.id() + " | total " + order.total() + " KZT");
    }
}
