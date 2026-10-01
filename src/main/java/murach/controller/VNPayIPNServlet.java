package murach.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import murach.service.VNPayService;
import murach.service.VNPayService.VNPayResult;

import java.io.IOException;

/**
 * Endpoint xử lý IPN (Server-to-Server) phản hồi trạng thái thanh toán cho VNPAY.
 */
@WebServlet(urlPatterns = {"/vnpay_ipn", "/VNPayIPNServlet"})
public class VNPayIPNServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        VNPayResult result = VNPayService.verifyCallback(request);
        response.setContentType("application/json;charset=UTF-8");

        if (!result.isSignatureValid()) {
            response.getWriter().write("{\"RspCode\":\"97\",\"Message\":\"Invalid Checksum\"}");
        } else if (result.isSuccess()) {
            response.getWriter().write("{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}");
        } else {
            response.getWriter().write("{\"RspCode\":\"01\",\"Message\":\"Order Not Successful\"}");
        }
    }
}
