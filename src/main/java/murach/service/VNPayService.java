package murach.service;

import jakarta.servlet.http.HttpServletRequest;
import murach.util.VNPayConfig;
import murach.util.VNPaySecurityUtil;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Dịch vụ chuyên trách xử lý kết nối, tạo URL thanh toán và xác thực kết quả từ VNPAY.
 */
public class VNPayService {

    /**
     * Tạo đường dẫn URL chuyển tiếp sang cổng thanh toán VNPAY Sandbox.
     */
    public static String createPaymentUrl(HttpServletRequest request, double totalUSD, String txnRef) {
        long totalVND = Math.round(totalUSD * VNPayConfig.USD_TO_VND_RATE);
        long vnpAmount = totalVND * 100L; // VNPAY yêu cầu nhân 100

        String returnUrl = System.getenv("VNPAY_RETURN_URL");
        if (returnUrl == null || returnUrl.trim().isEmpty()) {
            String scheme = request.getHeader("X-Forwarded-Proto");
            if (scheme == null || scheme.trim().isEmpty()) {
                scheme = request.getScheme();
            }
            String host = request.getHeader("X-Forwarded-Host");
            if (host == null || host.trim().isEmpty()) {
                host = request.getServerName();
                int port = request.getServerPort();
                if ((scheme.equalsIgnoreCase("http") && port != 80) || (scheme.equalsIgnoreCase("https") && port != 443)) {
                    host += ":" + port;
                }
            }
            returnUrl = scheme + "://" + host + request.getContextPath() + "/vnpay_return";
        }

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", VNPayConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(vnpAmount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", txnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang dia CD " + txnRef);
        vnp_OrderType(vnp_Params);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", returnUrl);
        vnp_Params.put("vnp_IpAddr", VNPaySecurityUtil.getIpAddress(request));

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));

        cld.add(Calendar.MINUTE, 15);
        vnp_Params.put("vnp_ExpireDate", formatter.format(cld.getTime()));

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                try {
                    hashData.append(fieldName).append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()))
                            .append('&');

                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString())).append('=')
                         .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString())).append('&');
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (hashData.length() > 0) {
            hashData.setLength(hashData.length() - 1);
            query.setLength(query.length() - 1);
        }

        String secureHash = VNPaySecurityUtil.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
        return VNPayConfig.vnp_Url + "?" + query.toString() + "&vnp_SecureHash=" + secureHash;
    }

    private static void vnp_OrderType(Map<String, String> vnp_Params) {
        vnp_Params.put("vnp_OrderType", "other");
    }

    /**
     * Xác thực tính hợp lệ của chữ ký và trạng thái giao dịch từ VNPAY callback.
     */
    public static VNPayResult verifyCallback(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }

        String signValue = VNPaySecurityUtil.hashAllFields(fields, VNPayConfig.vnp_HashSecret);
        boolean isSignatureValid = signValue.equalsIgnoreCase(vnp_SecureHash);

        String responseCode = request.getParameter("vnp_ResponseCode");
        String txnRef = request.getParameter("vnp_TxnRef");
        String transactionNo = request.getParameter("vnp_TransactionNo");
        String bankCode = request.getParameter("vnp_BankCode");
        String payDate = request.getParameter("vnp_PayDate");

        boolean isSuccess = isSignatureValid && "00".equals(responseCode);

        String message;
        if (!isSignatureValid) {
            message = "Invalid Signature (Checksum mismatch).";
        } else if ("00".equals(responseCode)) {
            message = "Payment successful.";
        } else if ("24".equals(responseCode)) {
            message = "Customer canceled transaction.";
        } else {
            message = "Transaction failed (Code: " + responseCode + ").";
        }

        return new VNPayResult(isSuccess, isSignatureValid, responseCode, txnRef, transactionNo, bankCode, payDate, message);
    }

    /**
     * Đối tượng chứa dữ liệu kết quả kiểm tra giao dịch VNPAY.
     */
    public static class VNPayResult {
        private final boolean success;
        private final boolean signatureValid;
        private final String responseCode;
        private final String txnRef;
        private final String transactionNo;
        private final String bankCode;
        private final String payDate;
        private final String message;

        public VNPayResult(boolean success, boolean signatureValid, String responseCode, String txnRef,
                           String transactionNo, String bankCode, String payDate, String message) {
            this.success = success;
            this.signatureValid = signatureValid;
            this.responseCode = responseCode;
            this.txnRef = txnRef;
            this.transactionNo = transactionNo;
            this.bankCode = bankCode;
            this.payDate = payDate;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public boolean isSignatureValid() { return signatureValid; }
        public String getResponseCode() { return responseCode; }
        public String getTxnRef() { return txnRef; }
        public String getTransactionNo() { return transactionNo; }
        public String getBankCode() { return bankCode; }
        public String getPayDate() { return payDate; }
        public String getMessage() { return message; }
    }
}
