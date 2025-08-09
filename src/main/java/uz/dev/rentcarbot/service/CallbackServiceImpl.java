package uz.dev.rentcarbot.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import uz.dev.rentcarbot.client.CarClient;
import uz.dev.rentcarbot.config.ChatContextHolder;
import uz.dev.rentcarbot.config.MyTelegramBot;
import uz.dev.rentcarbot.payload.CarDTO;
import uz.dev.rentcarbot.payload.PageableDTO;
import uz.dev.rentcarbot.repository.TelegramUserRepository;
import uz.dev.rentcarbot.service.template.CallbackService;
import uz.dev.rentcarbot.service.template.InlineButtonService;

import java.io.File;
import java.time.LocalDateTime;
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
    private final TelegramUserRepository telegramUserRepository;

    public CallbackServiceImpl(CarClient carClient, InlineButtonService inlineButtonService, @Lazy MyTelegramBot telegramBot, TelegramUserRepository telegramUserRepository) {
        this.carClient = carClient;
        this.inlineButtonService = inlineButtonService;
        this.telegramBot = telegramBot;
        this.telegramUserRepository = telegramUserRepository;
    }

    @Override
    public BotApiMethod<?> processCallbackQuery(CallbackQuery callbackQuery) {

        String data = callbackQuery.getData();

        Long chatId = callbackQuery.getFrom().getId();

        ChatContextHolder.setChatId(chatId);

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

            String message = "<b>🚗 " + car.getBrand() + " " + car.getModel() + "</b>\n" +
                    "📅 <b>Yili:</b> " + car.getYear() + "\n" +
                    "💰 <b>Narxi:</b> " + car.getPricePerDay() + " so'm/kun\n" +
                    "🪑 <b>O‘rindiqlar:</b> " + car.getSeats() + "\n" +
                    "⛽ <b>Yonilg‘i turi:</b> " + car.getFuelType() + "\n" +
                    "⚙️ <b>Transmissiya:</b> " + car.getTransmission() + "\n" +
                    "🛢️ <b>Sarfi:</b> " + car.getFuelConsumption() + " L/100km\n";

            if (car.getImageUrl() != null) {

                int lastSlashIndex = car.getImageUrl().lastIndexOf("/");

                String idStr = car.getImageUrl().substring(lastSlashIndex + 1);

                Long attachmentID = Long.parseLong(idStr);

                SendPhoto sendPhoto = SendPhoto.builder()
                        .chatId(chatId)
                        .caption(LocalDateTime.now().toString())
                        .photo(new InputFile(new File(telegramUserRepository.getPath(attachmentID))))
                        .parseMode(ParseMode.HTML)
                        .build();

                Message sendMessage = telegramBot.sendPhoto(sendPhoto);

                EditMessageCaption editMessageCaption = new EditMessageCaption();

                editMessageCaption.setCaption(message);
                editMessageCaption.setChatId(chatId);
                editMessageCaption.setMessageId(sendMessage.getMessageId());
                editMessageCaption.setParseMode(ParseMode.HTML);

                return editMessageCaption;
            } else {

                return SendMessage.builder()
                        .chatId(chatId)
                        .text(message)
                        .parseMode(ParseMode.HTML)
                        .build();

            }
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
