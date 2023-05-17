package ru.mpei.fqw.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.mpei.fqw.model.FaultCurrentModel;
import ru.mpei.fqw.repository.RepositoryIml;

import java.util.List;

@SpringBootTest
class ComtradeServiceTest {
    @Autowired
    private ComtradeService service;
    @Autowired
    private RepositoryIml repository;
    @Test
    void comtradeToJSON() {
    service.comtradeToJSON();
        List<FaultCurrentModel> faultCurrentInfo = repository.getFaultCurrentInfo();
        System.out.println();
    }
}
