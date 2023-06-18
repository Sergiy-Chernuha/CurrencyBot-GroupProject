package ua.goit.banks.privatbank;

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
public class PrivatBankService implements Banks {

    private static List<WorkingCurrency> currencies;
    private final String name = "PrivatBank";

    @Override
    public List<WorkingCurrency> getCurrencies() {
        return currencies;
    }

    @Override
    public void updateCurrentData() throws IOException {
        String url = "https://api.privatbank.ua/p24api/pubinfo?exchange&coursid=5";
        String json = Jsoup.connect(url).ignoreContentType(true).get().body().text();
        Type type = TypeToken.getParameterized(List.class, PrivatCurrency.class).getType();
        List<PrivatCurrency> privateCurrencies = new Gson().fromJson(json, type);

        filterAndMapCurrencies(privateCurrencies);
    }

    private void filterAndMapCurrencies(List<PrivatCurrency> privateCurrencies) {
        currencies = privateCurrencies.stream()
                .filter(x -> Currencies.contains(x.getCcy()))
                .map(x -> new WorkingCurrency(
                        Currencies.valueOf(x.getCcy()),
                        x.getSale(),
                        x.getBuy()))
                .collect(Collectors.toList());
    }
}
