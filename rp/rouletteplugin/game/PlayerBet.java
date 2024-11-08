package rp.rouletteplugin.game;

import java.util.UUID;

public class PlayerBet {
    private final UUID playerUUID;
    private double amount;
    private RouletteColor color;

    public PlayerBet(UUID playerUUID, double amount, RouletteColor color) {
        this.playerUUID = playerUUID;
        this.amount = amount;
        this.color = color;
    }

    // Getter 메서드들
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public double getAmount() {
        return amount;
    }

    public RouletteColor getColor() {
        return color;
    }

    // Setter 메서드들
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setColor(RouletteColor color) {
        this.color = color;
    }
}