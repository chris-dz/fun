package edu.lewisu.kdziedzic.fun;

import java.net.URLDecoder;
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
    @StorageAccount("AzureWebJobsStorage")
    public HttpResponseMessage getForm(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BlobInput(name = "file", dataType = "string", path = "app-data/form.html") String form,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        String response = form;

        return request.createResponseBuilder(HttpStatus.OK).body(response).header("Content-Type","text/html").build();
    }

    @FunctionName("write")
    @StorageAccount("AzureWebJobsStorage")
    public HttpResponseMessage write(
        @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
        @BlobInput(name = "file", dataType = "string", path = "app-data/data.txt") String inFile,
        @BlobInput(name = "htmlHeader", dataType = "string", path = "app-data/header.txt") String pageHeader,
        @BlobInput(name = "htmlFooter", dataType = "string", path = "app-data/footer.txt") String pageFooter,
        @BlobOutput(name = "target", path = "app-data/data.txt") OutputBinding<String> outputItem,
        final ExecutionContext context) {

            String content = "";
            String result = pageHeader;

            if (request.getHttpMethod() == HttpMethod.POST) {
                // Add body of the request to the stored messages
                content = request.getBody().get();
                try {
                    content = URLDecoder.decode(content, "utf-8" /*Strangely, Maven/compiler on Azure didn't like StandardCharsets.UTF_8*/);
                    if (content.startsWith("message=")) {
                        content = content.substring("message=".length());
                    }
                    Calendar calendar = Calendar.getInstance();
                    content = String.format("<Entry timestamp=\"%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS\">\n%2$s\n</Entry>\n\n",
                        calendar, content) + inFile;
                    outputItem.setValue(content);
                } catch (Exception e) {
                    content = e.toString();
                    for (StackTraceElement el : e.getStackTrace()) {
                        content += "\n" + el;
                    }
                    return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body(content)
                        .build();
                }
            } else {
                // This is a GET request, just get the existing messages
                // convert the content string to HTML
                content = inFile;
            }

            content = content.replaceAll("\">", "\n");
            content = content.replaceAll("<Entry ", "<p>");
            content = content.replaceAll("timestamp=\"", "Signed in on: ");
            content = content.replaceAll("</Entry>", "</p>");

            result += content + pageFooter;

            // build HTTP response with the content of the POST body
            return request.createResponseBuilder(HttpStatus.OK)
                .body(result)
                .header("Content-Type","text/html")
                .build();
    }

    private String getPageHeader() {
        return "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">" +
        "<html>" +
        "<head>" +
            "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"/>" +
            "<title>Fun</title>" +
            "<style>" +
            "body {" +
                "background-image: url(\"https://storageaccountfunrgaa05.blob.core.windows.net/app-data/IMG_9456_sm_cr.JPG\"); " +
                "background-repeat: no-repeat; " +
                "background-position: center; " +
                "background-attachment: fixed; " +
            "}" +
            "p {" +
                "background-color: LightGray; " +
                "white-space: pre; " +
                "opacity: 0.7; " +
                "margin: 50; " +
                "padding: 20;" +
            "}" +
            "</style>" +
        "</head>" +
        "<body lang=\"en-US\" dir=\"ltr\">" +
        "<a href=\"https://fun-kd.azurewebsites.net/api/getForm\">Click here to add an entry</a></br>";
    }

    private String getPageFooter() {
        return "<a href=\"https://fun-kd.azurewebsites.net/api/getForm\">Click here to add an entry</a></body></html>";
    }
}
