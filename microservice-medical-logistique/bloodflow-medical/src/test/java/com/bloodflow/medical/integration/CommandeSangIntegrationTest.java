package com.bloodflow.medical.integration;
import com.bloodflow.medical.entity.CommandeSang;
import com.bloodflow.medical.repository.CommandeSangRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CommandeSangIntegrationTest {
    @Autowired private CommandeSangRepository commandeSangRepository;
    @Test void createCommande() {
        CommandeSang c = new CommandeSang();
        c.setNumeroCommande("CMD-INT-001"); c.setGroupeSanguin("O+");
        c.setTypeProduit("Sang total"); c.setQuantite(3); c.setStatut("EN_ATTENTE");
        CommandeSang saved = commandeSangRepository.save(c);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getGroupeSanguin()).isEqualTo("O+");
        assertThat(commandeSangRepository.findByStatut("EN_ATTENTE")).isNotEmpty();
    }
}
