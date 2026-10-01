package murach.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import murach.dao.UserDAO;
import murach.model.Cart;
import murach.model.CartItem;
import murach.model.User;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Dịch vụ xử lý hoàn tất đơn hàng (lưu CSDL khách hàng, gọi gửi email, quản lý giỏ hàng).
 */
public class OrderService {

    /**
     * Xử lý lưu trữ và gửi mail khi thanh toán VNPAY thành công.
     */
    public static void processSuccessfulPayment(HttpServletRequest request,
                                               String txnRef,
                                               String bankCode,
                                               String transactionNo) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("pendingUser");
        @SuppressWarnings("unchecked")
        List<CartItem> orderedItems = (List<CartItem>) session.getAttribute("pendingItems");
        Double pendingTotal = (Double) session.getAttribute("pendingTotal");
        Cart cart = (Cart) session.getAttribute("cart");

        if (user == null) {
            user = new User("VNPAY Customer", "", "customer@example.com");
        }
        if (orderedItems == null) {
            orderedItems = new ArrayList<>();
        }
        double orderTotal = (pendingTotal != null) ? pendingTotal : 0.0;

        // 1. Lưu hoặc cập nhật thông tin khách hàng vào CSDL
        saveCustomerToDatabase(user);

        // 2. Định dạng thông tin đơn hàng
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        String formattedTotal = currencyFormat.format(orderTotal);
        String orderDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        // 3. Gửi email xác nhận hóa đơn qua EmailService
        boolean emailSent = EmailService.sendOrderConfirmation(user, txnRef, orderDate, orderedItems, formattedTotal, transactionNo, bankCode);
        String emailMessage = emailSent
                ? "Payment successful via VNPAY Sandbox (" + bankCode + "). Receipt sent to " + user.getEmail()
                : "Payment successful via VNPAY Sandbox (" + bankCode + "), but confirmation email could not be delivered.";

        // 4. Dọn sạch giỏ hàng và dữ liệu phiên tạm
        if (cart != null) {
            cart.clear();
        }
        session.removeAttribute("pendingUser");
        session.removeAttribute("pendingItems");
        session.removeAttribute("pendingTotal");

        // 5. Chuẩn bị dữ liệu hiển thị cho thanks.jsp
        request.setAttribute("user", user);
        request.setAttribute("orderNumber", txnRef);
        request.setAttribute("orderDate", orderDate);
        request.setAttribute("orderedItems", orderedItems);
        request.setAttribute("orderTotal", formattedTotal);
        request.setAttribute("emailSent", emailSent);
        request.setAttribute("emailMessage", emailMessage);
        request.setAttribute("vnpaySuccess", true);
        request.setAttribute("vnpayTransNo", transactionNo);
        request.setAttribute("vnpayBank", bankCode);
    }

    private static void saveCustomerToDatabase(User user) {
        if (UserDAO.emailExists(user.getEmail())) {
            User existing = UserDAO.selectUser(user.getEmail());
            if (existing != null) {
                existing.setFirstName(user.getFirstName());
                existing.setLastName(user.getLastName());
                UserDAO.update(existing);
            }
        } else {
            UserDAO.insert(user);
        }
    }
}
