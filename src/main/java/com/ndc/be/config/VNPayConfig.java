package com.ndc.be.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Configuration
@Getter
@Setter
public class VNPayConfig {
    @Value("${vnpay.pay-url:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}")
    private String vnpPayUrl;

    @Value("${vnpay.return-url:http://localhost:8080/api/payment/vnpay-return}")
    private String vnpReturnUrl;

    @Value("${vnpay.tmn-code:YOURCODE}")
    private String vnpTmnCode;

    @Value("${vnpay.hash-secret:YOURHASHSECRET}")
    private String vnpHashSecret;

    @Value("${vnpay.api-url:https://sandbox.vnpayment.vn/merchant_webapi/api/transaction}")
    private String vnpApiUrl;

    @Value("${vnpay.version:2.1.0}")
    private String vnpVersion;

    @Value("${vnpay.command:pay}")
    private String vnpCommand;

    public String hmacSHA512(String key, String data) {
        try {
            Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA512");
            sha512_HMAC.init(secret_key);
            byte[] hash = sha512_HMAC.doFinal(data.getBytes());
            return bytesToHex(hash);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to calculate hmac-sha512", ex);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
    
    public String createPaymentUrl(Long orderId, long amount, String orderInfo) {
        String vnp_TxnRef = orderId.toString();
        String vnp_TmnCode = getVnpTmnCode();
        
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", getVnpVersion());
        vnp_Params.put("vnp_Command", getVnpCommand());
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100)); // Amount in VND, convert to smallest unit (100 đồng)
        vnp_Params.put("vnp_CurrCode", "VND");
        
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", "other"); // Default value: other
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", getVnpReturnUrl());
        vnp_Params.put("vnp_IpAddr", "127.0.0.1");
        
        // Add current time
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        
        // Add expiry date
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
        
        // Build query string
        StringBuilder query = new StringBuilder();
        StringBuilder hashData = new StringBuilder();
        
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                try {
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    // Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("Error encoding URL parameters", e);
                }
                
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        
        String queryUrl = query.toString();
        String vnp_SecureHash = hmacSHA512(getVnpHashSecret(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        
        return getVnpPayUrl() + "?" + queryUrl;
    }
    
    public boolean validateReturnData(Map<String, String> vnp_Params) {
        // Remove vnp_SecureHash
        String vnp_SecureHash = vnp_Params.get("vnp_SecureHash");
        vnp_Params.remove("vnp_SecureHash");
        
        // Remove vnp_SecureHashType
        if (vnp_Params.containsKey("vnp_SecureHashType")) {
            vnp_Params.remove("vnp_SecureHashType");
        }
        
        // Build check sum
        StringBuilder hashData = new StringBuilder();
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                try {
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("Error encoding URL parameters", e);
                }
                
                if (itr.hasNext()) {
                    hashData.append('&');
                }
            }
        }
        
        String checkSum = hmacSHA512(getVnpHashSecret(), hashData.toString());
        return checkSum.equals(vnp_SecureHash);
    }
} 