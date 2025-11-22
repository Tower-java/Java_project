package towergame.util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire audio centralisé pour la musique de fond du jeu.
 * Gère la lecture en boucle des fichiers MP3 à différentes étapes du jeu.
 */
public class AudioManager {
    private static AudioManager instance;
    private final Map<String, MediaPlayer> players = new HashMap<>();
    private String currentTrack;
    private static final double VOLUME = 0.5;

    // Identifiants des pistes musicales
    public static final String MENU_MUSIC = "menu";
    public static final String FIRE_BOSS_MUSIC = "fireBoss";
    public static final String WATER_BOSS_MUSIC = "waterBoss";
    public static final String VICTORY_MUSIC = "victory";
    public static final String DEFEAT_MUSIC = "defeat";

    private AudioManager() {
        initializeAudio();
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    private void initializeAudio() {
        try {
            // Charger toutes les pistes musicales
            loadTrack(MENU_MUSIC, "/music/Gilles Stella (Crossed - Chroma) - Libre de droit (version longue).mp3");
            loadTrack(FIRE_BOSS_MUSIC, "/music/Charly & Lulu - Le Feu Ça Brûle (Clip officiel HD).mp3");
            loadTrack(WATER_BOSS_MUSIC, "/music/Jai mangé de la neige et jai plein dénergie - 1 HEURE.mp3");
            // La même musique pour victoire et défaite
            loadTrack(VICTORY_MUSIC, "/music/Gilles Stella (Crossed - Chroma) - Libre de droit (version longue).mp3");
            loadTrack(DEFEAT_MUSIC, "/music/Gilles Stella (Crossed - Chroma) - Libre de droit (version longue).mp3");
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des pistes audio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadTrack(String trackId, String resourcePath) {
        try {
            String fullPath = getClass().getResource(resourcePath).toString();
            Media media = new Media(fullPath);
            MediaPlayer player = new MediaPlayer(media);
            player.setVolume(VOLUME);
            player.setCycleCount(MediaPlayer.INDEFINITE);
            players.put(trackId, player);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de " + resourcePath + ": " + e.getMessage());
        }
    }

    /**
     * Lance la lecture d'une piste musicale en boucle.
     * Arrête la piste actuelle si une autre est en cours.
     */
    public void playTrack(String trackId) {
        if (trackId.equals(currentTrack)) {
            return; // Déjà en cours
        }

        // Arrêter la piste actuelle
        stopCurrentTrack();

        // Lancer la nouvelle piste
        MediaPlayer player = players.get(trackId);
        if (player != null) {
            player.play();
            currentTrack = trackId;
        }
    }

    /**
     * Arrête la piste actuelle
     */
    public void stopCurrentTrack() {
        if (currentTrack != null) {
            MediaPlayer player = players.get(currentTrack);
            if (player != null) {
                player.stop();
            }
            currentTrack = null;
        }
    }

    /**
     * Arrête toutes les pistes
     */
    public void stopAll() {
        for (MediaPlayer player : players.values()) {
            if (player != null) {
                player.stop();
            }
        }
        currentTrack = null;
    }

    /**
     * Change le volume global (0.0 à 1.0)
     */
    public void setVolume(double volume) {
        for (MediaPlayer player : players.values()) {
            if (player != null) {
                player.setVolume(volume);
            }
        }
    }

    /**
     * Retourne le volume actuel
     */
    public double getVolume() {
        if (currentTrack != null) {
            MediaPlayer player = players.get(currentTrack);
            if (player != null) {
                return player.getVolume();
            }
        }
        return VOLUME;
    }
}
