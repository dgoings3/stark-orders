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
        if (expenseRepository.count() == 0) {
            Expense expense = new Expense();
            expense.setChicksCost(0.0);
            expense.setFeedCost(0.0);
            expense.setBeddingCost(0.0);
            expense.setLaborCost(0.0);
            expense.setMiscCost(0.0);
            expenseRepository.save(expense);
        }
    }
}