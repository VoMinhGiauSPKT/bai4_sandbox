package murach.util;

import jakarta.mail.MessagingException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Tiện ích gửi email qua Webhook Google Apps Script (cổng HTTPS 443).
 */
public class MailUtilGmail {

    private static final String DEFAULT_WEBHOOK_URL =
            "https://script.google.com/macros/s/AKfycbwBGh_6dRvBKn-v9nfcRQuhjJJv4DXFaIWOLSFrc3fpB-zk_3Ox0x_d3UuJqC7z_br17Q/exec";

    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    public static void sendMail(String to, String from,
                                String subject, String body, boolean bodyIsHTML)
            throws MessagingException {

        String webhookUrl = System.getenv("MAIL_WEBHOOK_URL") != null
                ? System.getenv("MAIL_WEBHOOK_URL")
                : DEFAULT_WEBHOOK_URL;

        try {
            String jsonPayload = "{"
                    + "\"to\":\"" + escapeJson(to) + "\","
                    + "\"subject\":\"" + escapeJson(subject) + "\","
                    + "\"body\":\"" + escapeJson(body) + "\","
                    + "\"isHTML\":" + bodyIsHTML
                    + "}";

            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .connectTimeout(Duration.ofSeconds(15))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webhookUrl))
                    .timeout(Duration.ofSeconds(20))
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .header("User-Agent", USER_AGENT)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Xử lý thủ công nếu HttpClient không tự chuyển hướng trên một số runtime
            if (response.statusCode() == 302 || response.statusCode() == 307) {
                String location = response.headers().firstValue("location").orElse(null);
                if (location != null) {
                    HttpRequest getReq = HttpRequest.newBuilder()
                            .uri(URI.create(location))
                            .timeout(Duration.ofSeconds(20))
                            .header("User-Agent", USER_AGENT)
                            .GET()
                            .build();
                    response = client.send(getReq, HttpResponse.BodyHandlers.ofString());
                }
            }

            if (response.statusCode() != 200 || !response.body().contains("\"status\":\"success\"")) {
                throw new MessagingException("Google Mail Service responded (Status " + response.statusCode() + "): " + response.body());
            }

        } catch (MessagingException me) {
            throw me;
        } catch (Exception e) {
            throw new MessagingException("Mail transmission failed: " + e.getMessage(), e);
        }
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
