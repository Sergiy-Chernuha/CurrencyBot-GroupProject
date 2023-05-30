package ua.goit.banks.privatbank;

import lombok.Data;

@Data
public class PrivatCurrency {
    String ccy;
    String base_ccy;
    float buy;
    float sale;
}
