package com.example.data.model;

public enum IncomeCategory {
    ALLOWANCE("Pkt Money", "💵", 0xFF10B981, 0xFFD1FAE5),
    PART_TIME("Part-Time", "💼", 0xFF0EA5E9, 0xFFE0F2FE),
    FREELANCE("Freelance", "💻", 0xFF8B5CF6, 0xFFEDE9FE),
    SCHOLARSHIP("Scholarship", "🎓", 0xFFF59E0B, 0xFFFEF3C7),
    GIFTS("Gift", "🎁", 0xFFEC4899, 0xFFFCE7F3),
    CASHBACK("Cashback", "🪙", 0xFF14B8A6, 0xFFCCFBF1),
    SELLING("Sold Items", "🏷️", 0xFFF97316, 0xFFFFEDD5),
    OTHER_INCOME("Others", "✨", 0xFF6366F1, 0xFFE0E7FF);

    public final String label;
    public final String emoji;
    public final int colorValue;
    public final int bgLightColor;

    IncomeCategory(String label, String emoji, int colorValue, int bgLightColor) {
        this.label = label;
        this.emoji = emoji;
        this.colorValue = colorValue;
        this.bgLightColor = bgLightColor;
    }

    public CategoryItem toItem() {
        return new CategoryItem(name(), label, emoji, colorValue, bgLightColor);
    }

    public static IncomeCategory fromName(String name) {
        if (name == null) return OTHER_INCOME;
        for (IncomeCategory cat : values()) {
            if (cat.name().equalsIgnoreCase(name) || cat.label.equalsIgnoreCase(name)) {
                return cat;
            }
        }
        return OTHER_INCOME;
    }
}
