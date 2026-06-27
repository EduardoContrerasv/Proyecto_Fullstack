package cl.duoc.ms_currency.model;

import cl.duoc.ms_currency.enums.CurrencyType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_currency")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserCurrency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Enumerated(EnumType.STRING)
    @Column(name = "currency_type", nullable = false)
    private CurrencyType currencyType;
    @Column(name = "amount", nullable = false)
    private int amount = 0;
}
