package com.example;

import com.example.data.model.ExpenseCategory;
import com.example.data.model.IncomeCategory;
import com.example.data.model.ParsedVoiceTransaction;
import com.example.data.model.TransactionType;
import com.example.ui.components.VoiceTransactionParser;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class VoiceTransactionParserTest {

    @Test
    public void testSpentOnSnacks() {
        List<ParsedVoiceTransaction> list = VoiceTransactionParser.parse("Spent 50 rupees on snacks");
        assertFalse(list.isEmpty());
        ParsedVoiceTransaction tx = list.get(0);
        assertEquals(50.0, tx.amount, 0.01);
        assertEquals(TransactionType.EXPENSE, tx.type);
        assertEquals(ExpenseCategory.FOOD.label, tx.category.label);
        assertEquals("Snacks", tx.title);
    }

    @Test
    public void testPaidForPetrol() {
        List<ParsedVoiceTransaction> list = VoiceTransactionParser.parse("Paid 100 for petrol");
        assertFalse(list.isEmpty());
        ParsedVoiceTransaction tx = list.get(0);
        assertEquals(100.0, tx.amount, 0.01);
        assertEquals(TransactionType.EXPENSE, tx.type);
        assertEquals(ExpenseCategory.TRANSPORT.label, tx.category.label);
        assertEquals("Petrol", tx.title);
    }

    @Test
    public void testMomGave500Rupees() {
        List<ParsedVoiceTransaction> list = VoiceTransactionParser.parse("Mom gave me 500 rupees");
        assertFalse(list.isEmpty());
        ParsedVoiceTransaction tx = list.get(0);
        assertEquals(500.0, tx.amount, 0.01);
        assertEquals(TransactionType.INCOME, tx.type);
        assertEquals(IncomeCategory.ALLOWANCE.label, tx.category.label);
    }

    @Test
    public void testMultipleTransactionsInOneSentence() {
        List<ParsedVoiceTransaction> list = VoiceTransactionParser.parse("Bought books for 300 rs and spent 50 on chai");
        assertEquals(2, list.size());

        ParsedVoiceTransaction tx1 = list.get(0);
        assertEquals(300.0, tx1.amount, 0.01);
        assertEquals(TransactionType.EXPENSE, tx1.type);
        assertEquals(ExpenseCategory.STUDY.label, tx1.category.label);

        ParsedVoiceTransaction tx2 = list.get(1);
        assertEquals(50.0, tx2.amount, 0.01);
        assertEquals(TransactionType.EXPENSE, tx2.type);
        assertEquals(ExpenseCategory.FOOD.label, tx2.category.label);
    }
}
