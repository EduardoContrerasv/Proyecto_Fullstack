package cl.duoc.ms_shop.repository;

import cl.duoc.ms_shop.model.ShopItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopItemRepository extends JpaRepository<ShopItem, Long> {
}
