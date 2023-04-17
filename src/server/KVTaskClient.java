package server;

import com.google.gson.*;
import manager.*;

import java.io.*;
import java.net.*;
import java.net.http.*;

public class KVTaskClient {
    public final String API_TOKEN;

    public String getUrl() {
        return url;
    }

    private final HttpClient client;
    private final String url;
    private final Gson gson = new Gson();

    public KVTaskClient(String url) throws IOException, InterruptedException {
        this.client = HttpClient.newHttpClient();
        this.url = url;
        URI uri = URI.create(url + "/register");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        API_TOKEN = response.body();
    }

    public void put(String key, String json) throws ManagerSaveException {

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        URI uri = URI.create(url + "/save/" + key + "/?API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(body).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("Сохранение не выполнено");
            }
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Ошибка при сохранен: " + e.getMessage());
        }
    }

    public String load(String key) throws IOException, InterruptedException {
        URI uri = URI.create(url + "/load/" + key + "/?API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return response.body();
        }
        return " ";
    }
}
