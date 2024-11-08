package rp.rouletteplugin.hologramsystem;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HologramManager {
    private final ProtocolManager protocolManager;
    private final Map<UUID, Integer> entityIds;
    private static int ENTITY_ID = 1000000;

    public HologramManager() {
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.entityIds = new HashMap<>();
    }

    public void createHologram(Player player, Location location, String text) {
        // ArmorStand 패킷 생성
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);

        int entityId = ENTITY_ID++;
        entityIds.put(player.getUniqueId(), entityId);

        packet.getIntegers()
                .write(0, entityId) // Entity ID
                .write(1, EntityType.ARMOR_STAND.getTypeId()); // Entity Type

        packet.getDoubles()
                .write(0, location.getX())
                .write(1, location.getY())
                .write(2, location.getZ());

        // 메타데이터 패킷 생성
        PacketContainer metadata = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        metadata.getIntegers().write(0, entityId);

        WrappedDataWatcher watcher = new WrappedDataWatcher();
        watcher.setObject(0, (byte) 0x20); // Invisible
        watcher.setObject(2, text); // Custom name
        watcher.setObject(3, (byte) 1); // Custom name visible

        metadata.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());

        try {
            protocolManager.sendServerPacket(player, packet);
            protocolManager.sendServerPacket(player, metadata);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void removeHologram(Player player) {
        UUID playerId = player.getUniqueId();
        if (!entityIds.containsKey(playerId)) return;

        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        packet.getIntegerArrays().write(0, new int[]{entityIds.get(playerId)});

        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        entityIds.remove(playerId);
    }

    public void updateHologramText(Player player, String newText) {
        UUID playerId = player.getUniqueId();
        if (!entityIds.containsKey(playerId)) return;

        PacketContainer metadata = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        metadata.getIntegers().write(0, entityIds.get(playerId));

        WrappedDataWatcher watcher = new WrappedDataWatcher();
        watcher.setObject(2, newText);

        metadata.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());

        try {
            protocolManager.sendServerPacket(player, metadata);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}