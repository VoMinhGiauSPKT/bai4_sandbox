package murach.service;

import jakarta.mail.MessagingException;
import murach.model.CartItem;
import murach.model.User;
import murach.util.MailUtilGmail;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Dịch vụ chuyên trách xây dựng mẫu email hóa đơn và gửi email cho khách hàng.
 */
public class EmailService {

    private static final String STORE_EMAIL = "minhgiauad2024@gmail.com";

    /**
     * Gửi email xác nhận đơn hàng kèm thông tin thanh toán VNPAY.
     */
    public static boolean sendOrderConfirmation(User user, String orderNumber, String orderDate,
                                                List<CartItem> items, String formattedTotal,
                                                String vnpayTransNo, String bankCode) {
        String subject = "Order Confirmation #" + orderNumber + " [Paid via VNPAY]";
        String htmlBody = buildVNPayEmailHtml(user, orderNumber, orderDate, items, formattedTotal, vnpayTransNo, bankCode);

        try {
            MailUtilGmail.sendMail(user.getEmail(), STORE_EMAIL, subject, htmlBody, true);
            return true;
        } catch (MessagingException e) {
            System.err.println("EmailService error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Soạn nội dung HTML hóa đơn bán đĩa CD chuyên nghiệp.
     */
    public static String buildVNPayEmailHtml(User user, String orderNumber, String orderDate,
                                            List<CartItem> items, String formattedTotal,
                                            String vnpayTransNo, String bankCode) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;'>");
        sb.append("<h2 style='color: #008080; border-bottom: 2px solid #008080; padding-bottom: 10px;'>Murach CD Store - Order Confirmation</h2>");
        sb.append("<p>Dear <strong>").append(escapeHtml(user.getFirstName())).append(" ").append(escapeHtml(user.getLastName())).append("</strong>,</p>");
        sb.append("<p>Thank you for your purchase! Your payment has been <strong>successfully processed via VNPAY Sandbox</strong>.</p>");

        sb.append("<div style='background-color: #f0fdf4; border: 1px solid #bbf7d0; padding: 12px; border-radius: 6px; margin: 15px 0;'>");
        sb.append("<p style='margin: 4px 0;'><strong>Order Number:</strong> ").append(orderNumber).append("</p>");
        sb.append("<p style='margin: 4px 0;'><strong>Payment Method:</strong> VNPAY Sandbox (Bank: ").append(bankCode).append(")</p>");
        sb.append("<p style='margin: 4px 0;'><strong>VNPAY Transaction No:</strong> ").append(vnpayTransNo).append("</p>");
        sb.append("<p style='margin: 4px 0;'><strong>Payment Date:</strong> ").append(orderDate).append("</p>");
        sb.append("<p style='margin: 4px 0;'><strong>Status:</strong> <span style='color: #16a34a; font-weight: bold;'>PAID</span></p>");
        sb.append("</div>");

        sb.append("<table style='width: 100%; border-collapse: collapse; margin: 20px 0;'>");
        sb.append("<thead><tr style='background-color: #008080; color: #ffffff;'>");
        sb.append("<th style='padding: 8px; text-align: left;'>Product Description</th>");
        sb.append("<th style='padding: 8px; text-align: center;'>Qty</th>");
        sb.append("<th style='padding: 8px; text-align: right;'>Price</th>");
        sb.append("<th style='padding: 8px; text-align: right;'>Amount</th>");
        sb.append("</tr></thead><tbody>");

        for (CartItem item : items) {
            sb.append("<tr style='border-bottom: 1px solid #dddddd;'>");
            sb.append("<td style='padding: 8px;'>").append(escapeHtml(item.getProduct().getDescription())).append("</td>");
            sb.append("<td style='padding: 8px; text-align: center;'>").append(item.getQuantity()).append("</td>");
            sb.append("<td style='padding: 8px; text-align: right;'>").append(currencyFormat.format(item.getProduct().getPrice())).append("</td>");
            sb.append("<td style='padding: 8px; text-align: right;'>").append(currencyFormat.format(item.getTotal())).append("</td>");
            sb.append("</tr>");
        }

        sb.append("</tbody><tfoot>");
        sb.append("<tr>");
        sb.append("<td colspan='3' style='padding: 12px 8px; font-weight: bold; text-align: right;'>Total Paid:</td>");
        sb.append("<td style='padding: 12px 8px; font-weight: bold; text-align: right; color: #008080; font-size: 16px;'>").append(formattedTotal).append("</td>");
        sb.append("</tr></tfoot></table>");

        sb.append("<p>We hope you enjoy your music! If you have any questions, feel free to reply to this email.</p>");
        sb.append("<p style='color: #666; font-size: 13px;'>Best regards,<br><strong>Murach CD Store Team</strong></p>");
        sb.append("</div>");

        return sb.toString();
    }

    private static String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
}
