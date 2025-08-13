package uz.dev.rentcarbot.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import uz.dev.rentcarbot.client.BookingClient;
import uz.dev.rentcarbot.client.PaymentClient;
import uz.dev.rentcarbot.client.StatisticsClient;
import uz.dev.rentcarbot.entity.TelegramUser;
import uz.dev.rentcarbot.enums.RoleEnum;
import uz.dev.rentcarbot.enums.StepEnum;
import uz.dev.rentcarbot.payload.BookingDTO;
import uz.dev.rentcarbot.payload.PaymentDTO;
import uz.dev.rentcarbot.payload.UserStatisticDTO;
import uz.dev.rentcarbot.repository.TelegramUserRepository;
import uz.dev.rentcarbot.service.template.AdminTextService;
import uz.dev.rentcarbot.service.template.InlineButtonService;
import uz.dev.rentcarbot.service.template.ReplyButtonService;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Created by: asrorbek
 * DateTime: 8/13/25 20:17
 **/

@Service
public class AdminTextServiceImpl implements AdminTextService {

    private final TelegramUserRepository userRepository;
    private final StatisticsClient statisticsClient;
    private final InlineButtonService inlineButtonService;
    private final BookingClient bookingClient;
    private final ReplyButtonService replyButtonService;
    private final PaymentClient paymentClient;

    public AdminTextServiceImpl(TelegramUserRepository userRepository, StatisticsClient statisticsClient, InlineButtonService inlineButtonService, BookingClient bookingClient, ReplyButtonService replyButtonService, PaymentClient paymentClient) {
        this.userRepository = userRepository;
        this.statisticsClient = statisticsClient;
        this.inlineButtonService = inlineButtonService;
        this.bookingClient = bookingClient;
        this.replyButtonService = replyButtonService;
        this.paymentClient = paymentClient;
    }

