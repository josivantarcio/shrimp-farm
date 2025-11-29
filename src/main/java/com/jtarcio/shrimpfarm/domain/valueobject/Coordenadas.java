package com.jtarcio.shrimpfarm.domain.valueobject;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coordenadas {

    private BigDecimal latitude;
    private BigDecimal longitude;

    // Validações
    public boolean isValida() {
        return latitude != null && longitude != null
                && latitude.compareTo(BigDecimal.valueOf(-90)) >= 0
                && latitude.compareTo(BigDecimal.valueOf(90)) <= 0
                && longitude.compareTo(BigDecimal.valueOf(-180)) >= 0
                && longitude.compareTo(BigDecimal.valueOf(180)) <= 0;
    }

    // Formatar para Google Maps
    public String formatarParaMaps() {
        if (!isValida()) {
            throw new IllegalStateException("Coordenadas inválidas");
        }
        return String.format("%s,%s", latitude.toString(), longitude.toString());
    }

    // URL do Google Maps
    public String getUrlGoogleMaps() {
        return String.format("https://www.google.com/maps?q=%s", formatarParaMaps());
    }

    @Override
    public String toString() {
        return String.format("Lat: %s, Long: %s", latitude, longitude);
    }
}
