package com.starknakedpoultry.starkorders;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final InventoryRepository inventoryRepository;
    private final OrderRepository orderRepository;
    private final EmailService emailService;
    private final ExpenseRepository expenseRepository;
    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(InventoryRepository inventoryRepository,
                           OrderRepository orderRepository,
                           EmailService emailService,
                           ExpenseRepository expenseRepository,
                           AdminUserRepository adminUserRepository,
                           PasswordEncoder passwordEncoder) {
        this.inventoryRepository = inventoryRepository;
        this.orderRepository = orderRepository;
        this.emailService = emailService;
        this.expenseRepository = expenseRepository;
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Order> fulfilledOrders = orderRepository.findByStatusOrderByIdDesc(OrderStatus.FULFILLED);

        double totalRevenue = fulfilledOrders.stream()
                .mapToDouble(order -> order.getTotalPrice() == null ? 0.0 : order.getTotalPrice())
                .sum();

        double totalWeightSold = fulfilledOrders.stream()
                .mapToDouble(order -> order.getTotalWeight() == null ? 0.0 : order.getTotalWeight())
                .sum();

        int totalBirdsSold = fulfilledOrders.stream()
                .mapToInt(Order::getQuantity)
                .sum();

        Expense expense = expenseRepository.findAll().stream().findFirst().orElseThrow();
        double totalExpenses = expense.getTotalExpenses();
        double profitOrLoss = totalRevenue - totalExpenses;

        model.addAttribute("remaining", inventoryRepository.findById(1L).orElseThrow().getBirdsRemaining());
        model.addAttribute("totalRevenue", String.format("%.2f", totalRevenue));
        model.addAttribute("totalExpenses", String.format("%.2f", totalExpenses));
        model.addAttribute("profitOrLoss", String.format("%.2f", profitOrLoss));
        model.addAttribute("totalBirdsSold", totalBirdsSold);
        model.addAttribute("totalWeightSold", String.format("%.2f", totalWeightSold));

        return "admin_dashboard";
    }

    @GetMapping("/expenses")
    public String expensesPage(Model model) {
        Expense expense = expenseRepository.findAll().stream().findFirst().orElseThrow();
        model.addAttribute("expense", expense);
        return "admin_expenses";
    }

    @PostMapping("/expenses")
    public String updateExpenses(@RequestParam double chicksCost,
                                 @RequestParam double feedCost,
                                 @RequestParam double beddingCost,
                                 @RequestParam double laborCost,
                                 @RequestParam double miscCost) {
        Expense expense = expenseRepository.findAll().stream().findFirst().orElseThrow();

        expense.setChicksCost(Math.max(0, chicksCost));
        expense.setFeedCost(Math.max(0, feedCost));
        expense.setBeddingCost(Math.max(0, beddingCost));
        expense.setLaborCost(Math.max(0, laborCost));
        expense.setMiscCost(Math.max(0, miscCost));

        expenseRepository.save(expense);

        return "redirect:/admin/dashboard";
    }

    @GetMapping("/inventory")
    public String inventoryPage(Model model) {
        model.addAttribute("inv", inventoryRepository.findById(1L).orElseThrow());
        return "admin_inventory";
    }

    @PostMapping("/inventory")
    public String updateInventory(@RequestParam int birdsAvailable, @RequestParam int birdsLost) {
        Inventory inv = inventoryRepository.findById(1L).orElseThrow();

        inv.setBirdsAvailable(Math.max(0, birdsAvailable));
        inv.setBirdsLost(Math.max(0, birdsLost));

        inventoryRepository.save(inv);

        return "redirect:/admin/inventory";
    }

    @GetMapping("/orders")
    public String ordersPage(Model model) {
        model.addAttribute("orders", orderRepository.findByStatusOrderByIdDesc(OrderStatus.NEW));
        model.addAttribute("remaining", inventoryRepository.findById(1L).orElseThrow().getBirdsRemaining());
        return "admin_orders";
    }

    @GetMapping("/history")
    public String historyPage(Model model) {
        model.addAttribute(
                "orders",
                orderRepository.findByStatusInOrderByIdDesc(List.of(OrderStatus.FULFILLED, OrderStatus.CANCELED))
        );
        return "admin_history";
    }

    @PostMapping("/orders/{id}/cancel")
    public String cancelOrder(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElseThrow();

        if (order.getStatus() == OrderStatus.NEW) {
            Inventory inv = inventoryRepository.findById(1L).orElseThrow();

            inv.setBirdsAvailable(inv.getBirdsAvailable() + order.getQuantity());
            inventoryRepository.save(inv);

            order.setStatus(OrderStatus.CANCELED);
            orderRepository.save(order);
        }

        return "redirect:/admin/orders";
    }

    @GetMapping("/orders/{id}/fulfill")
    public String fulfillForm(@PathVariable Long id, Model model) {
        Order order = orderRepository.findById(id).orElseThrow();
        model.addAttribute("order", order);
        return "admin_fulfill_order";
    }

    @PostMapping("/orders/{id}/fulfill")
    public String fulfillOrder(@PathVariable Long id,
                               @RequestParam double totalWeight,
                               @RequestParam double totalPrice,
                               @RequestParam String paymentMethod) {
        Order order = orderRepository.findById(id).orElseThrow();

        if (order.getStatus() == OrderStatus.NEW) {
            order.setTotalWeight(totalWeight);
            order.setTotalPrice(totalPrice);
            order.setPaymentMethod(paymentMethod);
            order.setStatus(OrderStatus.FULFILLED);
            orderRepository.save(order);
        }

        return "redirect:/admin/history";
    }

    @GetMapping("/orders/{id}/edit-sale")
    public String editSaleForm(@PathVariable Long id, Model model) {
        Order order = orderRepository.findById(id).orElseThrow();
        model.addAttribute("order", order);
        return "admin_edit_sale";
    }

    @PostMapping("/orders/{id}/edit-sale")
    public String editSale(@PathVariable Long id,
                           @RequestParam double totalWeight,
                           @RequestParam double totalPrice,
                           @RequestParam String paymentMethod) {
        Order order = orderRepository.findById(id).orElseThrow();

        if (order.getStatus() == OrderStatus.FULFILLED) {
            order.setTotalWeight(totalWeight);
            order.setTotalPrice(totalPrice);
            order.setPaymentMethod(paymentMethod);
            orderRepository.save(order);
        }

        return "redirect:/admin/history";
    }

    @PostMapping("/orders/{id}/delete")
    public String deleteOrder(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElseThrow();

        if (order.getStatus() == OrderStatus.CANCELED || order.getStatus() == OrderStatus.FULFILLED) {
            orderRepository.delete(order);
        }

        return "redirect:/admin/history";
    }

    @GetMapping("/order/new")
    public String newManualOrderForm(Model model) {
        model.addAttribute("remaining", inventoryRepository.findById(1L).orElseThrow().getBirdsRemaining());
        return "admin_new_order";
    }

    @PostMapping("/order/new")
    public String createManualOrder(@RequestParam String customerName,
                                    @RequestParam int quantity,
                                    Model model) {
        Inventory inv = inventoryRepository.findById(1L).orElseThrow();
        int remaining = inv.getBirdsRemaining();

        if (quantity <= 0) {
            quantity = 1;
        }

        if (quantity > remaining) {
            model.addAttribute("remaining", remaining);
            model.addAttribute("error", "Not enough birds available. Only " + remaining + " left.");
            return "admin_new_order";
        }

        inv.setBirdsAvailable(inv.getBirdsAvailable() - quantity);
        inventoryRepository.save(inv);

        Order order = new Order();
        order.setCustomerName(customerName);
        order.setPhone("N/A");
        order.setQuantity(quantity);
        order.setPickupDate("TBD");
        order.setPickupTime("TBD");
        order.setNotes("");
        order.setSource(OrderSource.MANUAL);
        order.setStatus(OrderStatus.NEW);

        orderRepository.save(order);
        emailService.sendManualReservationEmail(order);

        return "redirect:/admin/orders";
    }

    @GetMapping("/change-password")
    public String changePasswordPage() {
        return "admin_change_password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Authentication authentication,
                                 Model model) {

        String username = authentication.getName();
        AdminUser adminUser = adminUserRepository.findByUsername(username).orElseThrow();

        if (!passwordEncoder.matches(currentPassword, adminUser.getPassword())) {
            model.addAttribute("error", "Current password is incorrect.");
            return "admin_change_password";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New passwords do not match.");
            return "admin_change_password";
        }

        if (newPassword.length() < 8) {
            model.addAttribute("error", "New password must be at least 8 characters.");
            return "admin_change_password";
        }

        adminUser.setPassword(passwordEncoder.encode(newPassword));
        adminUserRepository.save(adminUser);

        model.addAttribute("success", "Password changed successfully.");
        return "admin_change_password";
    }
}