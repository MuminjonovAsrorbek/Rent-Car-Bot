package uz.dev.rentcarbot.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.dev.rentcarbot.enums.PaymentStatus;
import uz.dev.rentcarbot.enums.PaymetMethodEnum;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO implements Serializable {

    private Long id;

    private Long bookingId;

    private Long amount;

    private PaymetMethodEnum paymentMethod;

    private PaymentStatus status;
}