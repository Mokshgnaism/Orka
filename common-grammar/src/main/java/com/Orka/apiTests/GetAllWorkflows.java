package com.Orka.apiTests;

import com.Orka.apiContract.generated.GetAllWorkflowRunsResponse;
import com.google.protobuf.util.JsonFormat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.Orka.apiTests.ApprovalWorkflowIntegrationTest.BASE_URL;

public class GetAllWorkflows {

    public static void getAllWorkflows() throws IOException, InterruptedException {

        HttpClient client = login.getLoggedInClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/run/workflows"))
                .header("Accept", "application/x-protobuf")
                .GET()
                .build();

        HttpResponse<byte[]> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofByteArray()
        );

        GetAllWorkflowRunsResponse workflows =
                GetAllWorkflowRunsResponse.parseFrom(response.body());

        System.out.println(
                JsonFormat.printer()
                        .includingDefaultValueFields()
                        .print(workflows)
        );
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        getAllWorkflows();
    }
}