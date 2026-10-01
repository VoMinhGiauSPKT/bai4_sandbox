package murach.util;

/**
 * Lưu trữ toàn bộ thông tin cấu hình cổng thanh toán VNPAY Sandbox.
 */
public class VNPayConfig {

    // Mã Website / Terminal ID do VNPAY cấp
    public static final String vnp_TmnCode = "ZUOYP50Q";

    // Chuỗi bí mật tạo chữ ký checksum HMAC-SHA512
    public static final String vnp_HashSecret = "GLDOCASLZWURECJKZCQDFACGWNXRWJRI";

    // URL chuyển hướng sang cổng thanh toán môi trường Sandbox
    public static final String vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    // Phiên bản chuẩn VNPAY
    public static final String vnp_Version = "2.1.0";
    public static final String vnp_Command = "pay";

    // Tỷ giá quy đổi giả lập từ USD sang VND (1 USD = 25,000 VND)
    public static final long USD_TO_VND_RATE = 25000L;
}
