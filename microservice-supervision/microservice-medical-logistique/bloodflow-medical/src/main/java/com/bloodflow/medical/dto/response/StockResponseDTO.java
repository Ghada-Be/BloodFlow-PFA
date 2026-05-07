package com.bloodflow.medical.dto.response;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class StockResponseDTO {
    private Long id;
    private String centreSang;
    private String groupeSanguin;
    private String typeProduit;
    private Integer quantiteDisponible;
    private Integer seuilAlerte;
    private Boolean enAlerte;
    private LocalDateTime dateMiseAJour;
}
