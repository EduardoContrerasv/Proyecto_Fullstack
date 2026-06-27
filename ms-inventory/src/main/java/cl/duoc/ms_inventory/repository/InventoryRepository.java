package cl.duoc.ms_inventory.repository;

import cl.duoc.ms_inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // <-- CRITICAL IMPORT!

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
        List<Inventory> findByUserId(Long userId);
        Optional<Inventory> findByUserIdAndItemId(Long userId, Long itemId);
        int countByUserIdAndItemType(Long userId, String itemType);
        
        @Query("SELECT COALESCE(SUM(i.quantity), 0) FROM Inventory i WHERE i.userId = :userId AND i.itemType IN :itemTypes")
        int countByUserIdAndItemTypeIn(@Param("userId") Long userId, @Param("itemTypes") List<String> itemTypes);
}