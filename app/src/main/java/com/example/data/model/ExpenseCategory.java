package com.example.data.model;

public enum ExpenseCategory {
    FOOD("Food", "🍔", 0xFFF97316, 0xFFFFEDD5),
    TRANSPORT("Transport", "🚌", 0xFF3B82F6, 0xFFDBEAFE),
    STUDY("Study", "📚", 0xFF8B5CF6, 0xFFEDE9FE),
    SHOPPING("Shopping", "🛍️", 0xFFEC4899, 0xFFFCE7F3),
    ENTERTAINMENT("Fun", "🎮", 0xFF10B981, 0xFFD1FAE5),
    BILLS("Bills", "⚡", 0xFFF59E0B, 0xFFFEF3C7),
    HEALTH("Health", "💊", 0xFFEF4444, 0xFFFEE2E2),
    OTHERS("Others", "📦", 0xFF64748B, 0xFFF1F5F9);

    public final String label;
    public final String emoji;
    public final int colorValue;
    public final int bgLightColor;

    ExpenseCategory(String label, String emoji, int colorValue, int bgLightColor) {
        this.label = label;
        this.emoji = emoji;
        this.colorValue = colorValue;
        this.bgLightColor = bgLightColor;
    }

    public CategoryItem toItem() {
        return new CategoryItem(name(), label, emoji, colorValue, bgLightColor);
    }

    public static ExpenseCategory fromName(String name) {
        if (name == null) return OTHERS;
        for (ExpenseCategory cat : values()) {
            if (cat.name().equalsIgnoreCase(name) || cat.label.equalsIgnoreCase(name)) {
                return cat;
            }
        }
        return OTHERS;
    }
}
