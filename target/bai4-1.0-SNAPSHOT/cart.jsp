<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="murach.model.Cart"%>
<%@page import="murach.model.CartItem"%>
<%
    Cart cart = (Cart) session.getAttribute("cart");
    if (cart == null) {
        cart = new Cart();
        session.setAttribute("cart", cart);
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Shopping Cart - Murach CD Store</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>

<div class="container">
    <div class="header-bar">
        <h1 class="brand-title">🛒 Your Shopping Cart</h1>
        <a href="${pageContext.request.contextPath}/index.jsp" class="btn btn-secondary btn-sm">
            ← Continue Shopping
        </a>
    </div>

    <% if (cart.isEmpty()) { %>
        <div class="empty-cart-view">
            <h2>Your cart is empty</h2>
            <p class="subtitle">Looks like you haven't added any CDs to your cart yet.</p>
            <a href="${pageContext.request.contextPath}/index.jsp" class="btn btn-primary" style="margin-top: 15px;">
                Browse CD Catalog
            </a>
        </div>
    <% } else { %>

        <div class="table-responsive">
            <table>
                <thead>
                    <tr>
                        <th style="width: 25%;">Quantity</th>
                        <th style="width: 40%;">Description</th>
                        <th style="width: 15%;">Price</th>
                        <th style="width: 15%;">Amount</th>
                        <th style="width: 5%;"></th>
                    </tr>
                </thead>
                <tbody>
                    <% for (CartItem item : cart.getItems()) { %>
                    <tr>
                        <td>
                            <form action="${pageContext.request.contextPath}/cartAction" method="post" class="form-inline-qty">
                                <input type="hidden" name="action" value="update">
                                <input type="hidden" name="productCode" value="<%= item.getProduct().getCode() %>">
                                <input type="number" name="quantity" value="<%= item.getQuantity() %>" min="1" max="99">
                                <button type="submit" class="btn btn-secondary btn-sm">Update</button>
                            </form>
                        </td>

                        <td>
                            <strong><%= item.getProduct().getDescription() %></strong>
                        </td>

                        <td>
                            <%= item.getProduct().getPriceCurrencyFormat() %>
                        </td>

                        <td style="font-weight: 600; color: var(--primary);">
                            <%= item.getTotalCurrencyFormat() %>
                        </td>

                        <td style="text-align: center;">
                            <form action="${pageContext.request.contextPath}/cartAction" method="post" style="margin: 0;">
                                <input type="hidden" name="action" value="remove">
                                <input type="hidden" name="productCode" value="<%= item.getProduct().getCode() %>">
                                <button type="submit" class="btn btn-danger" title="Remove item">✕</button>
                            </form>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>

        <p class="subtitle" style="font-size: 13px;">
            💡 <em>Tip: To update quantities, type the new number and click <strong>Update</strong>. Set quantity to 0 or click <strong>✕</strong> to remove.</em>
        </p>

        <div class="summary-box">
            <div class="total-row">
                <span>Order Total:</span>
                <span><%= cart.getTotalCurrencyFormat() %></span>
            </div>
        </div>

        <div class="btn-row">
            <form action="${pageContext.request.contextPath}/cartAction" method="post" style="margin: 0;">
                <input type="hidden" name="action" value="continueShopping">
                <button type="submit" class="btn btn-secondary">← Continue Shopping</button>
            </form>

            <a href="${pageContext.request.contextPath}/checkout" class="btn btn-primary" id="checkout-btn">
                Proceed to Checkout →
            </a>
        </div>

    <% } %>
</div>

</body>
</html>
