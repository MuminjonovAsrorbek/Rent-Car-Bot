package uz.dev.rentcarbot.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.dev.rentcarbot.enums.BookingStatusEnum;
import uz.dev.rentcarbot.enums.FuelTypeEnum;
import uz.dev.rentcarbot.enums.TransmissionEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO implements Serializable {
    private Long id;

    private Long userId;

    private String userFullName;

    private Long carId;

    private String carBrand;

    private String carModel;

    private int carSeats;

    private FuelTypeEnum carFuelType;

    private BigDecimal carFuelConsumption;

    private TransmissionEnum carTransmission;

    private LocalDateTime pickupDate;

    private LocalDateTime returnDate;

    private Boolean isForSelf;

    private String recipientFullName;

    private String recipientPhone;

    private Boolean hasPromoCode;

    private Long totalPrice;

    private BookingStatusEnum status;

    private PaymentDTO payment;

    private OfficeDTO pickupOffice;

    private OfficeDTO returnOffice;
}