package ua.goit.banks.nbubank;

import lombok.Data;

@Data
public class NBUCurrent {
    private final int r030;
    private final String txt;
    private final float rate;
    private final String cc;
    private final String exchangedate;
}
