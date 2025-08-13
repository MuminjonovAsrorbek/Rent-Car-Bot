package uz.dev.rentcarbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.dev.rentcarbot.client.CategoryClient;
import uz.dev.rentcarbot.client.OfficeClient;
import uz.dev.rentcarbot.client.StatisticsClient;
import uz.dev.rentcarbot.entity.TelegramUser;
import uz.dev.rentcarbot.enums.StepEnum;
import uz.dev.rentcarbot.payload.CategoryDTO;
import uz.dev.rentcarbot.payload.OfficeDTO;
import uz.dev.rentcarbot.payload.PageableDTO;
import uz.dev.rentcarbot.payload.UserStatisticDTO;
import uz.dev.rentcarbot.repository.TelegramUserRepository;
import uz.dev.rentcarbot.service.template.AdminTextService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminTextServiceImpl implements AdminTextService {

    private final TelegramUserRepository userRepository;
    private final StatisticsClient statisticsClient;
    private final CategoryClient categoryClient;
    private final OfficeClient officeClient;

    @Override
    public BotApiMethod<?> process(Message message) {

        String text = message.getText();
        Long chatId = message.getChatId();
        TelegramUser user = userRepository.findByChatIdOrThrowException(chatId);

        if (user.getStep().equals(StepEnum.SELECT_MENU_ADMIN)) {
            return handleMainMenu(text, chatId, user);
        } else if (user.getStep().equals(StepEnum.CATEGORY_MENU)) {
            return handleCategoryMenu(text, chatId, user);
        } else if (user.getStep().equals(StepEnum.CATEGORY_ADD_NAME)) {
            return handleCategoryAddName(text, chatId, user);
        } else if (user.getStep().equals(StepEnum.OFFICE_MENU)) {
            return handleOfficeMenu(text, chatId, user);
        } else if (user.getStep().equals(StepEnum.OFFICE_ADD_NAME)) {
            return handleOfficeAddName(text, chatId, user);
        } else if (user.getStep().equals(StepEnum.OFFICE_ADD_ADDRESS)) {
            return handleOfficeAddAddress(text, chatId, user);
        } else if (user.getStep().equals(StepEnum.OFFICE_ADD_LATITUDE)) {
            return handleOfficeAddLatitude(text, chatId, user);
        } else if (user.getStep().equals(StepEnum.OFFICE_ADD_LONGITUDE)) {
            return handleOfficeAddLongitude(text, chatId, user);
        }

        return SendMessage.builder()
                .chatId(chatId)
                .text("❌ Noto'g'ri buyruq! Iltimos, mavjud komandalarni ishlating.")
                .build();
    }

    private BotApiMethod<?> handleMainMenu(String text, Long chatId, TelegramUser user) {
        switch (text) {
            case "\uD83D\uDC64 Foydalanuvchilar" -> {
                String sb = getStatistics();
                return SendMessage.builder()
                        .chatId(chatId)
                        .text(sb)
                        .parseMode(ParseMode.HTML)
                        .build();
            }
            case "📂 Kategoriyalar" -> {
                user.setStep(StepEnum.CATEGORY_MENU);
                userRepository.save(user);
                return showCategoryMenu(chatId);
            }
            case "🏢 Ofislar" -> {
                user.setStep(StepEnum.OFFICE_MENU);
                userRepository.save(user);
                return showOfficeMenu(chatId);
            }
        }
        return getErrorMessage(chatId);
    }

    private BotApiMethod<?> handleCategoryMenu(String text, Long chatId, TelegramUser user) {
        switch (text) {
            case "➕ Kategoriya qo'shish" -> {
                user.setStep(StepEnum.CATEGORY_ADD_NAME);
                userRepository.save(user);
                return SendMessage.builder()
                        .chatId(chatId)
                        .text("📝 Yangi kategoriya nomini kiriting:")
                        .replyMarkup(buildBackButton())
                        .build();
            }
            case "📋 Kategoriyalar ro'yxati" -> {
                return showCategoriesList(chatId);
            }
            case "🔙 Orqaga" -> {
                user.setStep(StepEnum.SELECT_MENU_ADMIN);
                userRepository.save(user);
                return SendMessage.builder()
                        .chatId(chatId)
                        .text("🔙 Asosiy menyuga qaytdingiz")
                        .build();
            }
        }
        return getErrorMessage(chatId);
    }

    private BotApiMethod<?> handleCategoryAddName(String text, Long chatId, TelegramUser user) {
        if (text.equals("🔙 Orqaga")) {
            user.setStep(StepEnum.CATEGORY_MENU);
            userRepository.save(user);
            return showCategoryMenu(chatId);
        }

        try {
            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setName(text);

            CategoryDTO createdCategory = categoryClient.createCategory(categoryDTO);

            user.setStep(StepEnum.CATEGORY_MENU);
            userRepository.save(user);

            return SendMessage.builder()
                    .chatId(chatId)
                    .text("✅ Kategoriya muvaffaqiyatli qo'shildi!\n\n" +
                            "📂 Kategoriya: " + createdCategory.getName())
                    .parseMode(ParseMode.HTML)
                    .replyMarkup(buildCategoryMenuButtons())
                    .build();
        } catch (Exception e) {
            return SendMessage.builder()
                    .chatId(chatId)
                    .text("❌ Kategoriya qo'shishda xatolik yuz berdi. Qaytadan urinib ko'ring.")
                    .replyMarkup(buildBackButton())
                    .build();
        }
    }

    private BotApiMethod<?> handleOfficeMenu(String text, Long chatId, TelegramUser user) {
        switch (text) {
            case "➕ Ofis qo'shish" -> {
                user.setStep(StepEnum.OFFICE_ADD_NAME);
                userRepository.save(user);
                return SendMessage.builder()
                        .chatId(chatId)
                        .text("🏢 Yangi ofis nomini kiriting:")
                        .replyMarkup(buildBackButton())
                        .build();
            }
            case "📋 Ofislar ro'yxati" -> {
                return showOfficesList(chatId);
            }
            case "🔙 Orqaga" -> {
                user.setStep(StepEnum.SELECT_MENU_ADMIN);
                userRepository.save(user);
                return SendMessage.builder()
                        .chatId(chatId)
                        .text("🔙 Asosiy menyuga qaytdingiz")
                        .build();
            }
        }
        return getErrorMessage(chatId);
    }

    private BotApiMethod<?> handleOfficeAddName(String text, Long chatId, TelegramUser user) {
        if (text.equals("🔙 Orqaga")) {
            user.setStep(StepEnum.OFFICE_MENU);
            userRepository.save(user);
            return showOfficeMenu(chatId);
        }

        user.setTempData(text);
        user.setStep(StepEnum.OFFICE_ADD_ADDRESS);
        userRepository.save(user);

        return SendMessage.builder()
                .chatId(chatId)
                .text("📍 Ofis manzilini kiriting:")
                .replyMarkup(buildBackButton())
                .build();
    }

    private BotApiMethod<?> handleOfficeAddAddress(String text, Long chatId, TelegramUser user) {
        if (text.equals("🔙 Orqaga")) {
            user.setStep(StepEnum.OFFICE_ADD_NAME);
            userRepository.save(user);
            return SendMessage.builder()
                    .chatId(chatId)
                    .text("🏢 Yangi ofis nomini kiriting:")
                    .replyMarkup(buildBackButton())
                    .build();
        }

        user.setTempData(user.getTempData() + "|" + text);
        user.setStep(StepEnum.OFFICE_ADD_LATITUDE);
        userRepository.save(user);

        return SendMessage.builder()
                .chatId(chatId)
                .text("🌐 Latitude (kenglik) ni kiriting:\nMasalan: 41.2995")
                .replyMarkup(buildBackButton())
                .build();
    }

    private BotApiMethod<?> handleOfficeAddLatitude(String text, Long chatId, TelegramUser user) {
        if (text.equals("🔙 Orqaga")) {
            user.setStep(StepEnum.OFFICE_ADD_ADDRESS);
            userRepository.save(user);
            return SendMessage.builder()
                    .chatId(chatId)
                    .text("📍 Ofis manzilini kiriting:")
                    .replyMarkup(buildBackButton())
                    .build();
        }

        try {
            BigDecimal latitude = new BigDecimal(text);
            user.setTempData(user.getTempData() + "|" + text);
            user.setStep(StepEnum.OFFICE_ADD_LONGITUDE);
            userRepository.save(user);

            return SendMessage.builder()
                    .chatId(chatId)
                    .text("🌐 Longitude (uzunlik) ni kiriting:\nMasalan: 69.2401")
                    .replyMarkup(buildBackButton())
                    .build();
        } catch (NumberFormatException e) {
            return SendMessage.builder()
                    .chatId(chatId)
                    .text("❌ Noto'g'ri format! Raqam kiriting.\nMasalan: 41.2995")
                    .replyMarkup(buildBackButton())
                    .build();
        }
    }

    private BotApiMethod<?> handleOfficeAddLongitude(String text, Long chatId, TelegramUser user) {
        if (text.equals("🔙 Orqaga")) {
            user.setStep(StepEnum.OFFICE_ADD_LATITUDE);
            userRepository.save(user);
            return SendMessage.builder()
                    .chatId(chatId)
                    .text("🌐 Latitude (kenglik) ni kiriting:\nMasalan: 41.2995")
                    .replyMarkup(buildBackButton())
                    .build();
        }

        try {
            BigDecimal longitude = new BigDecimal(text);
            String[] tempData = user.getTempData().split("\\|");

            OfficeDTO officeDTO = new OfficeDTO();
            officeDTO.setName(tempData[0]);
            officeDTO.setAddress(tempData[1]);
            officeDTO.setLatitude(new BigDecimal(tempData[2]));
            officeDTO.setLongitude(longitude);

            OfficeDTO createdOffice = officeClient.createOffice(officeDTO);

            user.setStep(StepEnum.OFFICE_MENU);
            user.setTempData(null);
            userRepository.save(user);

            return SendMessage.builder()
                    .chatId(chatId)
                    .text("✅ Ofis muvaffaqiyatli qo'shildi!\n\n" +
                            "🏢 Nom: " + createdOffice.getName() + "\n" +
                            "📍 Manzil: " + createdOffice.getAddress() + "\n" +
                            "🌐 Koordinatalar: " + createdOffice.getLatitude() + ", " + createdOffice.getLongitude())
                    .parseMode(ParseMode.HTML)
                    .replyMarkup(buildOfficeMenuButtons())
                    .build();
        } catch (NumberFormatException e) {
            return SendMessage.builder()
                    .chatId(chatId)
                    .text("❌ Noto'g'ri format! Raqam kiriting.\nMasalan: 69.2401")
                    .replyMarkup(buildBackButton())
                    .build();
        } catch (Exception e) {
            return SendMessage.builder()
                    .chatId(chatId)
                    .text("❌ Ofis qo'shishda xatolik yuz berdi. Qaytadan urinib ko'ring.")
                    .replyMarkup(buildBackButton())
                    .build();
        }
    }

    private SendMessage showCategoryMenu(Long chatId) {
        return SendMessage.builder()
                .chatId(chatId)
                .text("📂 <b>Kategoriyalar boshqaruvi</b>\n\nQuyidagi amallardan birini tanlang:")
                .parseMode(ParseMode.HTML)
                .replyMarkup(buildCategoryMenuButtons())
                .build();
    }

    private SendMessage showOfficeMenu(Long chatId) {
        return SendMessage.builder()
                .chatId(chatId)
                .text("🏢 <b>Ofislar boshqaruvi</b>\n\nQuyidagi amallardan birini tanlang:")
                .parseMode(ParseMode.HTML)
                .replyMarkup(buildOfficeMenuButtons())
                .build();
    }

    private SendMessage showCategoriesList(Long chatId) {
        try {
            PageableDTO<CategoryDTO> categories = categoryClient.getAllCategories(0, 10);
            StringBuilder sb = new StringBuilder("📂 <b>Kategoriyalar ro'yxati:</b>\n\n");

            if (categories.getObjects().isEmpty()) {
                sb.append("❌ Hozircha kategoriyalar mavjud emas.");
            } else {
                for (CategoryDTO category : categories.getObjects()) {
                    sb.append("🔹 ").append(category.getName()).append("\n");
                }
            }

            return SendMessage.builder()
                    .chatId(chatId)
                    .text(sb.toString())
                    .parseMode(ParseMode.HTML)
                    .replyMarkup(buildCategoryMenuButtons())
                    .build();
        } catch (Exception e) {
            return SendMessage.builder()
                    .chatId(chatId)
                    .text("❌ Kategoriyalarni yuklashda xatolik yuz berdi.")
                    .replyMarkup(buildCategoryMenuButtons())
                    .build();
        }
    }

    private SendMessage showOfficesList(Long chatId) {
        try {
            PageableDTO<OfficeDTO> offices = officeClient.getAllOfficesAdmin(0, 10);
            StringBuilder sb = new StringBuilder("🏢 <b>Ofislar ro'yxati:</b>\n\n");

            if (offices.getObjects().isEmpty()) {
                sb.append("❌ Hozircha ofislar mavjud emas.");
            } else {
                for (OfficeDTO office : offices.getObjects()) {
                    sb.append("🏢 <b>").append(office.getName()).append("</b>\n");
                    sb.append("📍 ").append(office.getAddress()).append("\n\n");
                }
            }

            return SendMessage.builder()
                    .chatId(chatId)
                    .text(sb.toString())
                    .parseMode(ParseMode.HTML)
                    .replyMarkup(buildOfficeMenuButtons())
                    .build();
        } catch (Exception e) {
            return SendMessage.builder()
                    .chatId(chatId)
                    .text("❌ Ofislarni yuklashda xatolik yuz berdi.")
                    .replyMarkup(buildOfficeMenuButtons())
                    .build();
        }
    }

    private ReplyKeyboardMarkup buildCategoryMenuButtons() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add("➕ Kategoriya qo'shish");
        firstRow.add("📋 Kategoriyalar ro'yxati");
        keyboardRows.add(firstRow);

        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add("🔙 Orqaga");
        keyboardRows.add(secondRow);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    private ReplyKeyboardMarkup buildOfficeMenuButtons() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add("➕ Ofis qo'shish");
        firstRow.add("📋 Ofislar ro'yxati");
        keyboardRows.add(firstRow);

        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add("🔙 Orqaga");
        keyboardRows.add(secondRow);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    private ReplyKeyboardMarkup buildBackButton() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("🔙 Orqaga");
        keyboardRows.add(row);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    private String getStatistics() {
        UserStatisticDTO userStatistics = statisticsClient.getUserStatistics();

        return "<b>📊 Foydalanuvchilar statistikasi</b>\n\n" +
                "👥 <b>Umumiy foydalanuvchilar:</b> " + userStatistics.getTotalUsers() + "\n" +
                "🗑 <b>O'chirilganlar:</b> " + userStatistics.getDeletedUsers() + "\n" +
                "👨‍💼 <b>Adminlar:</b> " + userStatistics.getAdmins() + "\n" +
                "🙋‍♂️ <b>Oddiy foydalanuvchilar:</b> " + userStatistics.getUsers() + "\n\n" +
                "🗓 <b>Oxirgi oyda qo'shilgan:</b> " + userStatistics.getLastMonthUsers() + "\n" +
                "📅 <b>Oxirgi haftada qo'shilgan:</b> " + userStatistics.getLastWeekUsers() + "\n" +
                "📌 <b>Bugun qo'shilgan:</b> " + userStatistics.getTodayUsers();
    }

    private SendMessage getErrorMessage(Long chatId) {
        return SendMessage.builder()
                .chatId(chatId)
                .text("❌ Noto'g'ri buyruq! Iltimos, mavjud komandalarni ishlating.")
                .build();
    }
}
