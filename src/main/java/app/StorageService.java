package app;

import com.google.gson.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.time.LocalDate;

public class StorageService {
    private final Path dir;
    private final Gson gson;

    public StorageService(String folder) {
        this.dir = Path.of(folder);
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать папку data");
        }
    }

    public Wallet loadWallet(String login) {
        Path file = dir.resolve("wallet_" + login + ".json");
        if (!Files.exists(file)) return new Wallet();
        try {
            String json = Files.readString(file);
            Wallet w = gson.fromJson(json, Wallet.class);
            return w == null ? new Wallet() : w;
        } catch (Exception e) {
            return new Wallet();
        }
    }

    public void saveWallet(String login, Wallet wallet) {
        Path file = dir.resolve("wallet_" + login + ".json");
        try {
            Files.writeString(file, gson.toJson(wallet),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить кошелёк");
        }
    }

    public void exportWalletJson(Path file, Wallet wallet) throws IOException {
        Files.writeString(file, gson.toJson(wallet),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public Wallet importWalletJson(Path file) throws IOException {
        String json = Files.readString(file);
        Wallet w = gson.fromJson(json, Wallet.class);
        return w == null ? new Wallet() : w;
    }

    static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        @Override
        public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            return LocalDate.parse(json.getAsString());
        }
    }
}