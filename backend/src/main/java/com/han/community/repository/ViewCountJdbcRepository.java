package com.han.community.repository;

import com.han.community.service.BufferedViewCountIncrementer;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ViewCountJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void flushViewCountsBatch(List<BufferedViewCountIncrementer.ViewCountDelta> chunk) {

        jdbcTemplate.batchUpdate(
                "UPDATE post SET view_count = view_count + ? WHERE id = ?",
                chunk,
                chunk.size(),
                (ps, viewCount) -> {
                    ps.setLong(1, viewCount.delta());
                    ps.setLong(2, viewCount.id());
                }
        );
    }

}
