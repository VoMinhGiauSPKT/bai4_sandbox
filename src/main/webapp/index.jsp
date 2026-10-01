<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="murach.model.Cart"%>
<%
    Cart cart = (Cart) session.getAttribute("cart");
    int cartCount = (cart != null) ? cart.getCount() : 0;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Murach CD Store - Album Catalog</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>

<div class="container">
    <div class="header-bar">
        <h1 class="brand-title">🎵 Murach CD Store</h1>
        <a href="${pageContext.request.contextPath}/cart.jsp" class="cart-badge-btn" id="view-cart-btn">
            <span>🛒 Shopping Cart</span>
            <span class="badge-count"><%= cartCount %></span>
        </a>
    </div>

    <h1>Available CD Albums</h1>
    <p class="subtitle">Browse our collection of genuine music albums. Click "Add To Cart" to start your order.</p>

    <div class="table-responsive">
        <table>
            <thead>
                <tr>
                    <th style="width: 55%;">Album Description</th>
                    <th style="width: 20%;">Price</th>
                    <th style="width: 25%; text-align: center;">Action</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td><strong>86 (the band)</strong> - True Life Songs and Pictures</td>
                    <td>$14.95</td>
                    <td style="text-align: center;">
                        <form action="${pageContext.request.contextPath}/cart" method="post" style="margin: 0;">
                            <input type="hidden" name="productCode" value="cd01">
                            <input type="hidden" name="description" value="86 (the band) - True Life Songs and Pictures">
                            <input type="hidden" name="price" value="14.95">
                            <button type="submit" class="btn btn-primary btn-sm">Add To Cart</button>
                        </form>
                    </td>
                </tr>

                <tr>
                    <td><strong>Paddlefoot</strong> - The first CD</td>
                    <td>$12.95</td>
                    <td style="text-align: center;">
                        <form action="${pageContext.request.contextPath}/cart" method="post" style="margin: 0;">
                            <input type="hidden" name="productCode" value="cd02">
                            <input type="hidden" name="description" value="Paddlefoot - The first CD">
                            <input type="hidden" name="price" value="12.95">
                            <button type="submit" class="btn btn-primary btn-sm">Add To Cart</button>
                        </form>
                    </td>
                </tr>

                <tr>
                    <td><strong>Paddlefoot</strong> - The second CD</td>
                    <td>$14.95</td>
                    <td style="text-align: center;">
                        <form action="${pageContext.request.contextPath}/cart" method="post" style="margin: 0;">
                            <input type="hidden" name="productCode" value="cd03">
                            <input type="hidden" name="description" value="Paddlefoot - The second CD">
                            <input type="hidden" name="price" value="14.95">
                            <button type="submit" class="btn btn-primary btn-sm">Add To Cart</button>
                        </form>
                    </td>
                </tr>

                <tr>
                    <td><strong>Joe Rut</strong> - Genuine Wood Grained Finish CD</td>
                    <td>$14.95</td>
                    <td style="text-align: center;">
                        <form action="${pageContext.request.contextPath}/cart" method="post" style="margin: 0;">
                            <input type="hidden" name="productCode" value="cd04">
                            <input type="hidden" name="description" value="Joe Rut - Genuine Wood Grained Finish CD">
                            <input type="hidden" name="price" value="14.95">
                            <button type="submit" class="btn btn-primary btn-sm">Add To Cart</button>
                        </form>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>

    <div style="margin-top: 20px; display: flex; justify-content: flex-end;">
        <a href="${pageContext.request.contextPath}/cart.jsp" class="btn btn-secondary">
            View Current Cart (<%= cartCount %> items)
        </a>
    </div>
</div>

</body>
</html>
