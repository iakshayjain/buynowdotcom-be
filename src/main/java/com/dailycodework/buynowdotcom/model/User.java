package com.dailycodework.buynowdotcom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_list")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @NaturalId
    private String email;
    private String password;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private Cart cart;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER,
                cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "user_roles",
                joinColumns = @JoinColumn(name = "user_id",
                                        referencedColumnName = "id"),
                inverseJoinColumns = @JoinColumn(name = "role_id",
                                                referencedColumnName = "id"))
    private Collection<Role> roles = new HashSet<>();
}
