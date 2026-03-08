package com.starknakedpoultry.starkorders;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Controller
public class OrderController {

    private static final double PRICE_PER_LB = 3.75;

    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final EmailService emailService;

    public OrderController(OrderRepository orderRepository,
                           InventoryRepository inventoryRepository,
                           EmailService emailService) {
        this.orderRepository = orderRepository;
        this.inventoryRepository = inventoryRepository;
        this.emailService = emailService;
    }

    @GetMapping("/order")
    public String orderPage(Model model) {
        Inventory inv = inventoryRepository.findById(1L).orElseThrow();
        model.addAttribute("remaining", inv.getBirdsRemaining());
        return "order";
    }

    @PostMapping("/checkout")
    public String checkout(
            @RequestParam String customerName,
            @RequestParam String phone,
            @RequestParam int quantity,
            @RequestParam String pickupDate,
            @RequestParam String pickupTime,
            @RequestParam(required = false) String notes,
            Model model
    ) {
        Inventory inv = inventoryRepository.findById(1L).orElseThrow();
        int remaining = inv.getBirdsRemaining();

        if (quantity <= 0) {
            quantity = 1;
        }

        if (quantity > remaining) {
            model.addAttribute("remaining", remaining);
            model.addAttribute("error", "Not enough birds available. Only " + remaining + " left.");
            return "order";
        }

        inv.setBirdsAvailable(inv.getBirdsAvailable() - quantity);
        inventoryRepository.save(inv);

        Order order = new Order();
        order.setCustomerName(customerName);
        order.setPhone(phone);
        order.setQuantity(quantity);
        order.setPickupDate(pickupDate);
        order.setPickupTime(pickupTime);
        order.setNotes(notes == null ? "" : notes);
        order.setSource(OrderSource.WEBSITE);
        order.setStatus(OrderStatus.NEW);

        orderRepository.save(order);
        emailService.sendNewOrderEmail(order);

        LocalDate date = LocalDate.parse(pickupDate);
        LocalTime time = LocalTime.parse(pickupTime);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

        String pickupDateTime = date.format(dateFormatter) + " at " + time.format(timeFormatter);

        model.addAttribute("customerName", customerName);
        model.addAttribute("quantity", quantity);
        model.addAttribute("pickupDateTime", pickupDateTime);
        model.addAttribute("totalPrice", quantity * PRICE_PER_LB);

        return "success";
    }
}