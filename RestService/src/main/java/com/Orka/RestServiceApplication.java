package com.Orka;

import com.Orka.apiContract.generated.services.DefinitionManagerGrpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.grpc.client.ImportGrpcClients;

@SpringBootApplication
@ImportGrpcClients(
        target = "definition-manager",
        types = DefinitionManagerGrpc.DefinitionManagerBlockingStub.class
)
public class RestServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestServiceApplication.class, args);
    }

}
