package guru.qa.niffler.data.entity.spend;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Date;
import java.util.UUID;

@Getter
@Setter
public class SpendEntity implements Serializable {

    private UUID id;
    private String username;
    private CurrencyValues currency;
    private Date spendDate;
    private Double amount;
    private String description;
    private CategoryEntity category;

    public static SpendEntity fromJson(SpendJson json) {
        SpendEntity entity = new SpendEntity();

        entity.setId(json.id());
        entity.setUsername(json.username());
        entity.setCurrency(json.currency());
        entity.setSpendDate(new Date(json.spendDate().getTime()));
        entity.setAmount(json.amount());
        entity.setDescription(json.description());
        entity.setCategory(CategoryEntity.fromJson(json.category()));

        return entity;
    }

}
