# Azure Function: Validação de CPF em Java

## Ferramentas Necessárias
- **Java 11+**
- **Azure CLI**
- **Azure Functions Core Tools**
- **Apache Maven**

## Criar um projeto de função local

```sh
mvn archetype:generate -DarchetypeGroupId=com.microsoft.azure -DarchetypeArtifactId=azure-functions-archetype -DjavaVersion=11
```

Navegue até a pasta do projeto:

```sh
cd nome_projeto
```

## Executar a função localmente
```sh
mvn clean package -DskipTests
mvn azure-functions:run
```

Perto do fim da saída, devem aparecer as seguintes linhas:
```sh
...

 Now listening on: http://0.0.0.0:7071
 Application started. Press Ctrl+C to shut down.

 Http Functions:

         HttpExample: [GET,POST] http://localhost:7071/api/httpValidaCpf
 ...
```

Copie a URL da função HttpExample dessa saída para um navegador e acrescente a cadeia de caracteres de consulta ?cpf=<CPF>
```sh
http://localhost:7071/api/httpValidaCpf
```

## Publicar a funçao. Web deploy

Fazer o login, caso ainda não tenha feito:
```sh
az login
```

Realizar o deĺoy:
```sh
mvn azure-functions:deploy
```

Isso cria os seguintes recursos no Azure:
- Grupo de recursos. Nomeado como java-functions-group.
- Conta de armazenamento. Necessária para o Functions. O nome é gerado aleatoriamente de acordo com os requisitos de nome da conta de armazenamento.
- Plano de hospedagem. Hospedagem sem servidor para o aplicativo de funções na região westus. O nome é java-functions-app-service-plan.
- Aplicativo de funções. Um aplicativo de funções é a unidade de implantação e execução para suas funções. O nome é gerado aleatoriamente com base no artifactId, anexado a um número gerado aleatoriamente.

## Limpar recursos
excluir o grupo de recursos e todos os recursos contidos nele para evitar custos adicionais:

```sh
az group delete --name java-functions-group
```