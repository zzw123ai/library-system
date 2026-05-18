package com.library.system.performance;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
/**
 * 计划书非功能指标：核心查询接口响应时间 &lt; 1s（本地 H2 测试环境）。
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("API 性能验收（查询 < 1s）")
class ApiPerformanceTest {

    /** 计划书要求：查询 1s；测试环境留 200ms 余量 */
    static final long QUERY_MAX_MS = 1000L;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("图书列表 /api/book/list")
    void bookListUnderOneSecond() throws Exception {
        assertQueryUnderLimit("/api/book/list", null);
    }

    @Test
    @DisplayName("图书搜索 /api/book/search?keyword=红")
    void bookSearchUnderOneSecond() throws Exception {
        assertQueryUnderLimit("/api/book/search", "keyword=红");
    }

    @Test
    @DisplayName("用户列表 /api/user/list")
    void userListUnderOneSecond() throws Exception {
        assertQueryUnderLimit("/api/user/list", null);
    }

    @Test
    @DisplayName("借阅列表 /api/borrow/list")
    void borrowListUnderOneSecond() throws Exception {
        assertQueryUnderLimit("/api/borrow/list", null);
    }

    @Test
    @DisplayName("可借图书 /api/book/available")
    void availableBooksUnderOneSecond() throws Exception {
        assertQueryUnderLimit("/api/book/available", null);
    }

    @Test
    @DisplayName("逾期提醒 /api/borrow/overdue/reminder")
    void overdueReminderUnderOneSecond() throws Exception {
        assertQueryUnderLimit("/api/borrow/overdue/reminder", null);
    }

    private void assertQueryUnderLimit(String path, String query) throws Exception {
        long start = System.nanoTime();
        var request = get(path + (query != null ? "?" + query : ""));
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        long elapsedMs = (System.nanoTime() - start) / 1_000_000L;
        assertTrue(
                elapsedMs < QUERY_MAX_MS,
                path + " 耗时 " + elapsedMs + "ms，超过阈值 " + QUERY_MAX_MS + "ms");
    }
}
