package murach.model;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Cart implements Serializable {
    private List<CartItem> items;

    public Cart() {
        items = new ArrayList<>();
    }

    public Cart(List<CartItem> items) {
        this.items = items;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public int getCount() {
        int count = 0;
        for (CartItem item : items) {
            count += item.getQuantity();
        }
        return count;
    }

    public void addItem(Product newProduct) {
        for (CartItem item : items) {
            if (item.getProduct().getCode().equals(newProduct.getCode())) {
                item.addQuantity(1);
                return;
            }
        }
        items.add(new CartItem(newProduct, 1));
    }

    public void updateItem(String code, int quantity) {
        if (quantity <= 0) {
            removeItem(code);
            return;
        }
        for (CartItem item : items) {
            if (item.getProduct().getCode().equals(code)) {
                item.setQuantity(quantity);
                return;
            }
        }
    }

    public void removeItem(String code) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getProduct().getCode().equals(code)) {
                items.remove(i);
                return;
            }
        }
    }

    public double getTotal() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getTotal();
        }
        return total;
    }

    public String getTotalCurrencyFormat() {
        NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.US);
        return currency.format(getTotal());
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }
}
