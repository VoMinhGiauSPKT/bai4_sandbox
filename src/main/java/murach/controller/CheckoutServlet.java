package murach.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import murach.model.Cart;

import java.io.IOException;

@WebServlet(urlPatterns = {"/checkout", "/CheckoutServlet"})
public class CheckoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        request.getRequestDispatcher("/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Mọi yêu cầu thanh toán đều được chuyển tiếp đến VNPAY Payment Servlet (/vnpay_pay).
        // Email hóa đơn CHỈ được gửi sau khi VNPAY xác nhận giao dịch thành công (Mã 00).
        request.getRequestDispatcher("/vnpay_pay").forward(request, response);
    }
}
