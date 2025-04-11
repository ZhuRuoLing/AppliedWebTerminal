package icu.takeneko.appwebterminal.config;

import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.UpdateRestrictions;
import icu.takeneko.appwebterminal.AppWebTerminal;

@Config(id = AppWebTerminal.MOD_ID)
public class AppWebTerminalConfig {
   @Configurable
   @Configurable.Comment({"Http Server port for ME Web Terminal."})
   @Configurable.Range(
      min = 1L,
      max = 65536L
   )
   @Configurable.UpdateRestriction(UpdateRestrictions.MAIN_MENU)
   public int httpPort = 11451;

   @Configurable
   @Configurable.Comment({"Web Terminal Title"})
   public String frontendTitle = "Applied Web Terminal";

   @Configurable
   @Configurable.Comment({"Websocket url for frontend to connect", "Use \"~\" for auto detecting"})
   public String backendWebsocketEndpoint = "~";

   @SuppressWarnings("unused")
   public int getHttpPort() {
      return httpPort;
   }

   @SuppressWarnings("unused")
   public String getFrontendTitle() {
      return frontendTitle;
   }

   @SuppressWarnings("unused")
   public String getBackendWebsocketEndpoint() {
      return backendWebsocketEndpoint;
   }
}
