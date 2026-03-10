package com.starknakedpoultry.starkorders;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ExpenseInit implements CommandLineRunner {

    private final ExpenseRepository expenseRepository;

    public ExpenseInit(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    public void run(String... args) {
        // Leave blank for now so no old expense data gets seeded
    }
}