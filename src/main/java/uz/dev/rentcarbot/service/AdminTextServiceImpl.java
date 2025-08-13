package uz.dev.rentcarbot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import uz.dev.rentcarbot.client.StatisticsClient;
import uz.dev.rentcarbot.entity.TelegramUser;
import uz.dev.rentcarbot.enums.StepEnum;
import uz.dev.rentcarbot.payload.UserStatisticDTO;
import uz.dev.rentcarbot.repository.TelegramUserRepository;
import uz.dev.rentcarbot.service.template.AdminTextService;

/**
 * Created by: asrorbek
 * DateTime: 8/13/25 20:17
 **/

@Service
public class AdminTextServiceImpl implements AdminTextService {

    private final TelegramUserRepository userRepository;
    private final StatisticsClient statisticsClient;

    public AdminTextServiceImpl(TelegramUserRepository userRepository, StatisticsClient statisticsClient) {
        this.userRepository = userRepository;
        this.statisticsClient = statisticsClient;
    }

    @Override
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
