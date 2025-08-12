package uz.dev.rentcarbot.service.template;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

/**
 * Created by: asrorbek
 * DateTime: 8/12/25 20:23
 **/

public interface UserCallbackService {
    BotApiMethod<?> process(CallbackQuery callbackQuery);
}
