import packages.*;
import packages.DataBase.DatabaseConnection;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);

    private static final ClientManager   clientManager   = new ClientManager();
    private static final SupplierManager supplierManager = new SupplierManager();
    private static final UserManager     userManager     = new UserManager();

    // ─── Entry point ────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        try {
            DatabaseConnection.getConnection();
            printTitle("DATABASE CONNECTION SUCCESSFUL");
        } catch (Exception e) {
            System.err.println("  ✘ Unable to connect to the database.");
            System.err.println("    → Make sure XAMPP (MySQL) is running.");
            System.err.println("    → " + e.getMessage());
            return;
        }

        clientManager.loadFromDB();
        supplierManager.loadFromDB();
        userManager.loadFromDB();

        boolean quit = false;
        while (!quit) {
            showMainMenu();
            switch (readInt("  Your choice: ")) {
                case 1  -> menuProductTypes();
                case 2  -> menuProducts();
                case 3  -> menuSuppliers();
                case 4  -> menuClients();
                case 5  -> menuUsers();
                case 6  -> menuPurchases();
                case 7  -> menuSales();
                case 8  -> menuHistory();
                case 9  -> menuPayments();
                case 0  -> quit = true;
                default -> error("Invalid option.");
            }
        }

        DatabaseConnection.close();
        System.out.println("\n  Goodbye!\n");
    }

    // ─── Main menu ──────────────────────────────────────────────────────────────

    private static void showMainMenu() {
        System.out.println();
        System.out.println("  ╔════════════════════════════════════════════════╗");
        System.out.println("  ║       STOCK MANAGEMENT – MAIN MENU             ║");
        System.out.println("  ╠════════════════════════════════════════════════╣");
        System.out.println("  ║  1. Product types                              ║");
        System.out.println("  ║  2. Products                                   ║");
        System.out.println("  ║  3. Suppliers                                  ║");
        System.out.println("  ║  4. Clients                                    ║");
        System.out.println("  ║  5. Users                                      ║");
        System.out.println("  ║  6. Purchases  (supplier → stock)              ║");
        System.out.println("  ║  7. Sales      (stock → client)                ║");
        System.out.println("  ║  8. Transaction history                        ║");
        System.out.println("  ║  9. Debt Payments                              ║");
        System.out.println("  ║  0. Quit                                       ║");
        System.out.println("  ╚════════════════════════════════════════════════╝");
        printSeparator();
    }

   
    private static void menuProductTypes() {
        boolean back = false;
        while (!back) {
            printSection("PRODUCT TYPES");
            System.out.println("  1. Add a type");
            System.out.println("  2. Display all types");
            System.out.println("  3. Edit a type");
            System.out.println("  4. Delete a type");
            System.out.println("  0. Back");
            printSeparator();
            switch (readInt("  Your choice: ")) {
                case 1 -> {
                    String name = readString("  Type name: ");
                    Type_product type = new Type_product(name);
                    if (type.save()) success("Type '" + name + "' added (id=" + type.getId() + ").");
                    else             error("Failed to add.");
                }
                case 2 -> {
                    List<Type_product> types = Type_product.findAll();
                    if (types.isEmpty()) { info("No types registered."); break; }
                    printSection("TYPE LIST");
                    types.forEach(System.out::println);
                }
                case 3 -> {
                    List<Type_product> types = Type_product.findAll();
                    if (types.isEmpty()) { info("No types to edit."); break; }
                    types.forEach(System.out::println);
                    int id = readInt("  Type ID to edit: ");
                    Type_product type = Type_product.findById(id);
                    if (type == null) { error("Type not found."); break; }
                    String newName = readStringOr("  New name [" + type.getNom() + "]: ", type.getNom());
                    type.setNom(newName);
                    if (type.update()) success("Type updated.");
                    else               error("Failed.");
                }
                case 4 -> {
                    List<Type_product> types = Type_product.findAll();
                    if (types.isEmpty()) { info("No types to delete."); break; }
                    types.forEach(System.out::println);
                    int id = readInt("  Type ID to delete: ");
                    Type_product type = Type_product.findById(id);
                    if (type == null) { error("Type not found."); break; }
                    if (confirm("Delete '" + type.getNom() + "'?")) {
                        if (type.delete()) success("Type deleted.");
                        else               error("Failed (type used by products?).");
                    }
                }
                case 0 -> back = true;
                default -> error("Invalid option.");
            }
        }
    }

    private static void menuProducts() {
        boolean back = false;
        while (!back) {
            printSection("PRODUCTS");
            System.out.println("  1. Add a product");
            System.out.println("  2. Display all products");
            System.out.println("  3. Edit a product");
            System.out.println("  4. Delete a product");
            System.out.println("  5. Search a product by ID");
            System.out.println("  0. Back");
            printSeparator();
            switch (readInt("  Your choice: ")) {
                case 1 -> {
                    List<Type_product> types = Type_product.findAll();
                    if (types.isEmpty()) { error("No product types. Please add one first."); break; }
                    printSection("AVAILABLE TYPES");
                    types.forEach(System.out::println);
                    int typeId = readInt("  Type ID: ");
                    Type_product type = Type_product.findById(typeId);
                    if (type == null) { error("Type not found."); break; }
                    String name  = readString("  Name: ");
                    double price = readDouble("  Unit price (Ar): ");
                    String unit  = readString("  Unit (kg / piece / liter …): ");
                    int    stock = readInt("  Initial stock: ");
                    Product product = new Product(stock, name, price, unit, type);
                    if (product.save()) success("Product '" + name + "' added (id=" + product.getId() + ").");
                    else                error("Failed to add.");
                }
                case 2 -> {
                    List<Product> products = Product.findAll();
                    if (products.isEmpty()) { info("No products registered."); break; }
                    printSection("PRODUCT LIST");
                    products.forEach(System.out::println);
                }
                case 3 -> {
                    List<Product> products = Product.findAll();
                    if (products.isEmpty()) { info("No products to edit."); break; }
                    products.forEach(System.out::println);
                    int id = readInt("  Product ID to edit: ");
                    Product product = Product.findById(id);
                    if (product == null) { error("Product not found."); break; }
                    info("(Press Enter to keep the current value)");
                    String newName  = readStringOr("  Name [" + product.getDesignation() + "]: ", product.getDesignation());
                    double newPrice = readDoubleOr("  Unit price [" + product.getPrixUnitaire() + "]: ", product.getPrixUnitaire());
                    String newUnit  = readStringOr("  Unit [" + product.getUnite() + "]: ", product.getUnite());
                    List<Type_product> types = Type_product.findAll();
                    types.forEach(System.out::println);
                    int typeId = readInt("  Type ID [0 = keep current]: ");
                    Type_product newType = (typeId == 0) ? product.getTypeProduct() : Type_product.findById(typeId);
                    if (product.modify(newName, newPrice, newUnit, newType)) success("Product updated.");
                    else                                                      error("Failed.");
                }
                case 4 -> {
                    List<Product> products = Product.findAll();
                    if (products.isEmpty()) { info("No products to delete."); break; }
                    products.forEach(System.out::println);
                    int id = readInt("  Product ID to delete: ");
                    Product product = Product.findById(id);
                    if (product == null) { error("Product not found."); break; }
                    if (confirm("Delete '" + product.getDesignation() + "'?")) {
                        if (product.delete()) success("Product deleted.");
                        else                  error("Failed (product referenced in purchases/sales?).");
                    }
                }
                case 5 -> {
                    int id = readInt("  Product ID: ");
                    Product product = Product.findById(id);
                    if (product == null) error("Product not found.");
                    else                 product.afficher();
                }
                case 0 -> back = true;
                default -> error("Invalid option.");
            }
        }
    }

    private static void menuSuppliers() {
        boolean back = false;
        while (!back) {
            printSection("SUPPLIERS");
            System.out.println("  1. Add a supplier");
            System.out.println("  2. Display all suppliers");
            System.out.println("  3. Edit a supplier");
            System.out.println("  4. Delete a supplier");
            System.out.println("  5. Pay a debt");
            System.out.println("  0. Back");
            printSeparator();
            switch (readInt("  Your choice: ")) {
                case 1 -> {
                    String name    = readString("  Supplier name: ");
                    String phone   = readString("  Phone: ");
                    String address = readString("  Address: ");
                    double debt    = readDouble("  Initial debt (Ar) [0]: ");
                    Supplier supplier = new Supplier(name, phone, address, debt);
                    if (supplierManager.addSupplier(supplier))
                        success("Supplier added (code=" + supplier.getSupplierCode() + ").");
                }
                case 2 -> supplierManager.displaySuppliers();
                case 3 -> {
                    supplierManager.displaySuppliers();
                    if (supplierManager.getSuppliers().isEmpty()) break;
                    int code = readInt("  Supplier code to edit: ");
                    Supplier supplier = supplierManager.findByCode(code);
                    if (supplier == null) { error("Supplier not found."); break; }
                    info("(Enter = keep current value)");
                    String newName    = readStringOr("  Name [" + supplier.getSupplierName() + "]: ", supplier.getSupplierName());
                    String newPhone   = readStringOr("  Phone [" + supplier.getTelephone() + "]: ", supplier.getTelephone());
                    String newAddress = readStringOr("  Address [" + supplier.getAddress() + "]: ", supplier.getAddress());
                    double newDebt    = readDoubleOr("  Debt [" + supplier.getDebtSupplier() + "]: ", supplier.getDebtSupplier());
                    supplierManager.modifySupplier(code, newName, newPhone, newAddress, newDebt);
                }
                case 4 -> {
                    supplierManager.displaySuppliers();
                    if (supplierManager.getSuppliers().isEmpty()) break;
                    int code = readInt("  Supplier code to delete: ");
                    Supplier supplier = supplierManager.findByCode(code);
                    if (supplier == null) { error("Supplier not found."); break; }
                    if (confirm("Delete '" + supplier.getSupplierName() + "'?"))
                        supplierManager.deleteSupplier(code);
                }
                case 5 -> {
                    supplierManager.displaySuppliers();
                    if (supplierManager.getSuppliers().isEmpty()) break;
                    int code = readInt("  Supplier code: ");
                    Supplier supplier = supplierManager.findByCode(code);
                    if (supplier == null) { error("Supplier not found."); break; }
                    double amount = readDouble("  Amount to pay (Ar): ");
                    supplierManager.paySupplierDebt(code, amount);
                }
                case 0 -> back = true;
                default -> error("Invalid option.");
            }
        }
    }

    private static void menuClients() {
        boolean back = false;
        while (!back) {
            printSection("CLIENTS");
            System.out.println("  1. Add a client");
            System.out.println("  2. Display all clients");
            System.out.println("  3. Edit a client");
            System.out.println("  4. Delete a client");
            System.out.println("  5. Pay a debt");
            System.out.println("  0. Back");
            printSeparator();
            switch (readInt("  Your choice: ")) {
                case 1 -> {
                    String name  = readString("  Client name: ");
                    String phone = readString("  Phone: ");
                    double debt  = readDouble("  Initial debt (Ar) [0]: ");
                    Client client = new Client(name, phone, debt);
                    if (clientManager.addClient(client))
                        success("Client added (code=" + client.getCode() + ").");
                }
                case 2 -> clientManager.displayClients();
                case 3 -> {
                    clientManager.displayClients();
                    if (clientManager.getClients().isEmpty()) break;
                    int code = readInt("  Client code to edit: ");
                    Client client = clientManager.findByCode(code);
                    if (client == null) { error("Client not found."); break; }
                    info("(Enter = keep current value)");
                    String newName  = readStringOr("  Name [" + client.getName() + "]: ", client.getName());
                    String newPhone = readStringOr("  Phone [" + client.getPhone() + "]: ", client.getPhone());
                    double newDebt  = readDoubleOr("  Debt [" + client.getDebt() + "]: ", client.getDebt());
                    clientManager.modifyClient(code, newName, newPhone, newDebt);
                }
                case 4 -> {
                    clientManager.displayClients();
                    if (clientManager.getClients().isEmpty()) break;
                    int code = readInt("  Client code to delete: ");
                    Client client = clientManager.findByCode(code);
                    if (client == null) { error("Client not found."); break; }
                    if (confirm("Delete '" + client.getName() + "'?"))
                        clientManager.deleteClient(code);
                }
                case 5 -> {
                    clientManager.displayClients();
                    if (clientManager.getClients().isEmpty()) break;
                    int code = readInt("  Client code: ");
                    Client client = clientManager.findByCode(code);
                    if (client == null) { error("Client not found."); break; }
                    double amount = readDouble("  Amount to pay (Ar): ");
                    clientManager.payClientDebt(code, amount);
                }
                case 0 -> back = true;
                default -> error("Invalid option.");
            }
        }
    }

    private static void menuUsers() {
        boolean back = false;
        while (!back) {
            printSection("USERS");
            System.out.println("  1. Add a user");
            System.out.println("  2. Display all users");
            System.out.println("  3. Display active users");
            System.out.println("  4. Display inactive users");
            System.out.println("  5. Edit a user");
            System.out.println("  6. Enable / Disable an account");
            System.out.println("  7. Delete a user");
            System.out.println("  0. Back");
            printSeparator();
            switch (readInt("  Your choice: ")) {
                case 1 -> {
                    String uid      = readString("  User ID (e.g. U001): ");
                    String name     = readString("  Full name: ");
                    String login    = readString("  Login: ");
                    String password = readString("  Password: ");
                    info("Available roles: ADMIN, SELLER, STOCK");
                    String role = readString("  Role: ").toUpperCase();
                    User user = new User(uid, name, login, password, role, true);
                    userManager.addUser(user);
                }
                case 2 -> userManager.displayUsers();
                case 3 -> userManager.displayActiveUsers();
                case 4 -> userManager.displayInactiveUsers();
                case 5 -> {
                    userManager.displayUsers();
                    if (userManager.getUsers().isEmpty()) break;
                    String uid = readString("  User ID to edit: ");
                    User user = userManager.findById(uid);
                    if (user == null) { error("User not found."); break; }
                    info("(Enter = keep current value)");
                    String newName     = readStringOr("  Name [" + user.getUserName() + "]: ", user.getUserName());
                    String newLogin    = readStringOr("  Login [" + user.getLogin() + "]: ", user.getLogin());
                    String newPassword = readStringOr("  Password [****]: ", user.getPassword());
                    String newRole     = readStringOr("  Role [" + user.getRole() + "]: ", user.getRole()).toUpperCase();
                    userManager.modifyUser(uid, newName, newLogin, newPassword, newRole);
                }
                case 6 -> {
                    userManager.displayUsers();
                    if (userManager.getUsers().isEmpty()) break;
                    String uid = readString("  User ID: ");
                    User user = userManager.findById(uid);
                    if (user == null) { error("User not found."); break; }
                    info("Account currently: " + (user.isActive() ? "ACTIVE" : "INACTIVE"));
                    boolean active = readString("  Enable? (y/n): ").equalsIgnoreCase("y");
                    userManager.setActive(uid, active);
                }
                case 7 -> {
                    userManager.displayUsers();
                    if (userManager.getUsers().isEmpty()) break;
                    String uid = readString("  User ID to delete: ");
                    User user = userManager.findById(uid);
                    if (user == null) { error("User not found."); break; }
                    if (confirm("Delete '" + user.getUserName() + "'?"))
                        userManager.deleteUser(uid);
                }
                case 0 -> back = true;
                default -> error("Invalid option.");
            }
        }
    }

    private static void menuPurchases() {
        boolean back = false;
        while (!back) {
            printSection("PURCHASES — Supplier → Stock");
            System.out.println("  1. Record a purchase");
            System.out.println("  2. All purchases history");
            System.out.println("  3. History by supplier");
            System.out.println("  4. History by product");
            System.out.println("  0. Back");
            printSeparator();
            switch (readInt("  Your choice: ")) {
                case 1 -> recordPurchase();
                case 2 -> {
                    List<Purchase> purchases = Purchase.findAll();
                    if (purchases.isEmpty()) { info("No purchases recorded."); break; }
                    printSection("PURCHASE HISTORY (" + purchases.size() + ")");
                    double total = 0;
                    for (Purchase p : purchases) { p.afficher(); total += p.getTotal(); }
                    System.out.printf("  ► Grand total: %.2f Ar%n", total);
                }
                case 3 -> {
                    supplierManager.displaySuppliers();
                    if (supplierManager.getSuppliers().isEmpty()) break;
                    int code = readInt("  Supplier code: ");
                    Supplier supplier = supplierManager.findByCode(code);
                    if (supplier == null) { error("Supplier not found."); break; }
                    List<Purchase> purchases = Purchase.findBySupplier(code);
                    if (purchases.isEmpty()) { info("No purchases for this supplier."); break; }
                    printSection("PURCHASES — " + supplier.getSupplierName());
                    purchases.forEach(Purchase::afficher);
                }
                case 4 -> {
                    List<Product> products = Product.findAll();
                    if (products.isEmpty()) { info("No products."); break; }
                    products.forEach(System.out::println);
                    int id = readInt("  Product ID: ");
                    Product product = Product.findById(id);
                    if (product == null) { error("Product not found."); break; }
                    List<Purchase> purchases = Purchase.findByProduct(id);
                    if (purchases.isEmpty()) { info("No purchases for this product."); break; }
                    printSection("PURCHASES — " + product.getDesignation());
                    purchases.forEach(Purchase::afficher);
                }
                case 0 -> back = true;
                default -> error("Invalid option.");
            }
        }
    }

    private static void recordPurchase() {
        supplierManager.displaySuppliers();
        if (supplierManager.getSuppliers().isEmpty()) { error("Please add a supplier first."); return; }
        int supplierCode = readInt("  Supplier code: ");
        Supplier supplier = supplierManager.findByCode(supplierCode);
        if (supplier == null) { error("Supplier not found."); return; }

        List<Product> products = Product.findAll();
        if (products.isEmpty()) { error("No products. Please add one first."); return; }
        printSection("PRODUCTS");
        products.forEach(System.out::println);
        int productId = readInt("  Product ID: ");
        Product product = Product.findById(productId);
        if (product == null) { error("Product not found."); return; }

        int    quantity = readInt("  Quantity purchased: ");
        double price    = readDouble("  Purchase unit price (Ar): ");

        printSeparator();
        System.out.println("  ── Summary ─────────────────────────────────");
        System.out.println("  Supplier   : " + supplier.getSupplierName());
        System.out.println("  Product    : " + product.getDesignation());
        System.out.printf ("  Quantity   : %d %s%n", quantity, product.getUnite());
        System.out.printf ("  Unit price : %.2f Ar%n", price);
        System.out.printf ("  TOTAL      : %.2f Ar%n", (double) quantity * price);
        printSeparator();

        if (confirm("Confirm purchase?")) {
            Purchase purchase = new Purchase(supplier, product, quantity, price);
            if (purchase.process()) {
                success("Purchase recorded (id=" + purchase.getId() + ").");
                info("New stock of '" + product.getDesignation() + "': " + product.getStock());
            } else {
                error("Failed to record.");
            }
        }
    }

    private static void menuSales() {
        boolean back = false;
        while (!back) {
            printSection("SALES — Stock → Client");
            System.out.println("  1. Record a sale");
            System.out.println("  2. All sales history");
            System.out.println("  3. History by client");
            System.out.println("  4. History by product");
            System.out.println("  0. Back");
            printSeparator();
            switch (readInt("  Your choice: ")) {
                case 1 -> recordSale();
                case 2 -> {
                    List<Sale> sales = Sale.findAll();
                    if (sales.isEmpty()) { info("No sales recorded."); break; }
                    printSection("SALES HISTORY (" + sales.size() + ")");
                    double total = 0;
                    for (Sale s : sales) { s.afficher(); total += s.getTotal(); }
                    System.out.printf("  ► Grand total: %.2f Ar%n", total);
                }
                case 3 -> {
                    clientManager.displayClients();
                    if (clientManager.getClients().isEmpty()) break;
                    int code = readInt("  Client code: ");
                    Client client = clientManager.findByCode(code);
                    if (client == null) { error("Client not found."); break; }
                    List<Sale> sales = Sale.findByClient(code);
                    if (sales.isEmpty()) { info("No sales for this client."); break; }
                    printSection("SALES — " + client.getName());
                    sales.forEach(Sale::afficher);
                }
                case 4 -> {
                    List<Product> products = Product.findAll();
                    if (products.isEmpty()) { info("No products."); break; }
                    products.forEach(System.out::println);
                    int id = readInt("  Product ID: ");
                    Product product = Product.findById(id);
                    if (product == null) { error("Product not found."); break; }
                    List<Sale> sales = Sale.findByProduct(id);
                    if (sales.isEmpty()) { info("No sales for this product."); break; }
                    printSection("SALES — " + product.getDesignation());
                    sales.forEach(Sale::afficher);
                }
                case 0 -> back = true;
                default -> error("Invalid option.");
            }
        }
    }

    private static void recordSale() {
        clientManager.displayClients();
        if (clientManager.getClients().isEmpty()) { error("Please add a client first."); return; }
        int clientCode = readInt("  Client code: ");
        Client client = clientManager.findByCode(clientCode);
        if (client == null) { error("Client not found."); return; }

        List<Product> products = Product.findAll();
        if (products.isEmpty()) { error("No products available."); return; }
        printSection("PRODUCTS IN STOCK");
        products.forEach(System.out::println);
        int productId = readInt("  Product ID: ");
        Product product = Product.findById(productId);
        if (product == null) { error("Product not found."); return; }

        info("Available stock: " + product.getStock() + " " + product.getUnite());
        int    quantity = readInt("  Quantity to sell: ");
        double price    = readDoubleOr("  Sale unit price (Ar) [" + product.getPrixUnitaire() + "]: ",
                                       product.getPrixUnitaire());

        printSeparator();
        System.out.println("  ── Summary ─────────────────────────────────");
        System.out.println("  Client     : " + client.getName());
        System.out.println("  Product    : " + product.getDesignation());
        System.out.printf ("  Quantity   : %d %s%n", quantity, product.getUnite());
        System.out.printf ("  Unit price : %.2f Ar%n", price);
        System.out.printf ("  TOTAL      : %.2f Ar%n", (double) quantity * price);
        printSeparator();

        if (confirm("Confirm sale?")) {
            Sale sale = new Sale(client, product, quantity, price);
            if (sale.process()) {
                success("Sale recorded (id=" + sale.getId() + ").");
                info("Remaining stock of '" + product.getDesignation() + "': " + product.getStock());
            } else {
                error("Failed to record.");
            }
        }
    }

    private static void menuHistory() {
        boolean back = false;
        while (!back) {
            printSection("TRANSACTION HISTORY");
            System.out.println("  1. All purchases");
            System.out.println("  2. All sales");
            System.out.println("  3. Stock status");
            System.out.println("  0. Back");
            printSeparator();
            switch (readInt("  Your choice: ")) {
                case 1 -> {
                    List<Purchase> purchases = Purchase.findAll();
                    if (purchases.isEmpty()) { info("No purchases."); break; }
                    printSection("ALL PURCHASES (" + purchases.size() + ")");
                    double total = 0;
                    for (Purchase p : purchases) { p.afficher(); total += p.getTotal(); }
                    System.out.printf("  ► Grand total of purchases: %.2f Ar%n", total);
                }
                case 2 -> {
                    List<Sale> sales = Sale.findAll();
                    if (sales.isEmpty()) { info("No sales."); break; }
                    printSection("ALL SALES (" + sales.size() + ")");
                    double total = 0;
                    for (Sale s : sales) { s.afficher(); total += s.getTotal(); }
                    System.out.printf("  ► Grand total of sales: %.2f Ar%n", total);
                }
                case 3 -> {
                    List<Product> products = Product.findAll();
                    if (products.isEmpty()) { info("No products."); break; }
                    printSection("STOCK STATUS");
                    System.out.printf("  %-5s %-30s %8s  %-8s  %s%n", "ID", "Name", "Stock", "Unit", "Alert");
                    printSeparator();
                    for (Product p : products) {
                        String alert = p.getStock() == 0 ? "⚠ OUT OF STOCK"
                                     : p.getStock() < 10 ? "⚠ LOW" : "";
                        System.out.printf("  %-5d %-30s %8d  %-8s  %s%n",
                                p.getId(), p.getDesignation(), p.getStock(), p.getUnite(), alert);
                    }
                }
                case 0 -> back = true;
                default -> error("Invalid option.");
            }
        }
    }

    private static void menuPayments() {
        boolean back = false;
        while (!back) {
            printSection("DEBT PAYMENTS");
            System.out.println("  1. Client pays their debt");
            System.out.println("  2. Supplier pays their debt");
            System.out.println("  0. Back");
            printSeparator();
            switch (readInt("  Your choice: ")) {
                case 1 -> {
                    clientManager.displayClients();
                    if (clientManager.getClients().isEmpty()) break;
                    int code = readInt("  Client code: ");
                    Client client = clientManager.findByCode(code);
                    if (client == null) { error("Client not found."); break; }
                    info("Current debt of " + client.getName() + ": " + String.format("%.2f Ar", client.getDebt()));
                    if (client.getDebt() == 0) { info("This client has no debt."); break; }
                    double amount = readDouble("  Amount to pay (Ar): ");
                    if (confirm("Confirm payment of " + String.format("%.2f Ar", amount) + " for " + client.getName() + "?"))
                        clientManager.payClientDebt(code, amount);
                }
                case 2 -> {
                    supplierManager.displaySuppliers();
                    if (supplierManager.getSuppliers().isEmpty()) break;
                    int code = readInt("  Supplier code: ");
                    Supplier supplier = supplierManager.findByCode(code);
                    if (supplier == null) { error("Supplier not found."); break; }
                    info("Current debt of " + supplier.getSupplierName() + ": " + String.format("%.2f Ar", supplier.getDebtSupplier()));
                    if (supplier.getDebtSupplier() == 0) { info("This supplier has no debt."); break; }
                    double amount = readDouble("  Amount to pay (Ar): ");
                    if (confirm("Confirm payment of " + String.format("%.2f Ar", amount) + " for " + supplier.getSupplierName() + "?"))
                        supplierManager.paySupplierDebt(code, amount);
                }
                case 0 -> back = true;
                default -> error("Invalid option.");
            }
        }
    }


    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (input.isEmpty()) return 0;
            try { return Integer.parseInt(input); }
            catch (NumberFormatException e) { error("Please enter a whole number."); }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim().replace(",", ".");
            if (input.isEmpty()) return 0.0;
            try { return Double.parseDouble(input); }
            catch (NumberFormatException e) { error("Please enter a number (e.g. 1500.50)."); }
        }
    }

    private static String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (!input.isEmpty()) return input;
            error("This field cannot be empty.");
        }
    }

    private static String readStringOr(String prompt, String defaultValue) {
        System.out.print(prompt);
        String input = sc.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }

    private static double readDoubleOr(String prompt, double defaultValue) {
        System.out.print(prompt);
        String input = sc.nextLine().trim().replace(",", ".");
        if (input.isEmpty()) return defaultValue;
        try { return Double.parseDouble(input); }
        catch (NumberFormatException e) { return defaultValue; }
    }

    private static boolean confirm(String question) {
        System.out.print("  " + question + " (y/n): ");
        return sc.nextLine().trim().equalsIgnoreCase("y");
    }


    private static void printTitle(String title) {
        System.out.println();
        System.out.println("  ══════════════════════════════════════════════════");
        System.out.println("   " + title);
        System.out.println("  ══════════════════════════════════════════════════");
    }

    private static void printSection(String title)  { System.out.println("\n  ──── " + title + " ────"); }
    private static void printSeparator()             { System.out.println("  ──────────────────────────────────────────────"); }
    private static void success(String msg)          { System.out.println("  ✔ " + msg); }
    private static void error(String msg)            { System.out.println("  ✘ " + msg); }
    private static void info(String msg)             { System.out.println("  ℹ " + msg); }
}