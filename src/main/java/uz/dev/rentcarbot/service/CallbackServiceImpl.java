package uz.dev.rentcarbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import uz.dev.rentcarbot.client.CarClient;
import uz.dev.rentcarbot.config.MyTelegramBot;
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
public class CallbackServiceImpl implements CallbackService {

    private final CarClient carClient;

    private final InlineButtonService inlineButtonService;

    private final MyTelegramBot telegramBot;

    @Value("${services.rent-car-service.url}")
    private String rentCarServiceUrl;

    public CallbackServiceImpl(CarClient carClient, InlineButtonService inlineButtonService, @Lazy MyTelegramBot telegramBot) {
        this.carClient = carClient;
        this.inlineButtonService = inlineButtonService;
        this.telegramBot = telegramBot;
    }

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

            StringBuilder message = new StringBuilder();

            message.append("🚗 *").append(car.getBrand()).append(" ").append(car.getModel()).append("*\n");
            message.append("📅 Yili: ").append(car.getYear()).append("\n");
            message.append("💰 Narxi: ").append(car.getPricePerDay()).append(" so'm/kun\n");
            message.append("🪑 O‘rindiqlar: ").append(car.getSeats()).append("\n");
            message.append("⛽ Yonilg‘i turi: ").append(car.getFuelType()).append("\n");
            message.append("⚙️ Transmissiya: ").append(car.getTransmission()).append("\n");
            message.append("🛢️ Sarfi: ").append(car.getFuelConsumption()).append(" L/100km\n");

            SendPhoto sendPhoto = SendPhoto.builder()
                    .chatId(chatId)
                    .caption(message.toString())
                    .parseMode(ParseMode.HTML)
                    .photo(new InputFile(rentCarServiceUrl + car.getImageUrl()))
                    .parseMode(ParseMode.HTML)
                    .build();

            Message sendMessage = telegramBot.sendPhoto(sendPhoto);

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
