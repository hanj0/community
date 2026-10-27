package com.han.community.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ViewCountJdbcRepository {

    private final JdbcTemplate jdbcTemplate;


}
