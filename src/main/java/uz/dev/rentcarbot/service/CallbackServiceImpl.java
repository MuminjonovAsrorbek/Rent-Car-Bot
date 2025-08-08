package uz.dev.rentcarbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import uz.dev.rentcarbot.client.CarClient;
import uz.dev.rentcarbot.payload.CarDTO;
import uz.dev.rentcarbot.payload.PageableDTO;
import uz.dev.rentcarbot.service.template.CallbackService;
import uz.dev.rentcarbot.service.template.InlineButtonService;

import java.util.List;

/**
 * Created by: asrorbek
 * DateTime: 8/8/25 18:26
 **/

@Service
@RequiredArgsConstructor
public class CallbackServiceImpl implements CallbackService {

    private final CarClient carClient;
    private final InlineButtonService inlineButtonService;

    @Override
    public BotApiMethod<?> processCallbackQuery(CallbackQuery callbackQuery) {

        String data = callbackQuery.getData();

        Long chatId = callbackQuery.getFrom().getId();

        Integer messageId = callbackQuery.getMessage().getMessageId();

        if (data.equals("available-cars")) {

            PageableDTO pageableDTO = carClient.getAvailableCars(0, 6);

            pageableDTO.setCurrentPage(0);

            InlineKeyboardMarkup inlineKeyboardMarkup = inlineButtonService.getAvailableCars(pageableDTO);

            StringBuilder message = new StringBuilder();

            message.append("""
                    <b>\uD83D\uDE97 O'zingizga kerakli avtomobilni tanlang</b>
                    
                    <i>Quyida mavjud bo'lgan avtomobillarni ko'rishingiz mumkin. Har birini bosib, u haqida to'liqroq ma'lumot oling.</i>
                    """).append("\n\n");

            List<CarDTO> cars = (List<CarDTO>) pageableDTO.getObjects();

            for (int i = 0; i < cars.size(); i++) {

                message
                        .append(i + 1).append(" . ").append("\uD83D\uDE98 Brand : ").append(cars.get(i).getBrand()).append("\n")
                        .append("Ⓜ️ Model : ").append(cars.get(i).getModel()).append("\n")
                        .append("\uD83D\uDCB5 Kunlik Narxi : ").append(cars.get(i).getPricePerDay()).append(" so'm").append("\n\n");

            }

            return EditMessageText.builder()
                    .text(message.toString())
                    .messageId(messageId)
                    .chatId(chatId)
                    .replyMarkup(inlineKeyboardMarkup)
                    .parseMode(ParseMode.HTML)
                    .build();

        } else if (data.startsWith("car:")) {

            String carID = data.split(":")[1];

            CarDTO car = carClient.getCarById(Long.valueOf(carID));

            

        }

        return SendMessage.builder()
                .chatId(chatId)
                .text("""
                        ❌ Noto‘g‘ri buyruq!
                        Iltimos, mavjud komandalarni ishlating.
                        """)
                .build();

    }


}
