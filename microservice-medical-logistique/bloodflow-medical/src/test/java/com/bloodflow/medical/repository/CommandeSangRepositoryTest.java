package com.bloodflow.medical.repository;
import com.bloodflow.medical.entity.CommandeSang;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
@DataJpaTest
@ActiveProfiles("test")
class CommandeSangRepositoryTest {
    @Autowired private CommandeSangRepository commandeSangRepository;

    @Test void save_etFindByStatut() {
        CommandeSang c = new CommandeSang();
        c.setNumeroCommande("CMD-001"); c.setGroupeSanguin("A+");
        c.setTypeProduit("Sang total"); c.setQuantite(2); c.setStatut("EN_ATTENTE");
        commandeSangRepository.save(c);
        List<CommandeSang> result = commandeSangRepository.findByStatut("EN_ATTENTE");
        assertThat(result).isNotEmpty();
    }

    @Test void findByNumeroCommande_trouveBien() {
        CommandeSang c = new CommandeSang();
        c.setNumeroCommande("CMD-XYZ"); c.setGroupeSanguin("B+");
        c.setTypeProduit("Plasma"); c.setQuantite(1); c.setStatut("EN_ATTENTE");
        commandeSangRepository.save(c);
        assertThat(commandeSangRepository.findByNumeroCommande("CMD-XYZ")).isPresent();
    }
}
