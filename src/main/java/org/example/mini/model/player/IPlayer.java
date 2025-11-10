package org.example.mini.model.player;

import org.example.mini.model.card.Card;
import java.util.List;

/**
 * Defines the contract for any player in the game.
 * Both human and machine players must implement these methods.
 */
public interface IPlayer {

    /** Nombre del jugador */
    String getName();

    /** Mano actual del jugador */
    List<Card> getHand();

    /** Indica si el jugador es humano */
    boolean isHuman();

    /** Indica si el jugador sigue activo en la partida */
    boolean isActive();

    /** Cambia el estado activo/eliminado del jugador */
    void setActive(boolean active);

    /** Agrega una carta a la mano */
    void addCard(Card card);

    /** 🔹 NUEVO: elimina una carta de la mano (usado al jugarla) */
    void removeCard(Card card);

    /** El jugador elige y juega una carta (IA o humano) */
    Card playCard(int tableSum);

    /** Verifica si el jugador tiene al menos una carta jugable */
    boolean hasPlayableCards(int tableSum);
}
