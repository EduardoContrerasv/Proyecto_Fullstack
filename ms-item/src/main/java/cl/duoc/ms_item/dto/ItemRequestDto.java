package cl.duoc.ms_item.dto;

import cl.duoc.ms_item.enums.ItemType;
import cl.duoc.ms_item.enums.Rarity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {

    @NotBlank(message = "Debe tener un nombre")
    private String name;
    @NotBlank(message = "Debe tener descripcion")
    private String description;
    @Positive(message = "Precio debe ser mas que 0")
    private int price;
    @Min(value = 1, message = "Nivel requerido debe ser mayor o igual a 1")
    private int requiredLevel;
    @NotNull(message = "Debe tener una categoria (Weapon,Armor,Consumable,Currency,Cosmetic)")
    private ItemType itemType;
    @NotNull(message = "Debe tener una rareza (Common,Uncommon,Rare,Epic,Legendary)")
    private Rarity rarity;
}
