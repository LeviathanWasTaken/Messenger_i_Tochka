package com.leviathan.messenger_i_tochka.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "jwt_blacklist")
public class JwtBlacklisted {
    @Id
    @Column(name = "token")
    private String token;
}
