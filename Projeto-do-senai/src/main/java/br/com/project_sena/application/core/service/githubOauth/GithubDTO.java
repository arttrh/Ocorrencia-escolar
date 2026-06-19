package br.com.project_sena.application.core.service.githubOauth;

public record GithubDTO(
       String acess_token,
       String token_type,
       String scope
) {
}
