
package br.com.foxconcursos.services;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.resources.payment.Payment;
import br.com.foxconcursos.dto.PagamentoRequest;
@Service
public class MPService {

    public Payment checkout (PagamentoRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        PaymentClient client = new PaymentClient();
        PaymentCreateRequest createRequest = PaymentCreateRequest.builder()
                .token(request.getToken())
                .transactionAmount(request.getTransactionAmount())
                .description(request.getDescription())
                .installments(request.getInstallments())
                .paymentMethodId(request.getPaymentMethodId())
                .issuerId(request.getIssuerId())
                .payer(PaymentPayerRequest.builder()
                        .email(login)
                        .build())
                .build();
        return null;
    }
}
