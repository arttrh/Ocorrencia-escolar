package br.com.project_sena.application.core.service.githubOauth;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class GithubAuthentication {

    private final String clientId = "${GITHUB_CLIENTID}";
    private final String githubSecret = "${GITHUB_SECRET}";
    private final String redirect = "http://localhost:8080/home";
    private final RestClient restClient;

    public GithubAuthentication(RestClient restClient) {
        this.restClient = restClient;
    }

    public String gerarUrl(){
        return UriComponentsBuilder.fromUriString("https://github.com/login/oauth/authorize")
                .queryParam("client_id", clientId) // é o client da minha aplicação
                .queryParam("redirect_uri", redirect) // vou redirecionar o usuario de volta para a minha aplicação
                .queryParam("scope", "read:user user:email") // so quero pegar usuario e o email dele
                .toUriString();
    }

    public GithubDTO obterToken(String code){
        return restClient.post()
                .uri("https://github.com/login/oauth/access_token")
                .contentType(MediaType.APPLICATION_JSON) // Mandando um Json
                .accept(MediaType.APPLICATION_JSON) // Esperando um json
                .body(Map.of("code", code, "client_id", clientId, "secret", githubSecret, "redirect_uri", redirect))
                .retrieve()
                .body(GithubDTO.class);
    }

    public Map<String, Object> obterDadosUsuarios(String acessToken){
        return restClient.get()
                .uri("https://api.github.com/user")
                .header("Authorization", "Bearer " + acessToken)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(Map.class);
    }
}
