package uz.dev.rentcarbot.service.template;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

/**
 * Created by: asrorbek
 * DateTime: 8/8/25 18:26
 **/

public interface CallbackService {

    BotApiMethod<?> processCallbackQuery(CallbackQuery callbackQuery);

}
