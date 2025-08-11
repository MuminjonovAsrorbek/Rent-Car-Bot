package uz.dev.rentcarbot.service.template;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import uz.dev.rentcarbot.enums.PageEnum;
import uz.dev.rentcarbot.payload.PageableDTO;

/**
 * Created by: asrorbek
 * DateTime: 8/8/25 18:21
 **/

public interface InlineButtonService {
    InlineKeyboardMarkup buildAvailableCars();

    InlineKeyboardMarkup getAvailableCars(PageableDTO pageableDTO);

    InlineKeyboardMarkup buildCarInfo(String carId);

    InlineKeyboardMarkup buildPages(Long Id, PageableDTO pageableDTO, PageEnum pageEnum);

    InlineKeyboardMarkup buildIsForSelfOr();

    InlineKeyboardMarkup buildPaymentMethod();

    InlineKeyboardMarkup buildOffices(PageableDTO pageableDTO);
}
