<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="murach.model.Cart"%>
<%@page import="murach.model.CartItem"%>
<%@page import="murach.util.VNPayConfig"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%
    Cart cart = (Cart) session.getAttribute("cart");
    if (cart == null || cart.isEmpty()) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
    String errorMessage = (String) request.getAttribute("errorMessage");
    long totalVND = Math.round(cart.getTotal() * VNPayConfig.USD_TO_VND_RATE);
    NumberFormat vndFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Checkout - Murach CD Store</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>

<div class="container">
    <div class="header-bar">
        <h1 class="brand-title">💳 Checkout & Payment</h1>
        <a href="${pageContext.request.contextPath}/cart.jsp" class="btn btn-secondary btn-sm">
            ← Back to Cart
        </a>
    </div>

    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
        <div class="alert alert-danger">
            <%= errorMessage %>
        </div>
    <% } %>

    <h2>Order Summary</h2>
    <div class="table-responsive">
        <table>
            <thead>
                <tr>
                    <th>Album Description</th>
                    <th style="text-align: center;">Qty</th>
                    <th style="text-align: right;">Unit Price</th>
                    <th style="text-align: right;">Subtotal</th>
                </tr>
            </thead>
            <tbody>
                <% for (CartItem item : cart.getItems()) { %>
                <tr>
                    <td><%= item.getProduct().getDescription() %></td>
                    <td style="text-align: center;"><%= item.getQuantity() %></td>
                    <td style="text-align: right;"><%= item.getProduct().getPriceCurrencyFormat() %></td>
                    <td style="text-align: right; font-weight: 600;"><%= item.getTotalCurrencyFormat() %></td>
                </tr>
                <% } %>
            </tbody>
        </table>
    </div>

    <div class="summary-box">
        <div class="total-row">
            <span>Total in USD:</span>
            <span><%= cart.getTotalCurrencyFormat() %></span>
        </div>
        <div class="total-row" style="margin-top: 8px; font-size: 16px; color: #005baa; border-top: none;">
            <span>Equivalent in VND (VNPAY):</span>
            <span><%= vndFormat.format(totalVND) %> VND <small style="font-weight: normal; color: #666;">(1 USD = 25,000 VND)</small></span>
        </div>
    </div>

    <h2>Customer Information</h2>
    <p class="subtitle">Please enter your billing details. Receipt will only be sent after payment is successfully completed.</p>

    <form action="${pageContext.request.contextPath}/vnpay_pay" method="post" id="checkout-form">
        <div class="form-group">
            <label for="firstName">First Name *</label>
            <input type="text" id="firstName" name="firstName" class="form-control" placeholder="e.g. John" required>
        </div>

        <div class="form-group">
            <label for="lastName">Last Name *</label>
            <input type="text" id="lastName" name="lastName" class="form-control" placeholder="e.g. Doe" required>
        </div>

        <div class="form-group">
            <label for="email">Email Address (Nhận hóa đơn sau khi thanh toán thành công) *</label>
            <input type="email" id="email" name="email" class="form-control" placeholder="e.g. john.doe@example.com" required>
        </div>

        <!-- Khối thông tin Thẻ Test VNPAY Sandbox -->
        <div class="vnpay-guide-box">
            <div class="vnpay-guide-title">
                <span>🏦 Thông tin Thẻ Test VNPAY Sandbox (Dùng trên cổng thanh toán VNPAY):</span>
            </div>
            <table class="vnpay-card-table">
                <tr>
                    <td>Ngân hàng:</td>
                    <td>NCB</td>
                </tr>
                <tr>
                    <td>Số thẻ:</td>
                    <td>9704198526191432198</td>
                </tr>
                <tr>
                    <td>Tên chủ thẻ:</td>
                    <td>NGUYEN VAN A</td>
                </tr>
                <tr>
                    <td>Ngày phát hành:</td>
                    <td>07/15</td>
                </tr>
                <tr>
                    <td>Mật khẩu OTP:</td>
                    <td>123456</td>
                </tr>
            </table>
        </div>

        <div class="btn-row" style="margin-top: 30px;">
            <a href="${pageContext.request.contextPath}/cart.jsp" class="btn btn-secondary">
                ← Return to Cart
            </a>

            <div>
                <button type="submit" class="btn btn-vnpay" id="vnpay-btn" style="font-size: 15px; padding: 10px 24px;">
                    Tiến hành thanh toán qua VNPAY Sandbox 💳
                </button>
            </div>
        </div>

        <p class="subtitle" style="margin-top: 12px; font-size: 13px; color: #475569; text-align: right;">
            🔒 <em>Hệ thống chỉ gửi email hóa đơn sau khi giao dịch được VNPAY xác nhận thành công (Mã 00). Nếu hủy thanh toán, sẽ không gửi mail.</em>
        </p>
    </form>
</div>

</body>
</html>
