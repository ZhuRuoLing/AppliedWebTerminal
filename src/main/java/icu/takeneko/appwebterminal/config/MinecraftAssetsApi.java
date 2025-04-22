package icu.takeneko.appwebterminal.config;

import java.util.function.Function;

public enum MinecraftAssetsApi {
    MOJANG(
        "https://piston-meta.mojang.com",
        Function.identity(),
        Function.identity(),
        Function.identity()
    ),
    BMCLAPI(
        "https://bmclapi2.bangbang93.com",
        s -> s.replace("https://piston-meta.mojang.com", "https://piston-meta.bmclapi.com"),
        s -> s.replace("https://piston-meta.mojang.com", "https://bmclapi2.bangbang93.com"),
        s -> s.replace("https://resources.download.minecraft.net", "https://bmclapi2.bangbang93.com/assets")
    );

    private final String endpoint;
    private final Function<String, String> urlManifestReplacer;
    private final Function<String, String> urlIndexReplacer;
    private final Function<String, String> urlAssetsReplacer;

    MinecraftAssetsApi(String endpoint, Function<String, String> urlManifestReplacer, Function<String, String> urlIndexReplacer, Function<String, String> urlAssetsReplacer) {
        this.endpoint = endpoint;
        this.urlManifestReplacer = urlManifestReplacer;
        this.urlIndexReplacer = urlIndexReplacer;
        this.urlAssetsReplacer = urlAssetsReplacer;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public Function<String, String> getUrlManifestReplacer() {
        return urlManifestReplacer;
    }

    public Function<String, String> getUrlIndexReplacer() {
        return urlIndexReplacer;
    }

    public Function<String, String> getUrlAssetsReplacer() {
        return urlAssetsReplacer;
    }

}
