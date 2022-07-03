package orion.sdk.java;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

public class HTTPRestClient implements RestClient {
    String userID;
    HttpClient client;
    Duration queryTimeout;
    Duration txTimeout;

    public HTTPRestClient(String userID, HttpClient client, Duration queryTimeout, Duration txTimeout) {
        this.userID = userID;
        this.client = client;
        this.queryTimeout = queryTimeout;
        this.txTimeout = txTimeout;
    }

    public HttpResponse<String> Query(String endpoint, String httpMethod, byte[] postData, byte[] signature) throws InterruptedException, IOException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(endpoint))
            .GET()
            .headers("Accept", "application/json", "UserID",  userID, "Signature", Base64.getEncoder().encodeToString(signature))
            .timeout(queryTimeout)
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        return response;
    }

    public HttpResponse<String> Submit(String endpoint, Message msg) throws InterruptedException, IOException {
        String jsonString = JsonFormat.printer()
                .preservingProtoFieldNames()
                .print(msg);

        System.out.println(jsonString);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(endpoint))
            .POST(HttpRequest.BodyPublishers.ofString(jsonString))
            .headers("Accept", "application/json", "TxTimeout", txTimeout.toSeconds() + "s")
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        return response;
    }
}
