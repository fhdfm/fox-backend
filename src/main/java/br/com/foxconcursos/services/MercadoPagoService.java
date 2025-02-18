package br.com.foxconcursos.services;

import java.nio.charset.StandardCharsets;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MercadoPagoService {

    @Value("${integracao.mercadopago.private-key}")
    private String mercadoPagoPrivateKey;

    public void processarNotificacao(String xSignature, String xRequestId, String dataId) {
        System.out.println("xSignature: " + xSignature);
        System.out.println("xRequestId: " + xRequestId);
        System.out.println("dataId: " + dataId);

        System.out.println("Autenticidade? " + validarAutenticidade(xSignature, xRequestId, dataId));
    }
    
    private boolean validarAutenticidade(String xSignature, String xRequestId, String dataId) {
        
        String[] parts = xSignature.split(",");
        
        String ts = null;
        String hash = null;

        for (String part : parts) {
            String[] keyValue = part.split("=", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                if ("ts".equals(key)) {
                    ts = value;
                } else if ("v1".equals(key)) {
                    hash = value;
                }
            }
        }

        if (!StringUtils.hasText(ts) || !StringUtils.hasText(hash))
            throw new IllegalArgumentException("[x-signature] está incompleto ou incorreto");
        
        String manifest = String.format("id:%s;request-id:%s;ts:%s;", dataId, xRequestId, ts);

        String computedHash = computeHmacSha256(manifest);
        
        if (computedHash.equals(hash))
            return true;

        return false;
    }
        
    private String computeHmacSha256(String manifest) {

        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    mercadoPagoPrivateKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] digest = mac.doFinal(manifest.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao gerar HMAC-SHA256", e);
        }
    }
    
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
