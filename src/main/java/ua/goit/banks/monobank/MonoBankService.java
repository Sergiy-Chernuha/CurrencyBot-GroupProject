package ua.goit.banks.monobank;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import org.jsoup.Jsoup;
import ua.goit.banks.Banks;
import ua.goit.banks.Currencies;
import ua.goit.banks.WorkingCurrency;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MonoBankService implements Banks {

    private static List<WorkingCurrency> currencies;
    private final String name = "МоноБанк";

    @Override
    public List<WorkingCurrency> getCurrencies() {
        return currencies;
    }

    @Override
    public void updateCurrentData() throws IOException {
        String url = "https://api.monobank.ua/bank/currency";
        String json = Jsoup.connect(url).ignoreContentType(true).get().body().text();
        Type type = TypeToken.getParameterized(List.class, MonoCurrency.class).getType();
        List<MonoCurrency> monoCurrencies = new Gson().fromJson(json, type);

        filterAndMapCurrencies(monoCurrencies);
    }

    private String parseIsoToCurrency(int code) {
        if (code == 840) {
            return "USD";
        } else if (code == 978) {
            return "EUR";
        } else if (code == 980) {
            return "UAH";
        }

        return "000";
    }

    private void filterAndMapCurrencies(List<MonoCurrency> monoCurrencies) {
        currencies = monoCurrencies.stream()
                .filter(x -> x.getCurrencyCodeA() == 840 || x.getCurrencyCodeA() == 978)
                .filter(x -> x.getCurrencyCodeB() == 980)
                .map(x -> new WorkingCurrency(
                        Currencies.valueOf(parseIsoToCurrency(x.getCurrencyCodeA())),
                        x.getRateSell(),
                        x.getRateBuy()))
                .collect(Collectors.toList());
    }
}
