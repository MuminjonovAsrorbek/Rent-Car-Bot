package uz.dev.rentcarbot.service.template;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import uz.dev.rentcarbot.payload.BookingCreateDTO;

/**
 * Created by: asrorbek
 * DateTime: 8/5/25 16:34
 **/

public interface TextService {

    BotApiMethod<?> processText(Message message);

    SendMessage checkedBooking(BookingCreateDTO dto, Long chatId);
}
