package ua.goit.userssetting;

import lombok.Data;
import ua.goit.banks.Currencies;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Data
public class ChatBotSettings {

    private final Long chatId;
    private int numberOfDecimal = 2;
    private List<String> banks = List.of("PrivatBank");
    private List<Currencies> choicesCurrencies = List.of(Currencies.USD);
    private List<Integer> reminderHours = new LinkedList<>();
    private boolean reminderStarted = false;

    public ChatBotSettings(Long chatId) {
        this.chatId = chatId;
    }

    public boolean isReminderStarted() {
        return reminderStarted;
    }
}
