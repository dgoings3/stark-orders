package com.starknakedpoultry.starkorders;

import com.starknakedpoultry.starkorders.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {}