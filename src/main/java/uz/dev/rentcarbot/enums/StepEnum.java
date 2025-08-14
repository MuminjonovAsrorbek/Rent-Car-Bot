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


    // Category management steps
    CATEGORY_MENU,
    CATEGORY_ADD_NAME,
    CATEGORY_EDIT_SELECT,
    CATEGORY_EDIT_NAME,
    CATEGORY_DELETE_CONFIRM,

    // Office management steps
    OFFICE_MENU,
    OFFICE_ADD_NAME,
    OFFICE_ADD_ADDRESS,
    OFFICE_ADD_LATITUDE,
    OFFICE_ADD_LONGITUDE,
    OFFICE_EDIT_SELECT,
    OFFICE_EDIT_NAME,
    OFFICE_EDIT_ADDRESS,
    OFFICE_EDIT_LATITUDE,
    OFFICE_EDIT_LONGITUDE,
    OFFICE_DELETE_CONFIRM

}

