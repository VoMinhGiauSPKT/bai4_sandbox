package murach.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import murach.model.Cart;
import murach.model.CartItem;
import murach.model.User;
import murach.service.VNPayService;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Controller khởi tạo giao dịch thanh toán và chuyển hướng sang VNPAY Sandbox.
 */
@WebServlet(urlPatterns = {"/vnpay_pay", "/VNPayPaymentServlet"})
public class VNPayPaymentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");

        if (firstName == null || firstName.trim().isEmpty() ||
            lastName == null || lastName.trim().isEmpty() ||
            email == null || email.trim().isEmpty()) {

            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ họ tên và email trước khi thanh toán.");
            request.getRequestDispatcher("/checkout.jsp").forward(request, response);
            return;
        }

        // Lưu thông tin người mua và đơn hàng vào session để đối soát khi quay về
        User user = new User(firstName.trim(), lastName.trim(), email.trim());
        session.setAttribute("pendingUser", user);
        session.setAttribute("pendingItems", new ArrayList<CartItem>(cart.getItems()));
        session.setAttribute("pendingTotal", cart.getTotal());

        String txnRef = "CD" + System.currentTimeMillis();
        session.setAttribute("pendingTxnRef", txnRef);

        // Tạo đường dẫn thanh toán qua VNPayService và chuyển hướng
        String paymentUrl = VNPayService.createPaymentUrl(request, cart.getTotal(), txnRef);
        response.sendRedirect(paymentUrl);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/checkout.jsp");
    }
}
