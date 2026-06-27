package cl.duoc.ms_currency.repository;

import cl.duoc.ms_currency.enums.CurrencyType;
import cl.duoc.ms_currency.model.UserCurrency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<UserCurrency, Long> {
    Optional<UserCurrency> findByUserIdAndCurrencyType(Long userId, CurrencyType currencytype);
    List<UserCurrency> findByUserId(Long userId);

}
