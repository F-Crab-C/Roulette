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
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);

        int entityId = ENTITY_ID++;
        entityIds.put(player.getUniqueId(), entityId);

        packet.getIntegers()
                .write(0, entityId)
                .write(1, EntityType.ARMOR_STAND.getTypeId());

        packet.getDoubles()
                .write(0, location.getX())
                .write(1, location.getY())
                .write(2, location.getZ());

        PacketContainer metadata = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        metadata.getIntegers().write(0, entityId);

        WrappedDataWatcher watcher = new WrappedDataWatcher();
        watcher.setObject(0, (byte) 0x20);
        watcher.setObject(2, text);
        watcher.setObject(3, (byte) 1);

        metadata.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());

        try {
            protocolManager.sendServerPacket(player, packet);
            protocolManager.sendServerPacket(player, metadata);
        } catch (InvocationTargetException e) {
            // 단순 printStackTrace() 대신 더 구체적인 에러 처리
            Bukkit.getLogger().warning("홀로그램 생성 중 오류 발생: " + e.getCause().getMessage());
            // 또는 플러그인 로거 사용
            plugin.getLogger().warning("홀로그램 생성 실패: " + player.getName());
        }
    }

    public void removeHologram(Player player) {
        UUID playerId = player.getUniqueId();
        if (!entityIds.containsKey(playerId)) return;

        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        packet.getIntegerArrays().write(0, new int[]{entityIds.get(playerId)});

        try {
            protocolManager.sendServerPacket(player, packet);
            protocolManager.sendServerPacket(player, metadata);
        } catch (InvocationTargetException e) {
            // 단순 printStackTrace() 대신 더 구체적인 에러 처리
            Bukkit.getLogger().warning("홀로그램 생성 중 오류 발생: " + e.getCause().getMessage());
            // 또는 플러그인 로거 사용
            plugin.getLogger().warning("홀로그램 생성 실패: " + player.getName());
        }

        entityIds.remove(playerId);
    }
}