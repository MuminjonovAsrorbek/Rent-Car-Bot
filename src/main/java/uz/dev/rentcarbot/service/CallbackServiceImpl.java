package uz.dev.rentcarbot.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import uz.dev.rentcarbot.client.CarClient;
import uz.dev.rentcarbot.client.ReviewClient;
import uz.dev.rentcarbot.config.ChatContextHolder;
import uz.dev.rentcarbot.config.MyTelegramBot;
import uz.dev.rentcarbot.payload.CarDTO;
import uz.dev.rentcarbot.payload.PageableDTO;
import uz.dev.rentcarbot.payload.ReviewDTO;
import uz.dev.rentcarbot.repository.TelegramUserRepository;
import uz.dev.rentcarbot.service.template.CallbackService;
import uz.dev.rentcarbot.service.template.InlineButtonService;

import java.io.File;
import java.io.Serializable;
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

    private final ReviewClient reviewClient;

    public CallbackServiceImpl(CarClient carClient, InlineButtonService inlineButtonService, @Lazy MyTelegramBot telegramBot, TelegramUserRepository telegramUserRepository, ReviewClient reviewClient) {
        this.carClient = carClient;
        this.inlineButtonService = inlineButtonService;
        this.telegramBot = telegramBot;
        this.telegramUserRepository = telegramUserRepository;
        this.reviewClient = reviewClient;
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

            return getAvailableCars(pageableDTO, messageId, chatId);

        } else if (data.startsWith("car:")) {

            return getCarInfo(messageId, chatId, data);

        } else if (data.startsWith("car_page:")) {

            int page = Integer.parseInt(data.split(":")[1]);

            PageableDTO pageableDTO = carClient.getAvailableCars(page, 6);

            pageableDTO.setCurrentPage(page);

            return getAvailableCars(pageableDTO, messageId, chatId);

        } else if (data.equals("car_close")) {

            return DeleteMessage.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .build();

        } else if (data.startsWith("car-comment:")) {

            return getCarComments(data, chatId);

        } else if(data.equals("close")){

            return DeleteMessage.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .build();

        }

        return SendMessage.builder()
                .chatId(chatId)
                .text("""
                        ❌ Noto‘g‘ri buyruq!
                        Iltimos, mavjud komandalarni ishlating.
                        """)
                .build();

    }

    private SendMessage getCarComments(String data, Long chatId) {

        long carId = Long.parseLong(data.split(":")[1]);

        PageableDTO<ReviewDTO> reviews = reviewClient.getReviewsByCarId(carId);

        reviews.setCurrentPage(0);

        InlineKeyboardMarkup inlineKeyboardMarkup = inlineButtonService.buildPages(reviews);

        StringBuilder message = new StringBuilder();

        List<ReviewDTO> reviewDTOList = reviews.getObjects();

        for (int i = 0; i < reviewDTOList.size(); i++) {

            message.append(i + 1).append(" . ").append("<b>\uD83D\uDCAC Izoh ID: </b>").append(reviewDTOList.get(i).getId()).append("\n");
            message.append("<b>\uD83D\uDCC5 Yozilgan vaqti: </b>").append(reviewDTOList.get(i).getUpdatedAt()).append("\n");
            message.append("<b>⭐ Reyting: </b>").append(reviewDTOList.get(i).getRating()).append("\n");
            message.append("<b>✍ Izoh: </b>").append(reviewDTOList.get(i).getComment()).append("\n");
            message.append("<b>\uD83D\uDC64 Foydalanuvchi: </b>").append(reviewDTOList.get(i).getUserFullName()).append("\n\n");

        }

        return SendMessage.builder()
                .text(message.toString())
                .chatId(chatId)
                .replyMarkup(inlineKeyboardMarkup)
                .parseMode(ParseMode.HTML)
                .build();
    }

    private EditMessageText getAvailableCars(PageableDTO pageableDTO, Integer messageId, Long chatId) {
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
    }

    private BotApiMethod<? extends Serializable> getCarInfo(Integer messageId, Long chatId, String data) {

        DeleteMessage deleteMessage = DeleteMessage.builder()
                .messageId(messageId)
                .chatId(chatId)
                .build();

        telegramBot.deleteMessage(deleteMessage);

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
            editMessageCaption.setReplyMarkup(inlineButtonService.buildCarInfo(carID));

            return editMessageCaption;

        } else {

            return SendMessage.builder()
                    .chatId(chatId)
                    .text(message)
                    .parseMode(ParseMode.HTML)
                    .replyMarkup(inlineButtonService.buildCarInfo(carID))
                    .build();

        }
    }

}
