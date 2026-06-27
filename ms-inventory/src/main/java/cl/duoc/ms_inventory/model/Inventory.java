package cl.duoc.ms_inventory.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long  userId;
    @Column(name = "item_id", nullable = false)
    private Long itemId;
    @Column(name = "quantity",nullable = false)
    private int quantity;
    @Column(name = "item_type", nullable = false)
    private String itemType;

}
