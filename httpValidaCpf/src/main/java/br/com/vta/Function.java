package br.com.vta;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

public class Function {
    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
    @FunctionName("httpValidaCpf")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse request body
        String requestBody = request.getBody().orElse(null);
        String cpf = null;

        if (requestBody != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                CpfRequest cpfRequest = objectMapper.readValue(requestBody, CpfRequest.class);
                cpf = cpfRequest.getCpf();
            } catch (Exception e) {
                context.getLogger().severe("Failed to parse request body: " + e.getMessage());
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body("Invalid JSON format")
                        .build();
            }
        }

        if (cpf == null || !isValidCPF(cpf)) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Please pass a valid CPF in the request body")
                    .build();
        }

        return request.createResponseBuilder(HttpStatus.OK)
                .body("CPF is valid")
                .build();
    }

    // Create a method to validate CPF
    private boolean isValidCPF(String cpf) {
        if (cpf == null || cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
            return false;
        }
    
        try {
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            }
            int firstCheckDigit = 11 - (sum % 11);
            if (firstCheckDigit >= 10) {
                firstCheckDigit = 0;
            }
    
            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
            }
            int secondCheckDigit = 11 - (sum % 11);
            if (secondCheckDigit >= 10) {
                secondCheckDigit = 0;
            }
    
            return firstCheckDigit == Character.getNumericValue(cpf.charAt(9)) &&
                   secondCheckDigit == Character.getNumericValue(cpf.charAt(10));
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static class CpfRequest {
        private String cpf;

        public String getCpf() {
            return cpf;
        }

        public void setCpf(String cpf) {
            this.cpf = cpf;
        }
    }
}
