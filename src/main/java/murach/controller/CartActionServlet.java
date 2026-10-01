package murach.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import murach.model.Cart;

import java.io.IOException;

@WebServlet(urlPatterns = {"/cartAction", "/CartActionServlet"})
public class CartActionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");

        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }

        if ("update".equals(action)) {
            String productCode = request.getParameter("productCode");
            String quantityStr = request.getParameter("quantity");
            if (productCode != null && quantityStr != null) {
                try {
                    int quantity = Integer.parseInt(quantityStr);
                    cart.updateItem(productCode, quantity);
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
            response.sendRedirect(request.getContextPath() + "/cart.jsp");
        } else if ("remove".equals(action)) {
            String productCode = request.getParameter("productCode");
            if (productCode != null) {
                cart.removeItem(productCode);
            }
            response.sendRedirect(request.getContextPath() + "/cart.jsp");
        } else if ("continueShopping".equals(action)) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        } else if ("checkout".equals(action)) {
            response.sendRedirect(request.getContextPath() + "/checkout");
        } else {
            response.sendRedirect(request.getContextPath() + "/cart.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
