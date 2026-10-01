<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String errorMessage = (String) request.getAttribute("errorMessage");
    String vnp_TxnRef = (String) request.getAttribute("vnp_TxnRef");
    String vnp_ResponseCode = (String) request.getAttribute("vnp_ResponseCode");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>VNPAY Payment Status - Murach CD Store</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>

<div class="container">
    <div class="header-bar">
        <h1 class="brand-title">⚠️ Payment Notification</h1>
        <a href="${pageContext.request.contextPath}/index.jsp" class="btn btn-secondary btn-sm">
            ← Return to Store
        </a>
    </div>

    <div class="alert alert-danger">
        <h3>Transaction Incomplete / Canceled</h3>
        <p><%= (errorMessage != null) ? errorMessage : "Payment could not be completed." %></p>
    </div>

    <div class="receipt-card">
        <h2>Transaction Reference Details</h2>
        <div class="receipt-info-grid">
            <div>
                <strong>Order Reference:</strong> <%= (vnp_TxnRef != null) ? vnp_TxnRef : "N/A" %>
            </div>
            <div>
                <strong>VNPAY Status Code:</strong> <%= (vnp_ResponseCode != null) ? vnp_ResponseCode : "N/A" %>
            </div>
            <div>
                <strong>Status:</strong> <span style="color: var(--danger); font-weight: bold;">Unpaid / Canceled</span>
            </div>
        </div>
        <p class="subtitle" style="margin-top: 15px;">
            Your cart items have been preserved. You can return to your cart and try again.
        </p>
    </div>

    <div class="btn-row" style="margin-top: 25px;">
        <a href="${pageContext.request.contextPath}/cart.jsp" class="btn btn-secondary">
            ← Back to Cart
        </a>
        <a href="${pageContext.request.contextPath}/checkout.jsp" class="btn btn-primary">
            Try Checkout Again →
        </a>
    </div>
</div>

</body>
</html>
