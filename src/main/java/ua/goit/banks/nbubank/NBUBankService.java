package ua.goit.banks.nbubank;

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
public class NBUBankService implements Banks {

    private static List<WorkingCurrency> currencies;
    private final String name = "Національний банк України";

    @Override
    public List<WorkingCurrency> getCurrencies() {
        return currencies;
    }

    @Override
    public void updateCurrentData() throws IOException {
        String url = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";
        String json = Jsoup.connect(url).ignoreContentType(true).get().body().text();
        Type type = TypeToken.getParameterized(List.class, NBUCurrent.class).getType();
        List<NBUCurrent> nbuCurrencies = new Gson().fromJson(json, type);

        filterAndMapCurrencies(nbuCurrencies);
    }

    private void filterAndMapCurrencies(List<NBUCurrent> nbuCurrencies) {
        currencies = nbuCurrencies.stream()
                .filter(x -> x.getR030() == 840 || x.getR030() == 978)
                .map(x -> new WorkingCurrency(
                        Currencies.valueOf(x.getCc()),
                        x.getRate(),
                        x.getRate()))
                .collect(Collectors.toList());
    }

}
