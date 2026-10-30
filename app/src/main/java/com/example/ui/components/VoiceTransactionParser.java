package com.example.ui.components;

import com.example.data.model.CategoryItem;
import com.example.data.model.ExpenseCategory;
import com.example.data.model.IncomeCategory;
import com.example.data.model.ParsedVoiceTransaction;
import com.example.data.model.TransactionType;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VoiceTransactionParser {

    public static List<ParsedVoiceTransaction> parse(String speechInput) {
        List<ParsedVoiceTransaction> results = new ArrayList<>();
        if (speechInput == null || speechInput.trim().isEmpty()) {
            return results;
        }

        String raw = speechInput.trim();
        List<String> segments = splitMultipleSentences(raw);

        for (String segment : segments) {
            ParsedVoiceTransaction tx = parseSegment(segment, raw);
            if (tx != null && tx.amount > 0) {
                results.add(tx);
            }
        }

        // Fallback: If no amounts were parsed from splitting, try parsing the whole raw string
        if (results.isEmpty()) {
            ParsedVoiceTransaction tx = parseSegment(raw, raw);
            if (tx != null && tx.amount > 0) {
                results.add(tx);
            }
        }

        return results;
    }

    private static List<String> splitMultipleSentences(String text) {
        List<String> list = new ArrayList<>();
        // Split on common conjunctions and sentence breaks
        String[] parts = text.split("(?i)\\s+(?:and\\s+then|and|then|plus|also|comma)\\s+|\\s*;\\s*|\\s*,\\s*");
        for (String p : parts) {
            String trimmed = p.trim();
            if (!trimmed.isEmpty()) {
                list.add(trimmed);
            }
        }
        return list;
    }

    private static ParsedVoiceTransaction parseSegment(String segment, String originalFullText) {
        String lower = segment.toLowerCase(Locale.getDefault());

        // 1. Extract Amount
        double amount = extractAmount(lower);
        if (amount <= 0) {
            return null;
        }

        // 2. Detect Type (Income vs Expense)
        TransactionType type = detectType(lower);

        // 3. Detect Category
        CategoryItem category = detectCategory(lower, type);

        // 4. Extract Payment Method
        String paymentMethod = detectPaymentMethod(lower);

        // 5. Extract Timestamp
        long timestamp = detectTimestamp(lower);

        // 6. Generate Clean Title
        String title = generateTitle(segment, lower, amount, category, type);

        return new ParsedVoiceTransaction(
                amount,
                type,
                category,
                title,
                originalFullText,
                paymentMethod,
                timestamp
        );
    }

    private static double extractAmount(String text) {
        // Match numbers like 50, 100.50, 1000, 5k (5000), 1.5k (1500)
        Pattern kPattern = Pattern.compile("(?i)(\\d+(?:\\.\\d+)?)\\s*k\\b");
        Matcher kMatcher = kPattern.matcher(text);
        if (kMatcher.find()) {
            try {
                String group1 = kMatcher.group(1);
                if (group1 != null) {
                    return Double.parseDouble(group1) * 1000.0;
                }
            } catch (Exception ignored) {
            }
        }

        Pattern pattern = Pattern.compile("(?:rs\\.?|rupees?|inr|₹|bucks)?\\s*(\\d+(?:\\.\\d+)?)\\s*(?:rs\\.?|rupees?|inr|₹|bucks)?");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            try {
                String numStr = matcher.group(1);
                if (numStr != null) {
                    return Double.parseDouble(numStr);
                }
            } catch (Exception ignored) {
            }
        }
        return 0.0;
    }

    private static TransactionType detectType(String text) {
        String[] incomeTriggers = {
                "got", "received", "receive", "gave me", "given by", "credited", "earned",
                "income", "allowance", "pocket money", "salary", "stipend", "cashback",
                "refund", "sold", "scholarship", "freelance", "gift", "deposit", "bonus"
        };
        for (String trigger : incomeTriggers) {
            if (text.contains(trigger)) {
                return TransactionType.INCOME;
            }
        }

        String[] expenseTriggers = {
                "spent", "spend", "paid", "pay", "bought", "buy", "purchased", "gave",
                "cost", "debited", "bill", "fare", "recharge", "order", "fee", "ticket", "rent"
        };
        for (String trigger : expenseTriggers) {
            if (text.contains(trigger)) {
                return TransactionType.EXPENSE;
            }
        }

        return TransactionType.EXPENSE;
    }

    private static CategoryItem detectCategory(String text, TransactionType type) {
        if (type == TransactionType.INCOME) {
            if (containsAny(text, "pocket money", "allowance", "mom", "dad", "father", "mother", "parents", "uncle", "home")) {
                return IncomeCategory.ALLOWANCE.toItem();
            }
            if (containsAny(text, "job", "salary", "stipend", "work", "part time", "part-time")) {
                return IncomeCategory.PART_TIME.toItem();
            }
            if (containsAny(text, "freelance", "freelancing", "client", "project")) {
                return IncomeCategory.FREELANCE.toItem();
            }
            if (containsAny(text, "scholarship", "grant", "award")) {
                return IncomeCategory.SCHOLARSHIP.toItem();
            }
            if (containsAny(text, "gift", "gifts", "birthday", "diwali", "eid", "festival")) {
                return IncomeCategory.GIFTS.toItem();
            }
            if (containsAny(text, "cashback", "reward", "rewards", "scratch card", "paytm", "gpay", "phonepe")) {
                return IncomeCategory.CASHBACK.toItem();
            }
            if (containsAny(text, "sold", "sale", "olx")) {
                return IncomeCategory.SELLING.toItem();
            }
            return IncomeCategory.OTHER_INCOME.toItem();
        } else {
            if (containsAny(text, "snack", "snacks", "food", "chai", "tea", "coffee", "lunch", "dinner", "breakfast", "swiggy", "zomato", "burger", "pizza", "samosa", "restaurant", "canteen", "maggi", "mcdonald", "kfc", "juice", "ice cream", "biryani", "drinks", "eating", "eat", "mess", "tiffin", "cafe")) {
                return ExpenseCategory.FOOD.toItem();
            }
            if (containsAny(text, "petrol", "diesel", "transport", "cab", "uber", "ola", "auto", "bus", "train", "metro", "ticket", "fuel", "rapido", "rickshaw", "flight", "travel", "fare", "bike", "toll", "parking")) {
                return ExpenseCategory.TRANSPORT.toItem();
            }
            if (containsAny(text, "book", "books", "study", "pen", "notebook", "course", "udemy", "tuition", "exam", "xerox", "print", "stationery", "fees", "coaching", "pdf", "library", "class")) {
                return ExpenseCategory.STUDY.toItem();
            }
            if (containsAny(text, "shopping", "clothes", "shirt", "pants", "shoes", "dress", "myntra", "amazon", "flipkart", "watch", "sunglasses", "mall", "bag", "makeup", "store", "tshirt", "jeans")) {
                return ExpenseCategory.SHOPPING.toItem();
            }
            if (containsAny(text, "fun", "movie", "movies", "game", "gaming", "netflix", "spotify", "party", "outing", "concert", "steam", "playstation", "prime", "cinema", "pvr", "match")) {
                return ExpenseCategory.ENTERTAINMENT.toItem();
            }
            if (containsAny(text, "bill", "bills", "recharge", "electricity", "wifi", "broadband", "jio", "airtel", "water", "gas", "rent", "maintenance")) {
                return ExpenseCategory.BILLS.toItem();
            }
            if (containsAny(text, "health", "medicine", "medicines", "doctor", "pharmacy", "hospital", "gym", "medical", "clinic", "tablet", "pills", "dentist")) {
                return ExpenseCategory.HEALTH.toItem();
            }
            return ExpenseCategory.OTHERS.toItem();
        }
    }

    private static boolean containsAny(String text, String... keywords) {
        for (String kw : keywords) {
            if (text.contains(kw)) {
                return true;
            }
        }
        return false;
    }

    private static String detectPaymentMethod(String text) {
        if (containsAny(text, "cash", "notes", "change")) {
            return "Cash";
        }
        if (containsAny(text, "card", "debit", "credit")) {
            return "Card";
        }
        return "UPI";
    }

    private static long detectTimestamp(String text) {
        long now = System.currentTimeMillis();
        long dayMs = 24L * 60 * 60 * 1000L;
        if (text.contains("day before yesterday")) {
            return now - 2 * dayMs;
        } else if (text.contains("yesterday")) {
            return now - dayMs;
        }
        return now;
    }

    private static String generateTitle(String originalSegment, String lower, double amount, CategoryItem category, TransactionType type) {
        String clean = originalSegment;
        // Strip amount digits & currency words
        clean = clean.replaceAll("(?i)\\b(\\d+(?:\\.\\d+)?)\\b", "");
        clean = clean.replaceAll("(?i)\\b(rs|rupees?|inr|₹|bucks|k)\\b", "");

        // Strip common lead/trail verbs
        clean = clean.replaceAll("(?i)\\b(spent|spend|paid|pay|bought|buy|got|received|receive|gave|given|credited|for|on|from|to|a|an|the|me|my|worth|amount|rupee|rupees)\\b", " ");
        clean = clean.replaceAll("\\s+", " ").trim();

        if (clean.length() >= 2) {
            return capitalizeWord(clean);
        }

        // Fallback: If segment is e.g. "Mom gave me 500 rupees", keep descriptive "Mom gave me"
        if (lower.contains("mom") || lower.contains("dad")) {
            if (lower.contains("mom")) return "Mom's Allowance";
            if (lower.contains("dad")) return "Dad's Allowance";
        }

        return category.label;
    }

    private static String capitalizeWord(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase(Locale.getDefault()) + str.substring(1);
    }
}
