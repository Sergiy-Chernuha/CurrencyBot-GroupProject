package ua.goit.telegrambot.buttonmenus;

import lombok.Data;

@Data
public class ButtonValue {
    private final String buttonName;
    private final String callback;
}
