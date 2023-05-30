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
public class NBUBank implements Banks {

    List<WorkingCurrency> currencies;
    String name = "NBUBank";

    @Override
    public void updateCurrentData() throws IOException {
        String url = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";
        String json = Jsoup.connect(url).ignoreContentType(true).get().body().text();
        Type type = TypeToken.getParameterized(List.class, NBUCurrent.class).getType();
        List<NBUCurrent> monoCurrencies = new Gson().fromJson(json, type);

//		_____change for working with other currencies
        currencies = monoCurrencies.stream().filter(x -> x.getR030() == 840 || x.getR030() == 978)
                .map(x -> new WorkingCurrency(Currencies.valueOf(x.getCc()), x.getRate(), x.getRate()))
                .collect(Collectors.toList());

        System.out.println(name + " " + currencies.get(0).getName());
    }
}
