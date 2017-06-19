package org.icgc.dcc.song.server.exceptions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.val;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.List;

import static org.icgc.dcc.common.core.json.JsonNodeBuilders.array;
import static org.icgc.dcc.common.core.json.JsonNodeBuilders.object;
import static org.icgc.dcc.common.core.util.stream.Collectors.toImmutableList;
import static org.icgc.dcc.common.core.util.stream.Streams.stream;

@Data
public class Error {
  private List<StackTraceElement> stackTrace;
  private String errorId;
  private HttpStatus httpStatus;
  private String message;
  private String requestUrl;
  private String debugMessage;
  private long timestamp;

  public ObjectNode toObjectNode(){
    val date = new Date(timestamp);
    return object()
        .with("httpStatus", getHttpStatus().value())
        .with("errorId", getErrorId())
        .with("date", date.toString())
        .with("timestamp", timestamp)
        .with("message", getMessage())
        .with("debugMessage", getDebugMessage())
        .with("requestUrl", getRequestUrl())
        .with("stackTrace", array(
            getStackTrace()
                .stream()
                .map(Object::toString)
                .collect(toImmutableList())))
        .end();
  }

  public void setStackTrace(StackTraceElement[] stackTrace){
    setStackTrace(stream(stackTrace).collect(toImmutableList()));
  }

  public void setStackTrace(List<StackTraceElement> stackTrace){
    this.stackTrace = stackTrace;
  }

}
