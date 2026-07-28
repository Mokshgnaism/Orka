package com.Orka.apiTests;

import com.Orka.apiContract.generated.GetAllWorkflowRunsResponse;
import com.Orka.apiContract.generated.GetSingleWorkflowRunResponse;
import com.google.protobuf.util.JsonFormat;
import com.Orka.apiContract.generated.GetSingleTaskRunResponse;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.Orka.apiContract.generated.GetAllTaskRunsResponse;
public class TestGetWorkflow {

    private static final String BASE_URL = "http://localhost:4536";

    public static boolean test_get_workflow() throws IOException, InterruptedException {

        CookieManager cookieManager =
                new CookieManager(null, CookiePolicy.ACCEPT_ALL);

        HttpClient client = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .build();

        //----------------------------------------------------
        // LOGIN
        //----------------------------------------------------

        String loginBody = """
                {
                    "username":"mokshu1",
                    "password":"mokshu1"
                }
                """;

        HttpRequest loginRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(loginBody))
                .build();

        HttpResponse<byte[]> loginResponse =
                client.send(loginRequest,
                        HttpResponse.BodyHandlers.ofByteArray());

        System.out.println("=================================");
        System.out.println("LOGIN");
        System.out.println("=================================");

        System.out.println("Status : " + loginResponse.statusCode());

        if (loginResponse.statusCode() != 200) {
            System.out.println("Login failed.");
            System.out.println(new String(loginResponse.body()));
            return false;
        }

        //----------------------------------------------------
        // PRINT STORED COOKIES
        //----------------------------------------------------

        System.out.println("\nCookies stored by HttpClient:");

        for (HttpCookie cookie : cookieManager.getCookieStore().getCookies()) {
            System.out.println(cookie);
        }

        //----------------------------------------------------
        // GET WORKFLOW
        //----------------------------------------------------

        String workflowId =
                "914c6e90-c61d-4022-862c-976770f386d2";

        HttpRequest workflowRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/run/workflow/" + workflowId))
                .header("Accept", "application/x-protobuf")
                .GET()
                .build();

        HttpResponse<byte[]> workflowResponse =
                client.send(workflowRequest,
                        HttpResponse.BodyHandlers.ofByteArray());

        System.out.println("\n=================================");
        System.out.println("GET WORKFLOW");
        System.out.println("=================================");

        System.out.println("Status : " + workflowResponse.statusCode());

        if (workflowResponse.statusCode() != 200) {
            System.out.println("Request failed.");
            System.out.println(new String(workflowResponse.body()));
            return false;
        }
        GetSingleWorkflowRunResponse response =
                GetSingleWorkflowRunResponse.parseFrom(
                        workflowResponse.body());

        System.out.println("\n=================================");
        System.out.println("PROTOBUF RESPONSE");
        System.out.println("=================================");

        String json = JsonFormat.printer()
                .includingDefaultValueFields()
                .print(response);

        System.out.println(json);

        return true;
    }

    public static void main(String[] args)
            throws IOException, InterruptedException {

        boolean passed = test_get_workflow();

        System.out.println("\n=================================");
        System.out.println("TEST RESULT");
        System.out.println("=================================");

        if (passed) {
            System.out.println("✅ SUCCESS");
        } else {
            System.out.println("❌ FAILED");
        }
    }
}