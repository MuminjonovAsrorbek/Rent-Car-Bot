package uz.dev.rentcarbot.service.template;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

/**
 * Created by: asrorbek
 * DateTime: 8/13/25 21:40
 **/

public interface AdminCallbackService {
    BotApiMethod<?> process(CallbackQuery callbackQuery);
}
