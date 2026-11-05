# Conclusão a partir de 928ac97

Base escolhida: 928ac97. Preservados os pacotes adapter, application, port, config e exception, os modelos e a estrutura de serviços e repositórios.

Mudanças necessárias: compilar, persistir vínculos, concluir ocorrências, corrigir atualização de aluno e listagem de turmas, tratar JWT inválido e usuário inativo, adaptar a API ao frontend e configurar deploy.

Cucumber permanece com cenários de vínculo; ArchUnit verifica as fronteiras compatíveis com a estrutura original. O núcleo original continua usando Spring em alguns contratos. Não foi introduzida uma nova arquitetura.

Sem RabbitMQ: os dois arquivos antigos não tinham chamadores nem dependência no pom e impediam compilação. Não foram adicionados broker, ports de transação, fakes próprios, scanner de arquitetura ou testes de desempenho.

Banco de deploy deve ser novo. V1 não cria mais administrador com senha conhecida; ADMIN_EMAIL e ADMIN_PASSWORD criam a primeira conta. Não aplicar esse histórico de migrations sobre uma instalação existente sem conferir os checksums e preservar os dados.

Validação: mvn verify usa PostgreSQL real. FluxoApiTest cobre login, autenticação, perfis, aluno, turma, vínculo, ocorrência, atualização e cancelamento. Cucumber e ArchUnit executam junto.
