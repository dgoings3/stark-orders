package com.starknakedpoultry.starkorders;

import com.starknakedpoultry.starkorders.Inventory;
import com.starknakedpoultry.starkorders.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InventoryInit implements CommandLineRunner {

    private final InventoryRepository inventoryRepository;

    public InventoryInit(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public void run(String... args) {
        inventoryRepository.findById(1L).orElseGet(() -> {
            Inventory inv = new Inventory();
            inv.setBirdsAvailable(0);
            inv.setBirdsLost(0);
            return inventoryRepository.save(inv);
        });
    }
}