package br.com.foxconcursos.services;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import br.com.foxconcursos.domain.Pagamento;

@Service
public class MercadoPagoService {

    private final PagamentoService pagamentoService;
    
    @Value("${integracao.mercadopago.private-key}")
    private String mercadoPagoPrivateKey;

    @Value("${integracao.mercadopago.access-token}")
    private String accessToken;

    @Value("${integracao.mercadopago.url-consulta-pagamento}")
    private String urlConsultaPagamento;

    public MercadoPagoService(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    public void processarNotificacao(String xSignature, String xRequestId, String dataId) {

        if (!validarAutenticidade(xSignature, xRequestId, dataId)) {
            System.out.println("Requisição não validada.");
            return;
        }

        Map<String, Object> payment = this.findByPaymentId(dataId);

        Pagamento pagamento = new Pagamento();
        String externalId = (String) payment.get("external_reference");
        if (externalId == null)
            return;

        pagamento.setId(UUID.fromString(externalId));
        pagamento.setStatus((String)payment.get("status"));
        pagamento.setMpId(dataId);
        pagamento.setData(OffsetDateTime.parse("" + payment.get("date_last_updated")).toLocalDateTime());
        pagamento.setValor(new BigDecimal("" + payment.get("transaction_amount")));

        this.pagamentoService.update(pagamento);
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

        System.out.println(manifest);

        String computedHash = computeHmacSha256(manifest);

        if (computedHash.equals(hash)) {
            System.out.println("Requisicao validada.");
            return true;
        }

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

    public Map<String, Object> findByPaymentId(String paymentId) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            urlConsultaPagamento,
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<Map<String, Object>>() {},
            paymentId    
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null)
            return response.getBody();


        throw new RuntimeException("Erro ao consultar o pagamento: " + response.getStatusCode());
    }

}
