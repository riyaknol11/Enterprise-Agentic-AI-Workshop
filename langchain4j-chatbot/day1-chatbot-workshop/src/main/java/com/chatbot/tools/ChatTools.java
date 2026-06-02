package com.chatbot.tools;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom Tools — methods annotated with @Tool are automatically
 * discovered by LangChain4j and offered to the AI model as callable functions.
 *
 * The AI decides on its own WHEN to call these tools based on user intent.
 */
@Component
public class ChatTools {

    // ─── Mock product database ────────────────────────────────────────────────
    private static final Map<String, Map<String, String>> PRODUCT_DB = new HashMap<>();

    static {
        PRODUCT_DB.put("laptop", Map.of(
                "name", "ProBook Laptop X1",
                "price", "₹75,000",
                "stock", "In Stock",
                "rating", "4.5/5",
                "description", "15.6\" FHD display, Intel i5, 16GB RAM, 512GB SSD"
        ));
        PRODUCT_DB.put("phone", Map.of(
                "name", "SmartPhone Z20",
                "price", "₹25,000",
                "stock", "In Stock",
                "rating", "4.3/5",
                "description", "6.7\" AMOLED, 108MP camera, 5000mAh battery"
        ));
        PRODUCT_DB.put("headphones", Map.of(
                "name", "SoundMax Pro Headphones",
                "price", "₹8,500",
                "stock", "Low Stock",
                "rating", "4.7/5",
                "description", "Active Noise Cancellation, 30hr battery, wireless"
        ));
        PRODUCT_DB.put("tablet", Map.of(
                "name", "TabPro 10",
                "price", "₹35,000",
                "stock", "Out of Stock",
                "rating", "4.2/5",
                "description", "10.5\" display, Snapdragon 870, 8GB RAM, 256GB"
        ));
        PRODUCT_DB.put("keyboard", Map.of(
                "name", "MechType Keyboard",
                "price", "₹4,200",
                "stock", "In Stock",
                "rating", "4.6/5",
                "description", "Mechanical RGB keyboard, TKL layout, Blue switches"
        ));
    }

    // ─── Tool 1: Product Lookup ───────────────────────────────────────────────

    /**
     * Fetches product details from the mock product catalog.
     * The AI calls this automatically when user asks about a product.
     *
     * @param productName name of the product to look up
     * @return formatted product information string
     */
    @Tool("Fetch product details from the catalog. Use when the user asks about a product, its price, availability, or description.")
    public String getProductDetails(String productName) {
        System.out.println(">>> [TOOL CALLED] getProductDetails: " + productName);

        String key = productName.toLowerCase().trim();
        Map<String, String> product = PRODUCT_DB.get(key);

        if (product == null) {
            // Try partial match
            for (Map.Entry<String, Map<String, String>> entry : PRODUCT_DB.entrySet()) {
                if (key.contains(entry.getKey()) || entry.getKey().contains(key)) {
                    product = entry.getValue();
                    break;
                }
            }
        }

        if (product == null) {
            return "Product '" + productName + "' not found in catalog. " +
                   "Available products: laptop, phone, headphones, tablet, keyboard.";
        }

        return String.format(
                "Product: %s | Price: %s | Stock: %s | Rating: %s | Details: %s",
                product.get("name"),
                product.get("price"),
                product.get("stock"),
                product.get("rating"),
                product.get("description")
        );
    }

    // ─── Tool 2: Price Calculator ─────────────────────────────────────────────

    /**
     * Calculates final price after quantity and discount.
     * The AI calls this when user asks to calculate total cost.
     *
     * @param pricePerUnit  unit price in rupees
     * @param quantity      number of units
     * @param discountPercent discount percentage (0-100)
     * @return formatted price breakdown
     */
    @Tool("Calculate the total price given unit price, quantity, and discount percentage. Use when user asks to calculate cost or total price.")
    public String calculatePrice(double pricePerUnit, int quantity, double discountPercent) {
        System.out.println(">>> [TOOL CALLED] calculatePrice: price=" + pricePerUnit
                + ", qty=" + quantity + ", discount=" + discountPercent + "%");

        if (pricePerUnit < 0 || quantity <= 0 || discountPercent < 0 || discountPercent > 100) {
            return "Invalid inputs: price and quantity must be positive, discount must be 0-100.";
        }

        double subtotal        = pricePerUnit * quantity;
        double discountAmount  = subtotal * (discountPercent / 100.0);
        double finalPrice      = subtotal - discountAmount;

        return String.format(
                "Price Breakdown → Unit Price: ₹%.2f × %d = ₹%.2f | Discount (%.1f%%): -₹%.2f | Final Total: ₹%.2f",
                pricePerUnit, quantity, subtotal, discountPercent, discountAmount, finalPrice
        );
    }

    // ─── Tool 3: Current Date/Time ────────────────────────────────────────────

    /**
     * Returns the current date and time.
     * The AI calls this when user asks what time/date it is.
     */
    @Tool("Get the current date and time. Use when user asks what time or date it is.")
    public String getCurrentDateTime() {
        System.out.println(">>> [TOOL CALLED] getCurrentDateTime");
        return "Current date and time: " + java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy 'at' hh:mm a"));
    }
}
