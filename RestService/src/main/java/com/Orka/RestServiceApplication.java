package com.Orka;

import com.Orka.apiContract.generated.services.DefinitionManagerGrpc;
import com.Orka.apiContract.generated.services.RunManagerGrpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.grpc.client.ImportGrpcClients;

@SpringBootApplication
@ImportGrpcClients(
        target = "definition-manager",
        types = DefinitionManagerGrpc.DefinitionManagerBlockingStub.class
)
@ImportGrpcClients(
        target = "run-manager",
        types = RunManagerGrpc.RunManagerBlockingStub.class
)
public class RestServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestServiceApplication.class, args);
    }
}
