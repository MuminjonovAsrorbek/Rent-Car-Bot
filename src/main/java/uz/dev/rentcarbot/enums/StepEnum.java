package uz.dev.rentcarbot.enums;

/**
 * Created by: asrorbek
 * DateTime: 8/5/25 16:40
 **/

public enum StepEnum {

    //only for users
    START,
    SEND_PHONE_NUMBER,
    SELECT_MENU,
    PICKUP_DATE,
    RETURN_DATE,
    PAYMENT_METHOD,
    PICKUP_OFFICE,
    RETURN_OFFICE,
    RECIPIENT_FULL_NAME,
    RECIPIENT_PHONE,
    PROMO_CODE,
    SEND_PROMO_CODE,
    CHECKED_BOOKING,

    // only for admins
    SELECT_MENU_ADMIN,
    BOOKING_CONFIRM,
    BOOKING_COMPLETE,
    BOOKING_CANCEL,
    PAYMENT_CONFIRM,
    PAYMENT_CANCEL

}
