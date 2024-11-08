package rp.rouletteplugin.hologramsystem;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;

public class HologramManager {
    private final ProtocolManager protocolManager;
    private final Map<UUID, Integer> entityIds;
    private static int ENTITY_ID = 1000000;

    public HologramManager() {
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.entityIds = new HashMap<>();
    }

    public void createHologram(Player player, Location location, String text) {
        try {
            int entityId = ENTITY_ID++;
            entityIds.put(player.getUniqueId(), entityId);

            protocolManager.sendServerPacket(player, createSpawnPacket(entityId, location));
            protocolManager.sendServerPacket(player, createMetadataPacket(entityId, text));
        } catch (Exception e) {
            Bukkit.getLogger().warning("홀로그램 생성 실패: " + e.getMessage());
        }
    }

    public void removeHologram(Player player) {
        UUID playerId = player.getUniqueId();
        if (!entityIds.containsKey(playerId)) return;

        try {
            PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
            packet.getIntLists().write(0, List.of(entityIds.get(playerId)));

            protocolManager.sendServerPacket(player, packet);
            entityIds.remove(playerId);
        } catch (Exception e) {
            Bukkit.getLogger().warning("홀로그램 제거 실패: " + e.getMessage());
        }
    }

    public void updateHologramLocation(Player player, Location newLocation) {
        UUID playerId = player.getUniqueId();
        if (!entityIds.containsKey(playerId)) return;

        try {
            PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
            packet.getIntegers().write(0, entityIds.get(playerId));
            packet.getDoubles()
                    .write(0, newLocation.getX())
                    .write(1, newLocation.getY())
                    .write(2, newLocation.getZ());

            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            Bukkit.getLogger().warning("홀로그램 위치 업데이트 실패: " + e.getMessage());
        }
    }

    public void updateHologramText(Player player, String newText) {
        UUID playerId = player.getUniqueId();
        if (!entityIds.containsKey(playerId)) return;

        try {
            protocolManager.sendServerPacket(player, createMetadataPacket(entityIds.get(playerId), newText));
        } catch (Exception e) {
            Bukkit.getLogger().warning("홀로그램 텍스트 업데이트 실패: " + e.getMessage());
        }
    }

    private PacketContainer createSpawnPacket(int entityId, Location location) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
        packet.getIntegers().write(0, entityId);
        packet.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
        packet.getDoubles()
                .write(0, location.getX())
                .write(1, location.getY())
                .write(2, location.getZ());
        return packet;
    }

    private PacketContainer createMetadataPacket(int entityId, String text) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet.getIntegers().write(0, entityId);

        WrappedDataWatcher watcher = new WrappedDataWatcher();
        watcher.setObject(0, (byte) 0x20); // Invisible
        watcher.setObject(2, text); // Custom name
        watcher.setObject(3, true); // Custom name visible
        watcher.setObject(5, true); // No gravity

        packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
        return packet;
    }
}