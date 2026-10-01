<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="murach.model.User"%>
<%@page import="murach.model.CartItem"%>
<%@page import="java.util.List"%>
<%
    User user = (User) request.getAttribute("user");
    String orderNumber = (String) request.getAttribute("orderNumber");
    String orderDate = (String) request.getAttribute("orderDate");
    String orderTotal = (String) request.getAttribute("orderTotal");
    Boolean emailSent = (Boolean) request.getAttribute("emailSent");
    String emailMessage = (String) request.getAttribute("emailMessage");
    List<CartItem> orderedItems = (List<CartItem>) request.getAttribute("orderedItems");

    Boolean vnpaySuccess = (Boolean) request.getAttribute("vnpaySuccess");
    String vnpayTransNo = (String) request.getAttribute("vnpayTransNo");
    String vnpayBank = (String) request.getAttribute("vnpayBank");

    if (user == null || orderedItems == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Order Confirmation - Murach CD Store</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>

<div class="container">
    <div class="header-bar">
        <h1 class="brand-title">🎉 Order Confirmed!</h1>
        <a href="${pageContext.request.contextPath}/index.jsp" class="btn btn-secondary btn-sm">
            ← Return to Store
        </a>
    </div>

    <% if (emailSent != null && emailSent) { %>
        <div class="alert alert-success">
            <strong>✓ Order Successful!</strong> <%= emailMessage %>
        </div>
    <% } else { %>
        <div class="alert alert-warning">
            <strong>Order Recorded!</strong> <%= emailMessage %>
        </div>
    <% } %>

    <div class="receipt-card">
        <h2>Order Receipt: <%= orderNumber %></h2>
        <div class="receipt-info-grid">
            <div>
                <strong>Order Date:</strong> <%= orderDate %>
            </div>
            <div>
                <strong>Payment Status:</strong> <span style="color: var(--success); font-weight: bold;">Completed</span>
            </div>
            <div>
                <strong>Customer:</strong> <%= user.getFirstName() %> <%= user.getLastName() %>
            </div>
            <div>
                <strong>Email:</strong> <%= user.getEmail() %>
            </div>
            <% if (vnpaySuccess != null && vnpaySuccess) { %>
            <div>
                <strong>Payment Method:</strong> <span style="color: #005baa; font-weight: bold;">VNPAY Sandbox (<%= vnpayBank %>)</span>
            </div>
            <div>
                <strong>VNPAY Trans No:</strong> <%= vnpayTransNo %>
            </div>
            <% } %>
        </div>

        <div class="table-responsive">
            <table>
                <thead>
                    <tr>
                        <th>Product Description</th>
                        <th style="text-align: center;">Qty</th>
                        <th style="text-align: right;">Price</th>
                        <th style="text-align: right;">Amount</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (CartItem item : orderedItems) { %>
                    <tr>
                        <td><%= item.getProduct().getDescription() %></td>
                        <td style="text-align: center;"><%= item.getQuantity() %></td>
                        <td style="text-align: right;"><%= item.getProduct().getPriceCurrencyFormat() %></td>
                        <td style="text-align: right; font-weight: 600;"><%= item.getTotalCurrencyFormat() %></td>
                    </tr>
                    <% } %>
                </tbody>
                <tfoot>
                    <tr>
                        <td colspan="3" style="text-align: right; font-weight: bold; padding: 14px;">Total Paid:</td>
                        <td style="text-align: right; font-weight: bold; color: var(--primary); font-size: 17px; padding: 14px;">
                            <%= orderTotal %>
                        </td>
                    </tr>
                </tfoot>
            </table>
        </div>
    </div>

    <div style="text-align: center; margin-top: 30px;">
        <p class="subtitle">A record of your customer profile has been saved. We hope to see you again soon!</p>
        <a href="${pageContext.request.contextPath}/index.jsp" class="btn btn-primary">
            Continue Shopping
        </a>
    </div>
</div>

</body>
</html>
