package pl.skowrxn.springecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {

    private UUID id;

    private OrderDTO order;
    private String paymentMethod;
    private String thirdPartyPaymentId;
    private String thirdPartyPaymentStatus;
    private String thirdPartyPaymentUrl;
    private String thirdPartyPaymentResponse;

}
