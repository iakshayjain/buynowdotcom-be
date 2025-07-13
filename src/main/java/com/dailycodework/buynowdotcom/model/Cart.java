package com.dailycodework.buynowdotcom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal totalAmount;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cart", orphanRemoval = true)
    private Set<CartItem> items = new HashSet<>();

    public Cart(BigDecimal totalAmount, User user) {
        this.totalAmount = totalAmount;
        this.user = user;
    }

    public void removeItem(CartItem cartItem) {
        this.getItems().remove(cartItem);
        cartItem.setCart(this);
        updateTotalAmount();
    }

    public void addItem(CartItem cartItem) {
        this.getItems().add(cartItem);
        cartItem.setCart(this);
        updateTotalAmount();
    }

    public void clearCartItems() {
        this.items.clear();
        updateTotalAmount();
    }

    public void updateTotalAmount() {
        this.totalAmount = this.items
                .stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
