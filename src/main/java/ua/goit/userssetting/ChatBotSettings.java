package ua.goit.userssetting;

import lombok.Data;
import ua.goit.banks.Currencies;
import ua.goit.banks.privatbank.PrivatBankService;

import java.util.List;

@Data
public class ChatBotSettings {

    private final Long chatId;
    private int numberOfDecimal = 2;
    private String bank = "PrivatBank";
    private List<Currencies> choicesCurrencies = List.of(Currencies.USD);
    private int reminderTime = 0;
    private boolean reminderStarted = false;

    public ChatBotSettings(Long chatId) {
        this.chatId = chatId;
    }

    public boolean isReminderStarted() {
        return reminderStarted;
    }
}