    @Override
    @Transactional
    public BotApiMethod<?> process(Message message) {

        String text = message.getText();

        Long chatId = message.getChatId();

        TelegramUser user = userRepository.findByChatIdOrThrowException(chatId);

        if (user.getStep().equals(StepEnum.SELECT_MENU_ADMIN)) {

            if (text.equals("\uD83D\uDC64 Foydalanuvchilar")) {

                String sb = getStatistics();

                return SendMessage.builder()
                        .chatId(chatId)
                        .text(sb)
                        .parseMode(ParseMode.HTML)
                        .build();

            } else if (text.equals("\uD83D\uDCE6 Buyurtmalar")) {

                String sendMessage = """
                        <b>📦 Buyurtmalar</b>
                        
                        Bu bo‘limda siz quyidagilarni amalga oshirishingiz mumkin:
                        ✅ Buyurtmani <b>tasdiqlash</b>
                        📥 Buyurtmani <b>qabul qilish</b>
                        ❌ Buyurtmani <b>bekor qilish</b>
                        💳 To‘lovni <b>tasdiqlash</b>
                        🚫 To‘lovni <b>bekor qilish</b>
                        """;

                InlineKeyboardMarkup inlineKeyboardMarkup = inlineButtonService.buildBookingMenu();

                return SendMessage.builder()
                        .chatId(chatId)
                        .text(sendMessage)
                        .parseMode(ParseMode.HTML)
                        .replyMarkup(inlineKeyboardMarkup)
                        .build();

            }

        } else if (user.getStep().toString().startsWith("BOOKING_")) {

            if (text.equals("Orqaga")) {

                user.setStep(StepEnum.SELECT_MENU_ADMIN);

                userRepository.save(user);

                return SendMessage.builder()
                        .chatId(chatId)
                        .text("MENU")
                        .replyMarkup(replyButtonService.buildMenuButtons(RoleEnum.ADMIN))
                        .build();

            }

            Pattern pattern = Pattern.compile("^\\d+$");

            if (pattern.matcher(text).matches()) {

                Long bookingId = Long.parseLong(text);

                BookingDTO bookingDTO = null;

                if (user.getStep().equals(StepEnum.BOOKING_CONFIRM)) {

                    bookingDTO = bookingClient.confirmBooking(bookingId);


                } else if (user.getStep().equals(StepEnum.BOOKING_COMPLETE)) {

                    bookingDTO = bookingClient.completeBooking(bookingId);

                } else if (user.getStep().equals(StepEnum.BOOKING_CANCEL)) {

                    bookingDTO = bookingClient.cancelBooking(bookingId);

                }

                if (Objects.nonNull(bookingDTO)) {

                    user.setStep(StepEnum.SELECT_MENU_ADMIN);

                    userRepository.save(user);

                    return SendMessage.builder()
                            .chatId(chatId)
                            .text(buildBookingMessage(bookingDTO))
                            .parseMode(ParseMode.HTML)
                            .replyMarkup(replyButtonService.buildMenuButtons(RoleEnum.ADMIN))
                            .build();
                }

            } else {

                return SendMessage.builder()
                        .chatId(chatId)
                        .text("Iltimos faqat raqam kiriting !")
                        .build();

            }

        } else if (user.getStep().toString().startsWith("PAYMENT_")) {

            if (text.equals("Orqaga")) {

                user.setStep(StepEnum.SELECT_MENU_ADMIN);

                userRepository.save(user);

                return SendMessage.builder()
                        .chatId(chatId)
                        .text("MENU")
                        .replyMarkup(replyButtonService.buildMenuButtons(RoleEnum.ADMIN))
                        .build();

            }

            Pattern pattern = Pattern.compile("^\\d+$");

            if (pattern.matcher(text).matches()) {

                long bookingId = Long.parseLong(text);

                PaymentDTO paymentDTO = null;

                if (user.getStep().equals(StepEnum.PAYMENT_CONFIRM)) {

                    paymentDTO = paymentClient.confirmPayment(bookingId);

                } else if (user.getStep().equals(StepEnum.PAYMENT_CANCEL)) {

                    paymentDTO = paymentClient.cancelPayment(bookingId);

                }

                if (Objects.nonNull(paymentDTO)) {

                    user.setStep(StepEnum.SELECT_MENU_ADMIN);

                    userRepository.save(user);

                    return SendMessage.builder()
                            .chatId(chatId)
                            .text(buildPaymentMessage(paymentDTO))
                            .parseMode(ParseMode.HTML)
                            .replyMarkup(replyButtonService.buildMenuButtons(RoleEnum.ADMIN))
                            .build();

                }

            } else {

                return SendMessage.builder()
                        .chatId(chatId)
                        .text("Iltimos faqat raqam kiriting !")
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

    public String buildPaymentMessage(PaymentDTO dto) {

        return "💳 *To'lov ma'lumotlari*\n" +
                "━━━━━━━━━━━━━━━━\n" +
                "🔔 *ID:* `" + dto.getId() + "`\n" +
                "📅 *Buyurtma ID:* `" + dto.getBookingId() + "`\n" +
                "💵 *Miqdor:* `" + dto.getAmount() + " so'm`\n" +
                "💸 *To'lov turi:* " + dto.getPaymentMethod() + "\n" +
                "✅ *Holati:* " + dto.getStatus();
    }

    public String buildBookingMessage(BookingDTO booking) {

        StringBuilder sb = new StringBuilder();

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

        return sb.toString();

    }

    private String getStatistics() {
        UserStatisticDTO userStatistics = statisticsClient.getUserStatistics();

        return "<b>📊 Foydalanuvchilar statistikasi</b>\n\n" +
                "👥 <b>Umumiy foydalanuvchilar:</b> " + userStatistics.getTotalUsers() + "\n" +
                "🗑 <b>O‘chirilganlar:</b> " + userStatistics.getDeletedUsers() + "\n" +
                "👨‍💼 <b>Adminlar:</b> " + userStatistics.getAdmins() + "\n" +
                "🙋‍♂️ <b>Oddiy foydalanuvchilar:</b> " + userStatistics.getUsers() + "\n\n" +
                "🗓 <b>Oxirgi oyda qo‘shilgan:</b> " + userStatistics.getLastMonthUsers() + "\n" +
                "📅 <b>Oxirgi haftada qo‘shilgan:</b> " + userStatistics.getLastWeekUsers() + "\n" +
                "📌 <b>Bugun qo‘shilgan:</b> " + userStatistics.getTodayUsers();
    }
}
