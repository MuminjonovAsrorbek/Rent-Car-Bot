package uz.dev.rentcarbot.payload;

import lombok.Data;
import uz.dev.rentcarbot.enums.PaymetMethodEnum;

import java.time.LocalDateTime;

@Data
public class BookingCreateDTO {

    private Long carId;

    private Long pickupOfficeId;

    private Long returnOfficeId;

    private LocalDateTime pickupDate;

    private LocalDateTime returnDate;

    private PaymetMethodEnum paymentMethod;

    private boolean isForSelf = true;

    private String recipientFullName;

    private String recipientPhone;

    private String promoCode;
}
