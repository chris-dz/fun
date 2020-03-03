package edu.lewisu.kdziedzic.fun;

import java.nio.charset.StandardCharsets;
import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
    @FunctionName("HttpExample")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        String query = request.getQueryParameters().get("name");
        String name = request.getBody().orElse(query);

        if (name == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        } else {
            return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
        }
    }

    @FunctionName("getForm")
    public HttpResponseMessage getForm(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        String response = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n" +
        "<html>" +
        "<head>" +
            "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"/>" +
            "<title>Fun</title>" +
        "</head>" +
        "<body lang=\"en-US\" dir=\"ltr\">" +
        "<div id=\"header\">" +
        "<form id=\"form\" method=\"post\" action=\"/api/write\">" +
            "<fieldset>" +
                "<legend>Guest book entry form</legend>" +
                "<p>" +
                "<label for=\"message\">Sign in:</label><br/>" +
                "<textarea id=\"message\" name=\"message\" placeholder=\"Enter your sign in text here\" rows=\"10\" cols=\"200\"></textarea><br/>" +
                "</p>" +
                "<div style=\"display: block; text-align: center;\">" +
                    "<input type=\"submit\" value=\"Submit\"/>" +
                "</div>" +
            "</fieldset>" +
        "</form></div></body></html>";

        return request.createResponseBuilder(HttpStatus.OK).body(response).header("Content-Type","text/html").build();
    }

    @FunctionName("write")
    @StorageAccount("https://storageaccountfunrgaa05.blob.core.windows.net")
    public HttpResponseMessage write(
        @HttpTrigger(name = "req", methods = {HttpMethod.PUT}, authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
        @BlobInput(name = "file", dataType = "binary", path = "app-data/data.txt") byte[] retrieved,
        @BlobOutput(name = "target", path = "app-data/data.txt") OutputBinding<String> outputItem,
        final ExecutionContext context) {
            // Save blob to outputItem
            String content = request.getBody().get();
            outputItem.setValue("<Entry>\n" + content + "</Entry>\n\n");

            String result = new String(retrieved, StandardCharsets.UTF_8);

            // build HTTP response with size of requested blob
            return request.createResponseBuilder(HttpStatus.OK)
                .body(result)
                .build();
    }
}
