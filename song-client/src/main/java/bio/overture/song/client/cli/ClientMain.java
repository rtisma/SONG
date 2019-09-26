/*
 * Copyright (c) 2018. Ontario Institute for Cancer Research
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package bio.overture.song.client.cli;

import static bio.overture.song.core.exceptions.ServerErrors.UNAUTHORIZED_TOKEN;
import static bio.overture.song.core.exceptions.ServerErrors.UNKNOWN_ERROR;
import static bio.overture.song.core.exceptions.SongError.createSongError;

import bio.overture.song.client.command.ConfigCommand;
import bio.overture.song.client.command.ExportCommand;
import bio.overture.song.client.command.FileUpdateCommand;
import bio.overture.song.client.command.GetAnalysisTypeCommand;
import bio.overture.song.client.command.ListAnalysisTypesCommand;
import bio.overture.song.client.command.ManifestCommand;
import bio.overture.song.client.command.PingCommand;
import bio.overture.song.client.command.PublishCommand;
import bio.overture.song.client.command.RegisterAnalysisTypeCommand;
import bio.overture.song.client.command.SearchCommand;
import bio.overture.song.client.command.SubmitCommand;
import bio.overture.song.client.command.SuppressCommand;
import bio.overture.song.client.command.UnpublishCommand;
import bio.overture.song.client.config.Config;
import bio.overture.song.client.config.CustomRestClientConfig;
import bio.overture.song.client.errors.ErrorStatusHeader;
import bio.overture.song.core.exceptions.ServerException;
import bio.overture.song.core.exceptions.SongError;
import bio.overture.song.sdk.ManifestClient;
import bio.overture.song.sdk.SongApi;
import bio.overture.song.sdk.Toolbox;
import java.io.IOException;
import java.net.HttpRetryException;
import java.util.function.Consumer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.web.client.RestClientException;

@Slf4j
public class ClientMain {

  public static final int FAILURE_STATUS = 1;
  public static final int SUCCESS_STATUS = 0;

  public static Consumer<Integer> exit = System::exit;
  private CommandParser dispatcher;
  private ErrorStatusHeader errorStatusHeader;
  private SongApi songApi;
  private CustomRestClientConfig restClientConfig;

  public ClientMain(
      @NonNull CustomRestClientConfig restClientConfig,
      @NonNull SongApi songApi,
      @NonNull ManifestClient manifestClient) {
    this.restClientConfig = restClientConfig;
    this.songApi = songApi;
    val options = new Options();

    val builder = new CommandParserBuilder(restClientConfig.getProgramName(), options);
    builder.register("config", new ConfigCommand(restClientConfig));
    builder.register("submit", new SubmitCommand(restClientConfig, songApi));
    builder.register("ping", new PingCommand(songApi));
    builder.register("get-analysis-type", new GetAnalysisTypeCommand(songApi));
    builder.register("list-analysis-types", new ListAnalysisTypesCommand(songApi));
    builder.register("register-analysis-type", new RegisterAnalysisTypeCommand(songApi));
    builder.register("search", new SearchCommand(restClientConfig, songApi));
    builder.register("manifest", new ManifestCommand(restClientConfig, manifestClient));
    builder.register("publish", new PublishCommand(restClientConfig, songApi));
    builder.register("unpublish", new UnpublishCommand(restClientConfig, songApi));
    builder.register("suppress", new SuppressCommand(restClientConfig, songApi));
    builder.register("export", new ExportCommand(songApi));
    builder.register("update-file", new FileUpdateCommand(restClientConfig, songApi));
    this.dispatcher = builder.build();
    this.errorStatusHeader = new ErrorStatusHeader(restClientConfig);
  }

  public void run(String... args) {
    val command = dispatcher.parse(args);
    int exitCode = FAILURE_STATUS;
    try {
      command.run();
      exitCode = SUCCESS_STATUS;
    } catch (RestClientException e) {
      val isAlive = songApi.isAlive();
      SongError songError;
      if (isAlive) {
        val cause = e.getCause();
        if (cause instanceof HttpRetryException) {
          val httpRetryException = (HttpRetryException) cause;
          if (httpRetryException.responseCode() == UNAUTHORIZED_TOKEN.getHttpStatus().value()) {
            songError = createSongError(UNAUTHORIZED_TOKEN, "Invalid token");
          } else {
            songError =
                createSongError(
                    UNKNOWN_ERROR,
                    "Unknown error with ResponseCode [%s] -- Reason: %s, Message: %s",
                    httpRetryException.responseCode(),
                    httpRetryException.getReason(),
                    httpRetryException.getMessage());
          }
        } else {
          songError = createSongError(UNKNOWN_ERROR, "Unknown error: %s", e.getMessage());
        }
        command.err(errorStatusHeader.getSongClientErrorOutput(songError));
      } else {
        command.err(
            errorStatusHeader.createMessage(
                "The SONG server may not be running on '%s'", restClientConfig.getServerUrl()));
      }
    } catch (ServerException ex) {
      val songError = ex.getSongError();
      command.err(errorStatusHeader.getSongServerErrorOutput(songError));
    } catch (IOException e) {
      command.err("IO Error: %s", e.getMessage());
    } catch (Throwable e) {
      command.err("Unknown error: %s", e.getMessage());
    } finally {
      command.report();
      exit(exitCode);
    }
  }

  public static void exit(int status) {
    exit.accept(status);
  }

  public static ClientMain createClientMain(@NonNull Config config) {
    val toolbox = Toolbox.createToolbox(config.getClient(), config.getRetry());
    return new ClientMain(config.getClient(), toolbox.getSongApi(), toolbox.getManifestClient());
  }
}
