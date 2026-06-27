package cl.duoc.ms_shop.model;

import cl.duoc.ms_shop.enums.CurrencyType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "shop_catalog")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ShopItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "item_id", nullable = false)
    private Long itemId;
    @Column(name = "price",nullable = false)
    private int price;
    @Enumerated(EnumType.STRING)
    @Column(name = "currency_type",nullable = false)
    private CurrencyType currencyType;

}
