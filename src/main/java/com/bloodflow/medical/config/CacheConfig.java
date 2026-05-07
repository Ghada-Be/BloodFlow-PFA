package com.bloodflow.medical.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * =============================================
 * CONFIGURATION DU CACHE - CacheConfig
 * =============================================
 * Cette classe active et configure le cache dans l'application.
 *
 * Pourquoi un cache ?
 *   - Éviter des appels répétés à la base de données pour des données
 *     qui ne changent pas souvent (ex: liste des stocks, poches disponibles)
 *   - Améliorer les performances et réduire la charge sur la DB
 *
 * On utilise Caffeine : une librairie de cache en mémoire (dans la JVM),
 * rapide et facile à configurer.
 *
 * @EnableCaching : active les annotations @Cacheable, @CacheEvict, @CachePut
 *                  dans tous les services de l'application
 * =============================================
 */
@Configuration
@EnableCaching  // <-- Active le système de cache Spring dans toute l'application
public class CacheConfig {

    /**
     * Définit le CacheManager utilisé par Spring.
     * Spring utilise ce bean pour créer et gérer les caches.
     *
     * Configuration Caffeine :
     *   - maximumSize(500)         : max 500 entrées par cache (évite les débordements mémoire)
     *   - expireAfterWrite(10 min) : chaque entrée expire 10 minutes après son écriture
     *                                (après 10 min, la prochaine requête ira chercher en DB et re-remplira le cache)
     *   - recordStats()            : enregistre des statistiques (hits, misses) utiles pour le debug
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // Configuration globale du cache Caffeine
        cacheManager.setCaffeine(
            Caffeine.newBuilder()
                .maximumSize(500)                     // max 500 objets en mémoire par cache
                .expireAfterWrite(10, TimeUnit.MINUTES) // expiration 10 min après écriture
                .recordStats()                         // active les stats (optionnel, utile en debug)
        );

        // Déclaration des noms de caches utilisés dans les services
        // Chaque nom correspond au paramètre value= dans @Cacheable / @CacheEvict
        cacheManager.setCacheNames(java.util.List.of(
            "stocks",          // utilisé dans StockServiceImpl
            "poches-sang",     // utilisé dans PocheSangServiceImpl
            "analyses",        // utilisé dans AnalyseSangServiceImpl
            "commandes",       // utilisé dans CommandeSangServiceImpl
            "prescriptions",   // utilisé dans PrescriptionServiceImpl
            "dossiers",        // utilisé dans DossierMedicalServiceImpl
            "livraisons",      // utilisé dans LivraisonServiceImpl
            "resultats"        // utilisé dans ResultatBiologiqueServiceImpl
        ));

        return cacheManager;
    }
}
