package ua.goit.userssetting;

import lombok.Data;
import ua.goit.banks.Banks;
import ua.goit.banks.Currencies;

import java.util.List;

@Data
public class Client {
    private long chatId;
    private String registrationDate;
    private String username;
    private int alertTime;
    private List<Currencies> choicesCurrencies;
    private Banks bank;
    private int numberOfDecimal;
}
