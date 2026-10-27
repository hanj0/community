package com.han.community.integration.duplicate;

import com.han.community.common.IntegrationTestSupport;
import org.junit.jupiter.api.Test;

/**
 * 게시글 조회수 증가 중복처리 테스트
 * 동일한 id일때, 조회수가 1번 증가했다면 24시간동안 오르지않음
 * 비로그인일때, IP 또는 쿠키 둘 중 하나라도 일치하면 동일사용자로 판단
 */
public class ViewCountDeduplicationTest extends IntegrationTestSupport {


    @Test
    void 같은_사용자가_같은_게시글을_재조회해도_조회수가_증가하지않는다() {

    }

    @Test
    void 다른_사용자가_같은_게시글을_조회하면_조회수는_각각_증가한다() {

    }

    @Test
    void 중복조회방지기간이_지나면_조회수가_다시_증가한다() {

    }

    @Test
    void 비로그인_사용자는_IP_또는_임시발행쿠키로_판단한다() {

    }

}
