package ru.mpei.fqw.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ComtradeServiceTest {
    @Autowired
    private ComtradeService service;

    @Test
    void comtradeToJSON() {
    service.comtradeToJSON();
    }
}
