package murach.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import murach.service.OrderService;
import murach.service.VNPayService;
import murach.service.VNPayService.VNPayResult;

import java.io.IOException;

/**
 * Controller tiếp nhận kết quả phản hồi từ Cổng VNPAY.
 * Chỉ thực hiện điều hướng luồng giao diện dựa trên kết quả từ VNPayService.
 */
@WebServlet(urlPatterns = {"/vnpay_return", "/VNPayReturnServlet"})
public class VNPayReturnServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Xác thực giao dịch thông qua VNPayService
        VNPayResult result = VNPayService.verifyCallback(request);

        if (result.isSuccess()) {
            // 2. Thanh toán thành công -> Gọi OrderService hoàn tất đơn hàng và gửi email
            OrderService.processSuccessfulPayment(
                    request,
                    result.getTxnRef(),
                    result.getBankCode(),
                    result.getTransactionNo()
            );
            request.getRequestDispatcher("/thanks.jsp").forward(request, response);

        } else {
            // 3. Thanh toán thất bại hoặc hủy -> Hiển thị trang thông báo lỗi
            request.setAttribute("isSuccess", false);
            request.setAttribute("errorMessage", result.getMessage());
            request.setAttribute("vnp_TxnRef", result.getTxnRef());
            request.setAttribute("vnp_ResponseCode", result.getResponseCode());
            request.getRequestDispatcher("/vnpay_return.jsp").forward(request, response);
        }
    }
}
