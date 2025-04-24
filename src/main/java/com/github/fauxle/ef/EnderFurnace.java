package com.github.fauxle.ef;

import java.io.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Data;
import lombok.NonNull;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Furnace;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

@Data
@NonNull
public class EnderFurnace {

    private static final int META_DATA_VERSION = 1;

    private static final NamespacedKey META_KEY =
            new NamespacedKey(EnderFurnacePlugin.getPlugin(EnderFurnacePlugin.class), "efMeta");

    private final Furnace furnace;
    private UUID ownerUniqueId;
    private Set<UUID> canAccessUniqueIds = new HashSet<>();
    private Instant created = Instant.now();

    public EnderFurnace(Furnace furnace) {
        this.furnace = furnace;
    }

    public void load() throws IOException {
        PersistentDataContainer container = furnace.getPersistentDataContainer();
        byte[] data = container.get(META_KEY, PersistentDataType.BYTE_ARRAY);
        EnderFurnace read = EnderFurnace.deserialize(furnace, data);
        ownerUniqueId = read.ownerUniqueId;
        canAccessUniqueIds = read.canAccessUniqueIds;
        created = read.created;
    }

    public void save() throws IOException {
        PersistentDataContainer container = furnace.getPersistentDataContainer();
        container.set(META_KEY, PersistentDataType.BYTE_ARRAY, serialize());
        furnace.update(true, false);
    }

    private byte[] serialize() throws IOException {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(bytesOut)) {
            out.write(META_DATA_VERSION);
            out.writeUTF(ownerUniqueId.toString());
            out.writeInt(canAccessUniqueIds.size());
            for (UUID uuid : canAccessUniqueIds) out.writeUTF(uuid.toString());
            out.writeLong(created.toEpochMilli());
            out.flush();
            return bytesOut.toByteArray();
        }
    }

    private static EnderFurnace deserialize(Furnace furnace, byte[] data) throws IOException {
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(data))) {
            int version = in.read();
            if (version != META_DATA_VERSION)
                throw new IOException(
                        "Version " + version + " not supported by this version of the plugin");
            UUID ownerUniqueId = UUID.fromString(in.readUTF());
            int accessSetSize = in.readInt();
            Set<UUID> canAccessUniqueIds = new HashSet<>();
            for (int i = 0; i < accessSetSize; i++)
                canAccessUniqueIds.add(UUID.fromString(in.readUTF()));
            Instant created = Instant.ofEpochMilli(in.readLong());
            EnderFurnace ef = new EnderFurnace(furnace);
            ef.ownerUniqueId = ownerUniqueId;
            ef.canAccessUniqueIds = canAccessUniqueIds;
            ef.created = created;
            return ef;
        }
    }
}
