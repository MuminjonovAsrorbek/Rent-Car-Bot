package uz.dev.rentcarbot.service;

import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import uz.dev.rentcarbot.client.*;
import uz.dev.rentcarbot.config.MyTelegramBot;
import uz.dev.rentcarbot.entity.TelegramUser;
import uz.dev.rentcarbot.enums.PageEnum;
import uz.dev.rentcarbot.enums.StepEnum;
import uz.dev.rentcarbot.payload.*;
import uz.dev.rentcarbot.repository.TelegramUserRepository;
import uz.dev.rentcarbot.service.template.InlineButtonService;
import uz.dev.rentcarbot.service.template.ReplyButtonService;
import uz.dev.rentcarbot.service.template.UserTextService;
import uz.dev.rentcarbot.utils.DateTimeValidator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Created by: asrorbek
 * DateTime: 8/12/25 20:10
 **/

@Service
public class UserTextServiceImpl implements UserTextService {

    private final TelegramUserRepository userRepository;
    private final ReplyButtonService replyButtonService;
    private final InlineButtonService inlineButtonService;
    private final MyTelegramBot telegramBot;
    private final OfficeClient officeClient;
    private final CarClient carClient;
    private final PromoCodeClient promoCodeClient;
    private final BookingClient bookingClient;
    private final NotificationClient notificationClient;

    public UserTextServiceImpl(TelegramUserRepository userRepository, ReplyButtonService replyButtonService, InlineButtonService inlineButtonService, @Lazy MyTelegramBot telegramBot, OfficeClient officeClient, CarClient carClient, PromoCodeClient promoCodeClient, BookingClient bookingClient, NotificationClient notificationClient) {
        this.userRepository = userRepository;
        this.replyButtonService = replyButtonService;
        this.inlineButtonService = inlineButtonService;
        this.telegramBot = telegramBot;
        this.officeClient = officeClient;
        this.carClient = carClient;
        this.promoCodeClient = promoCodeClient;
        this.bookingClient = bookingClient;
        this.notificationClient = notificationClient;
    }


