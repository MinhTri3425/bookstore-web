package com.example.book_webstore.service.payment.vnpay;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.TreeMap;

@Service
public class VnPayService {
    private static final DateTimeFormatter VNPAY_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final int ORDER_ID_WIDTH = 10;
    private static final Logger log = LoggerFactory.getLogger(VnPayService.class);

    private final VnPayProperties properties;

    public VnPayService(VnPayProperties properties) {
        this.properties = properties;
    }

    public String createPaymentUrl(Long orderId, BigDecimal amount, String clientIp, String fallbackReturnUrl) {
        validateConfig();
        if (orderId == null) {
            throw new IllegalArgumentException("Ma don hang khong hop le");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("So tien thanh toan khong hop le");
        }

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        ZonedDateTime expire = now.plusMinutes(15);

        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", properties.getVersion());
        params.put("vnp_Command", properties.getCommand());
        params.put("vnp_TmnCode", properties.getTmnCode());
        params.put("vnp_Amount", amount.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).toPlainString());
        params.put("vnp_CurrCode", properties.getCurrCode());
        String txnRef = toTxnRef(orderId);
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_OrderInfo", "Thanh toan don hang " + orderId);
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", properties.getLocale());
        params.put("vnp_ReturnUrl", resolveReturnUrl(fallbackReturnUrl));
        params.put("vnp_IpAddr", normalizeClientIp(clientIp));
        params.put("vnp_CreateDate", now.format(VNPAY_DATE_FORMAT));
        params.put("vnp_ExpireDate", expire.format(VNPAY_DATE_FORMAT));

        log.info("VNPAY config tmnCode={} secretLength={} returnUrl={}",
            properties.getTmnCode(),
            properties.getHashSecret() == null ? 0 : properties.getHashSecret().length(),
            params.get("vnp_ReturnUrl"));

        String hashData = buildHashData(params);
        String secureHash = hmacSha512(properties.getHashSecret(), hashData);

        String queryString = buildQueryData(params)
            + "&vnp_SecureHashType=HmacSHA512"
            + "&vnp_SecureHash=" + secureHash;
        log.info("VNPAY sign data orderId={} txnRef={} hashData={} secureHash={} query={}", orderId, txnRef, hashData, secureHash, queryString);
        return properties.getPayUrl() + "?" + queryString;
    }

    public Long extractOrderIdFromTxnRef(String txnRef) {
        if (!StringUtils.hasText(txnRef)) {
            throw new IllegalArgumentException("Khong doc duoc ma giao dich VNPAY");
        }

        String normalized = txnRef.trim();
        if (!normalized.matches("\\d+")) {
            throw new IllegalArgumentException("Ma giao dich VNPAY khong hop le");
        }

        if (normalized.length() < ORDER_ID_WIDTH) {
            return Long.parseLong(normalized);
        }

        String orderPart = normalized.substring(0, ORDER_ID_WIDTH);
        return Long.parseLong(orderPart);
    }

    public boolean validateReturnSignature(Map<String, String> queryParams) {
        validateConfig();
        if (queryParams == null || queryParams.isEmpty()) {
            return false;
        }

        String receivedHash = queryParams.get("vnp_SecureHash");
        if (!StringUtils.hasText(receivedHash)) {
            return false;
        }

        Map<String, String> filtered = new TreeMap<>();
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!StringUtils.hasText(key)
                    || !key.startsWith("vnp_")
                    || "vnp_SecureHash".equals(key)
                    || "vnp_SecureHashType".equals(key)
                    || value == null
                    || value.isBlank()) {
                continue;
            }
            filtered.put(key, value);
        }

        String hashData = buildHashData(filtered);
        String expectedHash = hmacSha512(properties.getHashSecret(), hashData);
        return expectedHash.equalsIgnoreCase(receivedHash);
    }

    private String resolveReturnUrl(String fallbackReturnUrl) {
        if (StringUtils.hasText(properties.getReturnUrl())) {
            return properties.getReturnUrl();
        }
        if (StringUtils.hasText(fallbackReturnUrl)) {
            return fallbackReturnUrl;
        }
        throw new IllegalStateException("Thieu payment.vnpay.return-url");
    }

    private void validateConfig() {
        if (!StringUtils.hasText(properties.getTmnCode())) {
            throw new IllegalStateException("Thieu payment.vnpay.tmn-code");
        }
        if (!StringUtils.hasText(properties.getHashSecret())) {
            throw new IllegalStateException("Thieu payment.vnpay.hash-secret");
        }
        if (!StringUtils.hasText(properties.getPayUrl())) {
            throw new IllegalStateException("Thieu payment.vnpay.pay-url");
        }
    }

    private String buildHashData(Map<String, String> params) {
        List<String> fieldNames = getNonBlankSortedFieldNames(params);
        StringBuilder hashData = new StringBuilder();
        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String value = params.get(fieldName);
            hashData.append(fieldName)
                    .append('=')
                    .append(vnPayEncode(value));
            if (i < fieldNames.size() - 1) {
                hashData.append('&');
            }
        }
        return hashData.toString();
    }

    private String buildQueryData(Map<String, String> params) {
        List<String> fieldNames = getNonBlankSortedFieldNames(params);
        StringBuilder query = new StringBuilder();
        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String value = params.get(fieldName);
                query.append(vnPayEncode(fieldName))
                    .append('=')
                    .append(vnPayEncode(value));
            if (i < fieldNames.size() - 1) {
                query.append('&');
            }
        }
        return query.toString();
    }

    private List<String> getNonBlankSortedFieldNames(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        return fieldNames.stream()
                .filter(name -> {
                    String value = params.get(name);
                    return value != null && !value.isBlank();
                })
                .toList();
    }

    private String hmacSha512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKeySpec);
            byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder hash = new StringBuilder(2 * bytes.length);
            for (byte b : bytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            return hash.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Khong the ky du lieu VNPAY", ex);
        }
    }

    private String vnPayEncode(String input) {
        if (input == null) {
            return "";
        }
        return URLEncoder.encode(input, StandardCharsets.US_ASCII);
    }

    private String toTxnRef(Long orderId) {
        long safe = orderId == null ? 0L : Math.abs(orderId);
        if (safe > 9_999_999_999L) {
            safe = safe % 10_000_000_000L;
        }
        int randomSuffix = ThreadLocalRandom.current().nextInt(1000, 10000);
        return String.format("%0" + ORDER_ID_WIDTH + "d%04d", safe, randomSuffix);
    }

    private String normalizeClientIp(String clientIp) {
        if (!StringUtils.hasText(clientIp)) {
            return "127.0.0.1";
        }

        String ip = clientIp.trim();
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            return "127.0.0.1";
        }
        return ip;
    }
}
