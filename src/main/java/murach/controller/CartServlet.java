package murach.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import murach.model.Cart;
import murach.model.Product;

import java.io.IOException;

@WebServlet(urlPatterns = {"/cart", "/CartServlet"})
public class CartServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if ("continueShopping".equals(action)) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }

        String productCode = request.getParameter("productCode");
        String description = request.getParameter("description");
        String priceStr = request.getParameter("price");

        if (productCode != null && priceStr != null) {
            try {
                double price = Double.parseDouble(priceStr);
                Product product = new Product(productCode, description, price);
                cart.addItem(product);
            } catch (NumberFormatException e) {
                // Ignore parse errors
            }
        }

        response.sendRedirect(request.getContextPath() + "/cart.jsp");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
