package cinema.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.http.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MomoService {

    @Value("${momo.partner-code}") private String partnerCode;
    @Value("${momo.access-key}")   private String accessKey;
    @Value("${momo.secret-key}")   private String secretKey;
    @Value("${momo.endpoint}")     private String endpoint;
    @Value("${momo.return-url}")   private String returnUrl;
    @Value("${momo.ipn-url}")      private String ipnUrl;

    // ── Tạo link thanh toán MoMo ─────────────────────────────
    public String createPaymentUrl(String bookingCode, long amount) throws Exception {

        String orderId    = bookingCode + "_" + System.currentTimeMillis();
        String requestId  = UUID.randomUUID().toString();
        String orderInfo  = "Dat ve xem phim - " + bookingCode;
        String requestType = "payWithMethod";
        String extraData  = "";
        String lang       = "vi";

        // 1. Tạo chuỗi ký theo đúng format MoMo yêu cầu
        String rawSignature = "accessKey="    + accessKey
                + "&amount="      + amount
                + "&extraData="   + extraData
                + "&ipnUrl="      + ipnUrl
                + "&orderId="     + orderId
                + "&orderInfo="   + orderInfo
                + "&partnerCode=" + partnerCode
                + "&redirectUrl=" + returnUrl
                + "&requestId="   + requestId
                + "&requestType=" + requestType;

        String signature = hmacSHA256(rawSignature, secretKey);

        // 2. Tạo body JSON gửi lên MoMo
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("partnerCode", partnerCode);
        body.put("accessKey",   accessKey);
        body.put("requestId",   requestId);
        body.put("amount",      String.valueOf(amount));
        body.put("orderId",     orderId);
        body.put("orderInfo",   orderInfo);
        body.put("redirectUrl", returnUrl);
        body.put("ipnUrl",      ipnUrl);
        body.put("extraData",   extraData);
        body.put("requestType", requestType);
        body.put("signature",   signature);
        body.put("lang",        lang);

        // 3. Gửi HTTP request lên MoMo
        String jsonBody = new ObjectMapper().writeValueAsString(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        // 4. Lấy payUrl từ response
        Map<?, ?> result = new ObjectMapper().readValue(response.body(), Map.class);
        int resultCode = (int) result.get("resultCode");

        if (resultCode != 0) {
            throw new RuntimeException("MoMo lỗi: " + result.get("message"));
        }

        return (String) result.get("payUrl"); // Trả về link redirect sang MoMo
    }

    // ── Xác minh chữ ký từ IPN MoMo gửi về ──────────────────
    public boolean verifyIpnSignature(Map<String, String> params) {
        try {
            String rawSignature = "accessKey="    + params.get("accessKey")
                    + "&amount="      + params.get("amount")
                    + "&extraData="   + params.get("extraData")
                    + "&message="     + params.get("message")
                    + "&orderId="     + params.get("orderId")
                    + "&orderInfo="   + params.get("orderInfo")
                    + "&orderType="   + params.get("orderType")
                    + "&partnerCode=" + params.get("partnerCode")
                    + "&payType="     + params.get("payType")
                    + "&requestId="   + params.get("requestId")
                    + "&responseTime="+ params.get("responseTime")
                    + "&resultCode="  + params.get("resultCode")
                    + "&transId="     + params.get("transId");

            String expected = hmacSHA256(rawSignature, secretKey);
            return expected.equals(params.get("signature"));
        } catch (Exception e) {
            return false;
        }
    }

    // ── Hàm tạo chữ ký HMAC-SHA256 ───────────────────────────
    private String hmacSHA256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256"));
        byte[] hash = mac.doFinal(data.getBytes("UTF-8"));
        return Hex.encodeHexString(hash);
    }
}