    @Override
    public BotApiMethod<?> process(Message message) {

        String text = message.getText();

        Long chatId = message.getChatId();

        TelegramUser user = userRepository.findByChatIdOrThrowException(chatId);

        if (user.getStep().equals(StepEnum.SELECT_MENU)) {

            switch (text) {
                case "\uD83D\uDD11 Ijaraga olish" -> {

                    return SendMessage.builder()
                            .chatId(chatId)
                            .text("""
                                    <b>\uD83D\uDE97 Ijaraga olish uchun mavjud mashinalar:</b>
                                    <i>Quyidagi tugmani bosing va hozirda band bo‘lmagan avtomobillar ro‘yxatini ko‘ring.</i>
                                    \s""")
                            .parseMode(ParseMode.HTML)
                            .replyMarkup(inlineButtonService.buildAvailableCars())
                            .build();
                }
                case "⚖️ Jarimalar" -> {

                    return SendMessage.builder()
                            .chatId(chatId)
                            .text("""
                                    <b>⚖️ Jarimalar bo‘limi</b>
                                    
                                    Siz bu bo‘limda quyidagi ma’lumotlarni ko‘rishingiz mumkin: \s
                                    \uD83C\uDD95 <b>Yangi jarimalar</b> — Hozirgi va to‘lanmagan jarimalaringiz ro‘yxati. \s
                                    \uD83D\uDCDC <b>Barcha jarimalar</b> — Eski va yangi barcha jarimalaringiz tarixi.
                                    
                                    <i>Jarimalaringizni vaqtida to‘lab, muammolardan qoching \uD83D\uDE09</i>
                                    """)
                            .parseMode(ParseMode.HTML)
                            .replyMarkup(inlineButtonService.buildPenaltyMenu())
                            .build();
                }
                case "Mening buyurtmalarim" -> {

                    PageableDTO<BookingDTO> myBookings = bookingClient.getMyBookings(0, 1);

                    StringBuilder sendMessage = getUserBookings(myBookings, chatId);

                    myBookings.setCurrentPage(0);

                    return SendMessage.builder()
                            .chatId(chatId)
                            .text(sendMessage.toString())
                            .parseMode(ParseMode.HTML)
                            .replyMarkup(inlineButtonService.buildPages(0L, myBookings, PageEnum.BOOKING))
                            .build();
                }
                case "\uD83D\uDD14 Xabarnomalar" -> {

                    PageableDTO<NotificationDTO> allNotifications = notificationClient.getMyAllNotifications(0, 10);

                    allNotifications.setCurrentPage(0);

                    List<NotificationDTO> notifications = allNotifications.getObjects();

                    PageableDTO<NotificationDTO> myUnreadNotifications = notificationClient.getMyUnreadNotifications(0, 10);

                    String notification = getNotification(allNotifications, myUnreadNotifications, notifications);

                    return SendMessage.builder()
                            .chatId(chatId)
                            .text(notification)
                            .replyMarkup(inlineButtonService.buildNotificationPages(allNotifications, PageEnum.NOTIFICATION))
                            .parseMode(ParseMode.HTML)
                            .build();

                }
            }

        } else if (user.getStep().equals(StepEnum.PICKUP_DATE)) {

            return pickupDate(text, user, chatId);

        } else if (user.getStep().equals(StepEnum.RETURN_DATE)) {

            return returnDate(text, user, chatId);

        } else if (user.getStep().equals(StepEnum.PAYMENT_METHOD)) {

            if (text.equals("Orqaga")) {

                user.setStep(StepEnum.SELECT_MENU);

                userRepository.save(user);

                telegramBot.getUserBookings().remove(chatId);

                return SendMessage.builder()
                        .chatId(chatId)
                        .text("MENU")
                        .build();

            }

        } else if (user.getStep().equals(StepEnum.SEND_PROMO_CODE)) {

            return sendPromoCode(chatId, text, user);

        } else if (user.getStep().equals(StepEnum.RECIPIENT_FULL_NAME)) {

            telegramBot.getUserBookings().get(chatId).setRecipientFullName(text);

            user.setStep(StepEnum.RECIPIENT_PHONE);

            userRepository.save(user);

            return SendMessage.builder()
                    .chatId(chatId)
                    .text("Qabul qiluvchining telefon raqamini kiriting :")
                    .build();

        } else if (user.getStep().equals(StepEnum.RECIPIENT_PHONE)) {

            Pattern regex = Pattern.compile("^\\+998\\d{9}$");

            if (regex.matcher(text).matches()) {

                telegramBot.getUserBookings().get(chatId).setRecipientPhone(text);

                PageableDTO<OfficeDTO> dtos = officeClient.getAllOffices(0, 10);

                List<OfficeDTO> allOffices = dtos.getObjects();

                StringBuilder sb = new StringBuilder();

                sb.append("<b>\uD83D\uDCCD Qaysi ofisimizdan olib ketasiz?</b>").append("\n\n");

                for (int i = 0; i < allOffices.size(); i++) {

                    sb.append(i + 1).append(" . ").append("<b>").append(allOffices.get(i).getName()).append("</b>").append("\n");
                    sb.append("Manzil: ").append(allOffices.get(i).getAddress()).append("\n\n");

                }

                user.setStep(StepEnum.PICKUP_OFFICE);

                userRepository.save(user);

                return SendMessage.builder()
                        .chatId(chatId)
                        .text(sb.toString())
                        .parseMode(ParseMode.HTML)
                        .replyMarkup(inlineButtonService.buildOffices(dtos))
                        .build();
            } else {

                return SendMessage.builder()
                        .chatId(chatId)
                        .text("Telefon raqami formati notog'ri . Misol : +998912345678")
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

    @Override
    public String getNotification(PageableDTO<NotificationDTO> allNotifications, PageableDTO<NotificationDTO> myUnreadNotifications, List<NotificationDTO> notifications) {

        StringBuilder sb = new StringBuilder();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        Long total = (allNotifications.getTotalElements() == null) ? 0 : allNotifications.getTotalElements();
        long unreadCount = (myUnreadNotifications.getTotalElements() == null) ? 0 : myUnreadNotifications.getTotalElements();

        sb.append("<b>🔔 Bildirishnomalar</b>\n");
        sb.append("<i>Umumiy: ").append(total).append(", O'qilmagan: ").append(unreadCount).append("</i>\n\n");

        if (notifications == null || notifications.isEmpty()) {
            sb.append("<b>✅ Hozircha bildirishnomalar mavjud emas.</b>");
            return sb.toString();
        }

        for (NotificationDTO n : notifications) {
            String emoji;
            switch (n.getType()) {
                case INFO -> emoji = "ℹ️";
                case WARNING -> emoji = "⚠️";
                case ERROR -> emoji = "❌";
                default -> emoji = "🔔";
            }

            String timeStr = "-";
            if (n.getCreatedAt() != null) {
                timeStr = n.getCreatedAt().toLocalDateTime().format(fmt);
            }


            String safeMessage = n.getMessage() == null ? "-" : HtmlUtils.htmlEscape(n.getMessage());


            if (!n.isRead()) {
                sb.append("<b>")
                        .append(emoji).append(" [").append(n.getType()).append("] ")
                        .append(timeStr).append(" — ").append(safeMessage)
                        .append(" (ID: ").append(n.getId()).append(")")
                        .append("</b>\n\n");
            } else {
                sb.append(emoji).append(" [").append(n.getType()).append("] ")
                        .append(timeStr).append(" — ").append(safeMessage)
                        .append(" (ID: ").append(n.getId()).append(")\n\n");
            }
        }
        return sb.toString();
    }

    @Override
    public StringBuilder getUserBookings(PageableDTO<BookingDTO> myBookings, Long chatId) {

        StringBuilder sb = new StringBuilder();

        List<BookingDTO> bookingDTOS = myBookings.getObjects();

        BookingDTO booking = bookingDTOS.get(0);

        sb.append("<b>📄 Booking Ma'lumotlari</b>\n\n");

        sb.append("<b>🆔 Buyurtma ID:</b> ").append(booking.getId()).append("\n");
        sb.append("<b>👤 Mijoz:</b> ").append(booking.getUserFullName()).append("\n");
        sb.append("<b>📅 Status:</b> ").append(booking.getStatus()).append("\n\n");


        sb.append("<b>🚗 Mashina</b>\n");
        sb.append("• Brand: ").append(booking.getCarBrand()).append("\n");
        sb.append("• Model: ").append(booking.getCarModel()).append("\n");
        sb.append("• Joylar soni: ").append(booking.getCarSeats()).append("\n");
        sb.append("• Yoqilg'i turi: ").append(booking.getCarFuelType()).append("\n");
        sb.append("• Sarfi: ").append(booking.getCarFuelConsumption()).append(" L/100km\n");
        sb.append("• Transmissiya: ").append(booking.getCarTransmission()).append("\n\n");


        sb.append("<b>📆 Olish vaqti:</b> ").append(booking.getPickupDate()).append("\n");
        sb.append("<b>📆 Qaytarish vaqti:</b> ").append(booking.getReturnDate()).append("\n\n");


        if (booking.getPickupOffice() != null) {
            sb.append("<b>📍 Olish ofisi:</b> ").append(booking.getPickupOffice().getName())
                    .append(" — ").append(booking.getPickupOffice().getAddress()).append("\n");
        }
        if (booking.getReturnOffice() != null) {
            sb.append("<b>📍 Qaytarish ofisi:</b> ").append(booking.getReturnOffice().getName())
                    .append(" — ").append(booking.getReturnOffice().getAddress()).append("\n");
        }
        sb.append("\n");


        sb.append("<b>👥 Kim uchun:</b> ").append(booking.getIsForSelf() ? "O‘zi uchun" : "Boshqa shaxs uchun").append("\n");
        if (!booking.getIsForSelf()) {
            sb.append("<b>👤 Qabul qiluvchi:</b> ").append(booking.getRecipientFullName()).append("\n");
            sb.append("<b>📞 Tel:</b> ").append(booking.getRecipientPhone()).append("\n");
        }
        sb.append("\n");


        if (booking.getPayment() != null) {
            sb.append("<b>💳 To'lov</b>\n");
            sb.append("• Summasi: ").append(booking.getPayment().getAmount()).append(" so'm\n");
            sb.append("• Usuli: ").append(booking.getPayment().getPaymentMethod()).append("\n");
            sb.append("• Status: ").append(booking.getPayment().getStatus()).append("\n\n");
        }


        sb.append("<b>🎁 Promokod:</b> ").append(booking.getHasPromoCode() ? "Bor" : "Yo‘q").append("\n");


        sb.append("<b>💰 Umumiy narx:</b> ").append(booking.getTotalPrice()).append(" so'm\n");

        return sb;
    }

    @Transactional
    public SendMessage sendPromoCode(Long chatId, String text, TelegramUser user) {

        BookingCreateDTO dto = telegramBot.getUserBookings().get(chatId);

        if (text.equals("Orqaga")) {

            user.setStep(StepEnum.CHECKED_BOOKING);

            userRepository.save(user);

            return checkedBooking(dto, chatId);

        } else {

            boolean exists = promoCodeClient.codeValidate(text);

            if (exists) {

                dto.setPromoCode(text);

                user.setStep(StepEnum.CHECKED_BOOKING);

                userRepository.save(user);

                return checkedBooking(dto, chatId);

            } else {

                return SendMessage.builder()
                        .chatId(chatId)
                        .text("Siz kiritgan " + text + " promo-code topilmadi !")
                        .build();

            }

        }
    }

    @Override
    public SendMessage checkedBooking(BookingCreateDTO dto, Long chatId) {

        CarDTO car = carClient.getCarById(dto.getCarId());

        OfficeDTO pickupOffice = officeClient.getOfficeById(dto.getPickupOfficeId());

        OfficeDTO returnOffice = officeClient.getOfficeById(dto.getPickupOfficeId());

        StringBuilder sb = new StringBuilder();

        sb.append("<b>📋 Bron ma'lumotlari</b>\n\n");

        sb.append("<b>🚗 Avtomobil:</b> ")
                .append(car.getBrand()).append(" ")
                .append(car.getModel())
                .append(" (").append(car.getSeats()).append(" o‘rinli, ")
                .append(car.getFuelType()).append(", ")
                .append(car.getTransmission()).append(")\n\n");

        sb.append("<b>📍 Olish manzili:</b> ")
                .append(pickupOffice.getName())
                .append(" — ").append(pickupOffice.getAddress()).append("\n");

        sb.append("<b>📍 Qaytarish manzili:</b> ")
                .append(returnOffice.getName())
                .append(" — ").append(returnOffice.getAddress()).append("\n\n");

        sb.append("<b>📅 Olish vaqti:</b> ").append(dto.getPickupDate()).append("\n");
        sb.append("<b>📅 Qaytarish vaqti:</b> ").append(dto.getReturnDate()).append("\n\n");

        sb.append("<b>💳 To‘lov turi:</b> ").append(dto.getPaymentMethod()).append("\n");

        if (dto.isForSelf()) {
            sb.append("<b>👤 Kim uchun:</b> O‘zi uchun\n");
        } else {
            sb.append("<b>👤 Qabul qiluvchi:</b> ").append(dto.getRecipientFullName()).append("\n");
            sb.append("<b>📞 Telefon:</b> ").append(dto.getRecipientPhone()).append("\n");
        }

        if (dto.getPromoCode() != null && !dto.getPromoCode().isBlank()) {
            sb.append("<b>🎟 Promo kod:</b> ").append(dto.getPromoCode()).append("\n");
        }

        sb.append("\n<b>✅ Ma'lumotlar to‘g‘rimi?</b>");

        return SendMessage.builder()
                .chatId(chatId)
                .text(sb.toString())
                .parseMode(ParseMode.HTML)
                .replyMarkup(inlineButtonService.buildYesOrNo("booking"))
                .build();
    }

    @Transactional
    public SendMessage returnDate(String text, TelegramUser user, Long chatId) {

        if (text.equals("Orqaga")) {

            user.setStep(StepEnum.SELECT_MENU);

            userRepository.save(user);

            telegramBot.getUserBookings().remove(chatId);

            return SendMessage.builder()
                    .chatId(chatId)
                    .text("MENU")
                    .replyMarkup(replyButtonService.buildMenuButtons(user.getRole()))
                    .build();

        }

        Optional<LocalDateTime> optionalTime = DateTimeValidator.validDateTime(text);

        if (optionalTime.isEmpty()) {

            return SendMessage.builder()
                    .chatId(chatId)
                    .text("⛔ Faqat shu formatdagi sana va vaqt qabul qilinadi.  \n" +
                            "✅ Format: <b>YYYY-MM-DD HH:mm</b>  ")
                    .replyMarkup(replyButtonService.buildCancelButton())
                    .build();

        }

        LocalDateTime localDateTime = optionalTime.get();

        telegramBot.getUserBookings().get(chatId).setReturnDate(localDateTime);

        user.setStep(StepEnum.PAYMENT_METHOD);

        userRepository.save(user);

        return SendMessage.builder()
                .chatId(chatId)
                .text("To'lov usulini tanlang :")
                .replyMarkup(inlineButtonService.buildPaymentMethod())
                .build();
    }

    @Transactional
    public SendMessage pickupDate(String text, TelegramUser user, Long chatId) {

        if (text.equals("Orqaga")) {

            user.setStep(StepEnum.SELECT_MENU);

            userRepository.save(user);

            telegramBot.getUserBookings().remove(chatId);

            return SendMessage.builder()
                    .chatId(chatId)
                    .text("MENU")
                    .replyMarkup(replyButtonService.buildMenuButtons(user.getRole()))
                    .build();

        }

        Optional<LocalDateTime> optionalTime = DateTimeValidator.validDateTime(text);

        if (optionalTime.isEmpty()) {

            return SendMessage.builder()
                    .chatId(chatId)
                    .text("⛔ Faqat shu formatdagi sana va vaqt qabul qilinadi.  \n" +
                            "✅ Format: <b>YYYY-MM-DD HH:mm</b>  ")
                    .replyMarkup(replyButtonService.buildCancelButton())
                    .build();

        }

        LocalDateTime localDateTime = optionalTime.get();

        telegramBot.getUserBookings().get(chatId).setPickupDate(localDateTime);

        PageableDTO<OfficeDTO> dtos = officeClient.getAllOffices(0, 10);

        List<OfficeDTO> allOffices = dtos.getObjects();

        StringBuilder message = new StringBuilder();

        message.append("<b>\uD83D\uDCCD Qaysi ofisimizga qaytarasiz?</b>").append("\n\n");

        for (int i = 0; i < allOffices.size(); i++) {

            message.append(i + 1).append(" . ").append("<b>").append(allOffices.get(i).getName()).append("</b>").append("\n");
            message.append("Manzil: ").append(allOffices.get(i).getAddress()).append("\n\n");

        }
        user.setStep(StepEnum.RETURN_OFFICE);

        userRepository.save(user);

        return SendMessage.builder()
                .chatId(chatId)
                .text(message.toString())
                .parseMode(ParseMode.HTML)
                .replyMarkup(inlineButtonService.buildOffices(dtos))
                .build();

    }
}
