package com.Orka.apiTests;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.Orka.apiTests.ApprovalWorkflowIntegrationTest.BASE_URL;

public class login {

    public static HttpClient getLoggedInClient() throws IOException, InterruptedException {
        CookieManager cookieManager =
                new CookieManager(null, CookiePolicy.ACCEPT_ALL);

        var client = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .build();

        String loginBody = """
                {
                    "username":"mokshu1",
                    "password":"mokshu1"
                }
                """;

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/api/auth/login"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(loginBody))
                        .build();

        HttpResponse<byte[]> response =
                client.send(request,
                        HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 200) {

            throw new RuntimeException(
                    "Login failed\n"
                            + new String(response.body()));
        }

        System.out.println("==================================");
        System.out.println("LOGIN SUCCESS");
        System.out.println("==================================");

        for (HttpCookie cookie :
                cookieManager.getCookieStore().getCookies()) {

            System.out.println(cookie);
        }
        return client;
    }
}
