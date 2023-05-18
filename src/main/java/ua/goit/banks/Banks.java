package ua.goit.banks;

import java.io.IOException;
import java.util.List;

public interface Banks {
    String getName();
    List<WorkingCurrency> getCurrencies();
    void updateCurrentData() throws IOException;
}
