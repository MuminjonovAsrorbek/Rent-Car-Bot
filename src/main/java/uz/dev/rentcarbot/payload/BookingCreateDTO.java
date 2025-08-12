package uz.dev.rentcarbot.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import uz.dev.rentcarbot.enums.PaymetMethodEnum;

import java.time.LocalDateTime;

@Data
public class BookingCreateDTO {

    private Long carId;

    private Long pickupOfficeId;

    private Long returnOfficeId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime pickupDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime returnDate;

    private PaymetMethodEnum paymentMethod;

    private boolean isForSelf = true;

    private String recipientFullName;

    private String recipientPhone;

    private String promoCode;
}
