package uz.dev.rentcarbot.service.template;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.dev.rentcarbot.enums.RoleEnum;

/**
 * Created by: asrorbek
 * DateTime: 8/5/25 16:49
 **/

public interface ReplyButtonService {
    ReplyKeyboardMarkup buildPhoneNumber();

    ReplyKeyboardMarkup buildMenuButtons(RoleEnum role);
}
