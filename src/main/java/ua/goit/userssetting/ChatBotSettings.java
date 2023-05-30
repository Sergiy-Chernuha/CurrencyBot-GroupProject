package ua.goit.userssetting;

import lombok.Data;
import ua.goit.banks.Banks;
import ua.goit.banks.Currencies;
import ua.goit.banks.privatbank.PrivatBank;

import java.util.List;
@Data
public class ChatBotSettings {

    private int numberOfDecimal = 2;
    private Banks bank = new PrivatBank();
    private List<Currencies> choicesCurrencies = List.of(Currencies.USD);
    private int reminderTime = 0;
    private boolean reminderStarted = false;
    private final Long chatId ;

    public ChatBotSettings(Long chatId) {
        this.chatId = chatId;
    }

    public boolean isReminderStarted() {
            return reminderStarted;
    }
}
