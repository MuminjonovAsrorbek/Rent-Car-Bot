package uz.dev.rentcarbot.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.dev.rentcarbot.enums.FuelTypeEnum;
import uz.dev.rentcarbot.enums.TransmissionEnum;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarDTO implements Serializable {

    private Long id;

    private String brand;

    private String model;

    private int year;

    private Long pricePerDay;

    private String imageUrl;

    private int seats;

    private FuelTypeEnum fuelType;

    private BigDecimal fuelConsumption;

    private TransmissionEnum transmission;
}