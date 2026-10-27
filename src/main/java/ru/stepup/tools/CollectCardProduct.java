package ru.stepup.tools;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import java.util.ArrayList;
import java.util.List;

public class CollectCardProduct {

    private final List<String> valueAttributeCardProducts = new ArrayList<>();

    //собираем карточки товаров в массив
    public List<String> getCardProductByAttribute(ElementsCollection cardProducts, String attribute) {
        for (SelenideElement element : cardProducts) {
            valueAttributeCardProducts.add(element.getAttribute(attribute));
        }
        return valueAttributeCardProducts;
    }
}
