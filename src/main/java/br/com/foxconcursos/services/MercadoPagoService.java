package br.com.foxconcursos.services;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.mercadopago.resources.payment.Payment;

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
        System.out.println("xSignature: " + xSignature);
        System.out.println("xRequestId: " + xRequestId);
        System.out.println("dataId: " + dataId);

        Payment payment = this.findByPaymentId(dataId);

        System.out.println(payment.getExternalReference());

        Pagamento pagamento = new Pagamento();
        pagamento.setId(UUID.fromString(payment.getExternalReference()));
        pagamento.setStatus(payment.getStatus());
        pagamento.setMpId(dataId);
        pagamento.setData(payment.getDateLastUpdated().toLocalDateTime());
        pagamento.setValor(payment.getTransactionAmount());

        if (!validarAutenticidade(xSignature, xRequestId, dataId)) {
            return;
        }

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

        String computedHash = computeHmacSha256(manifest);
        
        System.out.println(hash);
        System.out.println(computedHash);

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

    public Payment findByPaymentId(String paymentId) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Payment> response = restTemplate.exchange(
            urlConsultaPagamento,
            HttpMethod.GET,
            entity,
            Payment.class,
            paymentId    
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null)
            return response.getBody();


        throw new RuntimeException("Erro ao consultar o pagamento: " + response.getStatusCode());
    }

}
