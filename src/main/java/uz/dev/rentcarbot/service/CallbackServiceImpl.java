package uz.dev.rentcarbot.service;

import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import uz.dev.rentcarbot.client.*;
import uz.dev.rentcarbot.config.MyTelegramBot;
import uz.dev.rentcarbot.entity.TelegramUser;
import uz.dev.rentcarbot.enums.PageEnum;
import uz.dev.rentcarbot.enums.PaymetMethodEnum;
import uz.dev.rentcarbot.enums.StepEnum;
import uz.dev.rentcarbot.payload.*;
import uz.dev.rentcarbot.repository.TelegramUserRepository;
import uz.dev.rentcarbot.service.template.CallbackService;
import uz.dev.rentcarbot.service.template.InlineButtonService;
import uz.dev.rentcarbot.service.template.ReplyButtonService;
import uz.dev.rentcarbot.utils.ChatContextHolder;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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

    private final FavoriteClient favoriteClient;
    private final ReplyButtonService replyButtonService;
    private final OfficeClient officeClient;
    private final BookingClient bookingClient;

    public CallbackServiceImpl(CarClient carClient, InlineButtonService inlineButtonService, @Lazy MyTelegramBot telegramBot, TelegramUserRepository telegramUserRepository, ReviewClient reviewClient, FavoriteClient favoriteClient, ReplyButtonService replyButtonService, OfficeClient officeClient, BookingClient bookingClient) {
        this.carClient = carClient;
        this.inlineButtonService = inlineButtonService;
        this.telegramBot = telegramBot;
        this.telegramUserRepository = telegramUserRepository;
        this.reviewClient = reviewClient;
        this.favoriteClient = favoriteClient;
        this.replyButtonService = replyButtonService;
        this.officeClient = officeClient;
        this.bookingClient = bookingClient;
    }

    @Override
    @Transactional
    public BotApiMethod<?> processCallbackQuery(CallbackQuery callbackQuery) {

        String data = callbackQuery.getData();

        Long chatId = callbackQuery.getFrom().getId();

        ChatContextHolder.setChatId(chatId);

        Integer messageId = callbackQuery.getMessage().getMessageId();

        String callbackId = callbackQuery.getId();

        TelegramUser user = telegramUserRepository.findByChatIdOrThrowException(chatId);

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

            long carId = Long.parseLong(data.split(":")[1]);

            PageableDTO<ReviewDTO> reviews = reviewClient.getReviewsByCarId(carId, 0, 6);

            reviews.setCurrentPage(0);

            return getCarComments(carId, reviews, chatId);

        } else if (data.equals("close")) {

            user.setStep(StepEnum.SELECT_MENU);

            telegramUserRepository.save(user);

            DeleteMessage deleteMessage = DeleteMessage.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .build();

            telegramBot.deleteMessage(deleteMessage);

            return SendMessage.builder()
                    .chatId(chatId)
                    .text("MENU")
                    .replyMarkup(replyButtonService.buildMenuButtons(user.getRole()))
                    .build();

        } else if (data.startsWith("page:")) {

            String pageEnumElement = data.split(":")[1];

            long id = Long.parseLong(data.split(":")[2]);

            int page = Integer.parseInt(data.split(":")[3]);

            if (pageEnumElement.equals(PageEnum.CAR_COMMENT.toString())) {

                PageableDTO<ReviewDTO> reviews = reviewClient.getReviewsByCarId(id, page, 6);

                reviews.setCurrentPage(0);

                return getCarComments(id, reviews, chatId);

            } else if (pageEnumElement.equals(PageEnum.OFFICE.toString())) {

                PageableDTO<OfficeDTO> pageableDTO = officeClient.getAllOffices(page, 10);

            }
        } else if (data.startsWith("car-favorite")) {

            return addOrRemoveFavorites(data, chatId, callbackId);

        } else if (data.startsWith("car-booking:")) {

            return carBooking(chatId, messageId, data);

        } else if (data.startsWith("for-")) {

            EditMessageReplyMarkup replyMarkup = new EditMessageReplyMarkup();

            replyMarkup.setChatId(chatId);
            replyMarkup.setMessageId(messageId);
            replyMarkup.setReplyMarkup(null);

            telegramBot.editMessageReplyMarkup(replyMarkup);

            if (data.equals("for-me")) {

                telegramBot.getUserBookings().get(chatId).setForSelf(true);

                PageableDTO<OfficeDTO> dtos = officeClient.getAllOffices(0, 10);

                List<OfficeDTO> allOffices = dtos.getObjects();

                StringBuilder message = new StringBuilder();

                message.append("<b>\uD83D\uDCCD Qaysi ofisimizdan olib ketasiz?</b>").append("\n\n");

                for (int i = 0; i < allOffices.size(); i++) {

                    message.append(i + 1).append(" . ").append("<b>").append(allOffices.get(i).getName()).append("</b>").append("\n");
                    message.append("Manzil: ").append(allOffices.get(i).getAddress()).append("\n\n");

                }

                user.setStep(StepEnum.PICKUP_OFFICE);

                telegramUserRepository.save(user);

                return EditMessageText.builder()
                        .chatId(chatId)
                        .messageId(messageId)
                        .text(message.toString())
                        .parseMode(ParseMode.HTML)
                        .replyMarkup(inlineButtonService.buildOffices(dtos))
                        .build();


            } else {


            }

        } else if (user.getStep().equals(StepEnum.PAYMENT_METHOD)) {

            String paymentMethod = data.split(":")[1];

            telegramBot.getUserBookings().get(chatId).setPaymentMethod(PaymetMethodEnum.valueOf(paymentMethod));

            user.setStep(StepEnum.PROMO_CODE);

            BookingCreateDTO dto = telegramBot.getUserBookings().get(chatId);

            BookingDTO booking = bookingClient.createBooking(dto);

            DeleteMessage deleteMessage = DeleteMessage.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .build();

            telegramBot.deleteMessage(deleteMessage);

            if (Objects.nonNull(booking)) {

                return SendMessage.builder()
                        .chatId(chatId)
                        .text(booking.toString())
                        .replyMarkup(replyButtonService.buildMenuButtons(user.getRole()))
                        .build();

            }

        } else if (user.getStep().equals(StepEnum.PICKUP_OFFICE)) {

            DeleteMessage deleteMessage = DeleteMessage.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .build();

            telegramBot.deleteMessage(deleteMessage);

            long pickupOfficeId = Long.parseLong(data.split(":")[1]);

            telegramBot.getUserBookings().get(chatId).setPickupOfficeId(pickupOfficeId);

            user.setStep(StepEnum.PICKUP_DATE);

            return SendMessage.builder()
                    .chatId(chatId)
                    .text("""
                            <b>\uD83D\uDCC5 Qachon olib ketasiz?</b>
                            Iltimos, sanani quyidagi formatda kiriting:
                            <code>2025-08-01 10:00</code>
                            ⛔ Faqat shu formatdagi sana va vaqt qabul qilinadi.
                            ✅ Format: <b>YYYY-MM-DD HH:mm</b>
                            """)
                    .parseMode(ParseMode.HTML)
                    .replyMarkup(replyButtonService.buildCancelButton())
                    .build();

        } else if (user.getStep().equals(StepEnum.RETURN_OFFICE)) {

            DeleteMessage deleteMessage = DeleteMessage.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .build();

            telegramBot.deleteMessage(deleteMessage);

            long returnOfficeId = Long.parseLong(data.split(":")[1]);

            telegramBot.getUserBookings().get(chatId).setReturnOfficeId(returnOfficeId);

            user.setStep(StepEnum.RETURN_DATE);

            return SendMessage.builder()
                    .chatId(chatId)
                    .text("""
                            <b>\uD83D\uDCC5 Qachon qaytarasiz?</b>
                            Iltimos, sanani quyidagi formatda kiriting:
                            <code>2025-08-05 16:30</code>
                            ⛔ Faqat shu formatdagi sana va vaqt qabul qilinadi.
                            ✅ Format: <b>YYYY-MM-DD HH:mm</b>
                            """)
                    .replyMarkup(replyButtonService.buildCancelButton())
                    .parseMode(ParseMode.HTML)
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

    private SendMessage carBooking(Long chatId, Integer messageId, String data) {

        EditMessageReplyMarkup replyMarkup = new EditMessageReplyMarkup();

        replyMarkup.setChatId(chatId);
        replyMarkup.setMessageId(messageId);
        replyMarkup.setReplyMarkup(null);

        telegramBot.editMessageReplyMarkup(replyMarkup);

        long carId = Long.parseLong(data.split(":")[1]);

        BookingCreateDTO booking = new BookingCreateDTO();

        booking.setCarId(carId);

        telegramBot.getUserBookings().put(chatId, booking);

        return SendMessage.builder()
                .chatId(chatId)
                .text("""
                        🚗 <b>Bron qilish jarayoni</b>
                        
                        Siz mashinani <b>oʻzingiz uchun</b> olasizmi yoki <b>boshqa birov uchun</b>?
                        
                        🔹 <i>Oʻzim uchun</i> — mening ismim va maʼlumotlarim bilan bron qilinadi.
                        🔹 <i>Boshqa birov uchun</i> — boshqa shaxsning ism-sharifi va telefon raqamini kiriting.""")
                .parseMode(ParseMode.HTML)
                .replyMarkup(inlineButtonService.buildIsForSelfOr())
                .build();
    }

    private AnswerCallbackQuery addOrRemoveFavorites(String data, Long chatId, String callbackId) {

        long carID = Long.parseLong(data.split(":")[1]);

        TelegramUser user = telegramUserRepository.findByChatIdOrThrowException(chatId);

        TgFavoriteDTO checkFavorite = favoriteClient.getCheckFavorite(user.getUserId(), carID);

        if (checkFavorite.isHave()) {

            favoriteClient.deleteFavorite(checkFavorite.getId());

            return AnswerCallbackQuery.builder()
                    .callbackQueryId(callbackId)
                    .text("Sevimlilardan o'chirildi !")
                    .build();

        }

        FavoriteDTO createDTO = new FavoriteDTO();

        createDTO.setCarId(carID);
        createDTO.setUserId(user.getUserId());

        favoriteClient.createFavorite(createDTO);

        return AnswerCallbackQuery.builder()
                .callbackQueryId(callbackId)
                .text("Sevimlilarga qo'shildi !")
                .build();
    }

    private SendMessage getCarComments(long carId, PageableDTO<ReviewDTO> reviews, Long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = inlineButtonService.buildPages(carId, reviews, PageEnum.CAR_COMMENT);

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