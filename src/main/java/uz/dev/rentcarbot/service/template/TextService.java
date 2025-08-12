package uz.dev.rentcarbot.service.template;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import uz.dev.rentcarbot.payload.BookingCreateDTO;
import uz.dev.rentcarbot.payload.BookingDTO;
import uz.dev.rentcarbot.payload.PageableDTO;

/**
 * Created by: asrorbek
 * DateTime: 8/5/25 16:34
 **/

public interface TextService {

    BotApiMethod<?> processText(Message message);

    StringBuilder getUserBookings(PageableDTO<BookingDTO> myBookings, Long chatId);

    SendMessage checkedBooking(BookingCreateDTO dto, Long chatId);
}
