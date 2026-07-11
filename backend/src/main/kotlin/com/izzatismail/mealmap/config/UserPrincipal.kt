package com.izzatismail.mealmap.config

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

class UserPrincipal(
    username: String,
    password: String,
    val userId: Long,
    authorities: Collection<GrantedAuthority> = emptyList(),
) : User(username, password, authorities)