package ua.goit.telegrambot;

public class SomeButton {

    private final String buttonName;
    private final String callback;

    public String getButtonName() {
        return buttonName;
    }

    public String getCallback() {
        return callback;
    }

    public SomeButton(String buttonName, String callback) {
        this.buttonName = buttonName;
        this.callback = callback;
    }
}
