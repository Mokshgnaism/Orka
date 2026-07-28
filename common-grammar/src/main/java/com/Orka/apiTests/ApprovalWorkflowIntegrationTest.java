package com.Orka.apiTests;

import com.Orka.apiContract.generated.*;
import com.google.protobuf.util.JsonFormat;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApprovalWorkflowIntegrationTest {

    static final String BASE_URL = "http://localhost:4536";

    // Replace this
    private static final String WORKFLOW_DEFINITION_ID =
            "ad9a6078-6513-4cdc-a81c-8d850c040cd3";

    private static HttpClient client;

    //------------------------------------------------------------
    // LOGIN
    //------------------------------------------------------------

    private static void login() throws Exception {

        CookieManager cookieManager =
                new CookieManager(null, CookiePolicy.ACCEPT_ALL);

        client = HttpClient.newBuilder()
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
    }

    //------------------------------------------------------------
    // START WORKFLOW
    //------------------------------------------------------------

    private static String startWorkflow() throws Exception {

        String body = """
                {
  "id": "ad9a6078-6513-4cdc-a81c-8d850c040cd3",
  "username": "mokshu1",

  "taskRunAuthorization": [
 ],

  "workflowAuthorization": [
    {
      "username": "mokshu1",
      "authRole": "WORKFLOW_RUN_MANAGER"
    }
  ]
}""";

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/api/run/startWorkflow"))
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/x-protobuf")
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build();

        HttpResponse<byte[]> response =
                client.send(request,
                        HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 200) {

            throw new RuntimeException(
                    "Unable to start workflow\n"
                            + new String(response.body()));
        }

        StartWorkflowRunResponse start =
                StartWorkflowRunResponse.parseFrom(response.body());

        System.out.println("==================================");
        System.out.println("WORKFLOW STARTED");
        System.out.println("==================================");

        System.out.println(
                JsonFormat.printer()
                        .includingDefaultValueFields()
                        .print(start));

        return start.getId();
    }

    //------------------------------------------------------------
    // GET WORKFLOW
    //------------------------------------------------------------

    private static GetSingleWorkflowRunResponse
    getWorkflow(String workflowRunId)
            throws Exception {

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        BASE_URL +
                                                "/api/run/workflow/" +
                                                workflowRunId))
                        .header(
                                "Accept",
                                "application/x-protobuf")
                        .GET()
                        .build();

        HttpResponse<byte[]> response =
                client.send(request,
                        HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 200) {

            throw new RuntimeException(
                    "Unable to fetch workflow.");
        }

        GetSingleWorkflowRunResponse workflow =
                GetSingleWorkflowRunResponse.parseFrom(
                        response.body());

        System.out.println(
                JsonFormat.printer()
                        .includingDefaultValueFields()
                        .print(workflow));

        return workflow;
    }

    //------------------------------------------------------------
    // FIND TASK
    //------------------------------------------------------------

    private static GetSingleTaskRunResponse
    findTask(
            GetSingleWorkflowRunResponse workflow,
            String taskName) {

        for (GetSingleTaskRunResponse task :
                workflow.getTaskRunsList()) {

            if (task.getTaskRun()
                    .getTaskDefinitionName()
                    .equals(taskName)) {

                return task;
            }
        }

        throw new RuntimeException(
                "Task not found : "
                        + taskName);
    }

    //------------------------------------------------------------
    // GET TASK ID
    //------------------------------------------------------------

    private static String getTaskRunId(
            GetSingleWorkflowRunResponse workflow,
            String taskName) {

        return findTask(
                workflow,
                taskName)
                .getTaskRun()
                .getId();
    }

    //------------------------------------------------------------
    // ASSERT CURRENT STATE
    //------------------------------------------------------------

    private static void assertState(
            GetSingleWorkflowRunResponse workflow,
            String taskName,
            String expectedState) {

        GetSingleTaskRunResponse task =
                findTask(workflow,
                        taskName);

        String actual =
                task.getTaskRun()
                        .getCurrentStateDefinitionName();

        if (!actual.equals(expectedState)) {

            throw new RuntimeException(
                    "Expected "
                            + taskName
                            + " to be in "
                            + expectedState
                            + " but was "
                            + actual);
        }

        System.out.println(
                "✓ "
                        + taskName
                        + " -> "
                        + actual);
    }

    //------------------------------------------------------------
    // WAIT UNTIL STATE
    //------------------------------------------------------------

    private static GetSingleWorkflowRunResponse
    waitUntilState(
            String workflowRunId,
            String taskName,
            String state)
            throws Exception {

        for (int i = 0; i < 20; i++) {

            GetSingleWorkflowRunResponse workflow =
                    getWorkflow(workflowRunId);

            try {

                assertState(
                        workflow,
                        taskName,
                        state);

                return workflow;

            } catch (Exception ignored) {

            }

            Thread.sleep(500);
        }

        throw new RuntimeException(
                "Timed out waiting for "
                        + taskName
                        + " -> "
                        + state);
    }
    //------------------------------------------------------------
    // PROVIDE INPUT
    //------------------------------------------------------------

    private static void provideBooleanInput(
            String taskRunId,
            String path,
            boolean value)
            throws Exception {

        String body = """
                {
                    "taskRunId":"%s",
                    "path":"%s",
                    "inputVal":%s,
                    "username":"mokshu1"
                }
                """.formatted(
                taskRunId,
                path,
                Boolean.toString(value));

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        BASE_URL +
                                                "/api/run/provideInput"))
                        .header(
                                "Content-Type",
                                "application/json")
                        .header(
                                "Accept",
                                "application/x-protobuf")
                        .POST(
                                HttpRequest.BodyPublishers.ofString(body))
                        .build();

        HttpResponse<byte[]> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 200) {

            throw new RuntimeException(
                    "Provide Input Failed\n"
                            + new String(response.body()));
        }

        System.out.println("----------------------------------");
        System.out.println("INPUT PROVIDED");
        System.out.println("----------------------------------");
        System.out.println(body);
    }

    //------------------------------------------------------------
    // PRINT TASK STATES
    //------------------------------------------------------------

    private static void printTaskStates(
            GetSingleWorkflowRunResponse workflow) {

        System.out.println();
        System.out.println("============== TASKS ==============");

        for (GetSingleTaskRunResponse task :
                workflow.getTaskRunsList()) {

            TaskRunDTO dto = task.getTaskRun();

            System.out.printf(
                    "%-15s -> %-20s (%s)%n",
                    dto.getTaskDefinitionName(),
                    dto.getCurrentStateDefinitionName(),
                    dto.getStatus());
        }

        System.out.println("===================================");
        System.out.println();
    }

    //------------------------------------------------------------
    // MAIN
    //------------------------------------------------------------

    public static void main(String[] args)
            throws Exception {

        //----------------------------------------------------
        // LOGIN
        //----------------------------------------------------

        login();

        //----------------------------------------------------
        // START WORKFLOW
        //----------------------------------------------------

        String workflowRunId =
                startWorkflow();

        //----------------------------------------------------
        // WAIT UNTIL APPROVAL STARTS
        //----------------------------------------------------

        GetSingleWorkflowRunResponse workflow =
                waitUntilState(
                        workflowRunId,
                        "Approval",
                        "AskingApproval");

        printTaskStates(workflow);

        //----------------------------------------------------
        // APPROVE
        //----------------------------------------------------

        String approvalTaskRunId =
                getTaskRunId(
                        workflow,
                        "Approval");

        System.out.println(
                "Approval TaskRun : "
                        + approvalTaskRunId);

        provideBooleanInput(
                approvalTaskRunId,
                "$.approved",
                true);

        //----------------------------------------------------
        // WAIT FOR APPROVAL STATE
        //----------------------------------------------------

        workflow =
                waitUntilState(
                        workflowRunId,
                        "Approval",
                        "Approved");

        printTaskStates(workflow);

        //----------------------------------------------------
        // WAIT FOR BUY TO START
        //----------------------------------------------------

        workflow =
                waitUntilState(
                        workflowRunId,
                        "Buy",
                        "Buying");

        printTaskStates(workflow);

        //----------------------------------------------------
        // PURCHASE SUCCESS
        //----------------------------------------------------

        String buyTaskRunId =
                getTaskRunId(
                        workflow,
                        "Buy");

        System.out.println(
                "Buy TaskRun : "
                        + buyTaskRunId);

        provideBooleanInput(
                buyTaskRunId,
                "$.success",
                true);

        //----------------------------------------------------
        // WAIT FOR DONE
        //----------------------------------------------------

        workflow =
                waitUntilState(
                        workflowRunId,
                        "Buy",
                        "Done");

        printTaskStates(workflow);

        //----------------------------------------------------
        // FINAL WORKFLOW
        //----------------------------------------------------

        System.out.println();
        System.out.println("========== FINAL WORKFLOW ==========");

        System.out.println(
                JsonFormat.printer()
                        .includingDefaultValueFields()
                        .print(workflow));

        System.out.println("====================================");

        //----------------------------------------------------
        // SUCCESS
        //----------------------------------------------------

        System.out.println();
        System.out.println("####################################");
        System.out.println("#                                  #");
        System.out.println("#   APPROVAL WORKFLOW TEST PASSED  #");
        System.out.println("#                                  #");
        System.out.println("####################################");
    }
}