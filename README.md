# SoccerNow: Gestão de Jogos de Futsal

# Esse ficheiro deve ser rescrito com base nas informações descritas no enunciado do projeto.

---
Membros:

André Santos - fc57538
Erickson Cacondo - fc53653
Miguel Sanchez - fc64675

**Projeto Prático #1:**  
 - O projeto deve ser desenvolvido por grupos de **no máximo três alunos**.  
 - Devem identificar no ficheiro `Readme.md` os integrantes do grupo, o número, e quais são os conjuntos de casos de uso que cada um dos alunos ira resolver.

 **Plágio:**  
 - Além de inspeção manual, será utilizado um software de deteção de plágio no código-fonte do projeto.  
 - **Todos** os alunos que submeterem código obtido através de plágio terão os seus projetos anulados.

 **Uso de Inteligência Artificial:**  
 - É permitido utilizar ferramentas de IA como ChatGPT, Copilot, DeepSeek e similares.  
 - Contudo, todos os membros do grupo devem ser capazes de **compreender e explicar** o projeto **na sua totalidade** aos docentes quando solicitado.  Se houver código que o grupo não consiga explicar, o projeto será considerado como **plágio**.

**Submissão:**  
 - A data de submissão do projeto é **01/05/2025** às 23:59.  
 - A descrição do processo de submissão encontra-se no enunciado.

---

## Tarefas da Fase 1

A equipa deverá:

- Fazer *fork* do repositório original: [https://git.alunos.di.fc.ul.pt/css000/soccernow](https://git.alunos.di.fc.ul.pt/css000/soccernow) para a conta de um dos elementos do grupo.
- Dar acesso a todos os membros do grupo como **Maintainer**.
- Dar acesso à conta **css000** como **Reporter**.
- Alterar o `Readme.md` para identificar a equipa e indicar qual conjunto de casos de uso cada membro implementará.
- Criar uma pasta `docs` contendo um único documento PDF com todos os diagramas do projeto.
- Desenhar o modelo de domínio, considerando todos os casos de uso, e colocá-lo na pasta `docs`.
- Desenhar o diagrama de sequência (SSD) para o caso de uso **H**.
- Esboçar o diagrama de classes que demonstre a divisão em camadas.
- Anotar as classes relevantes com as anotações JPA para garantir o melhor mapeamento possível.
- Justificar, no relatório, cada decisão tomada no mapeamento JPA.
- Incluir no relatório as garantias que o sistema oferece em relação à lógica de negócio.
- Gerar a base de dados a partir das anotações.
- Implementar testes que garantam a correção da lógica de negócio.
- Implementar os endpoints REST necessários (acessíveis via Swagger) para os casos de uso implementados.

---

## Como Entregar

Para entregar o trabalho, crie uma tag chamada `fase1` e envie-a para o repositório. O repositório deverá conter:
- O código-fonte do projeto.
- Os ficheiros necessários para executar o projeto em Docker.
- Um único documento PDF na pasta `docs` com todos os diagramas.

Execute os seguintes comandos:

```bash
git tag fase1
git push origin fase1
```

### Atenção:
Confirme que o projeto está acessível à conta CSS000 na tag fase1. Caso contrário, a entrega receberá nota 0. Certifique-se de que o projeto pode ser executado sem erros de compilação ou outros impedimentos, dentro do ambiente docker.

---


# FAQ

## Preciso de `sudo` para correr o `run.sh`
Tenta correr `sudo usermod -aG docker $USER` seguido de um log-out na máquina.
Ou tentar [desta forma](https://www.digitalocean.com/community/questions/how-to-fix-docker-got-permission-denied-while-trying-to-connect-to-the-docker-daemon-socket)

## O Docker não instala em ubuntu.

Tentar [desta forma](https://askubuntu.com/a/1411717).

## O `run.sh` não está a correr no meu macos m1.

Tentar correr `docker ps`. Se não funcionar, [tentar isto](https://stackoverflow.com/a/68202428/28516).
Confirmar também que está instalado o Docker Desktop (`brew install --cask docker`) e não apenas a command-line tool (`brew install docker`). A aplicação Docker deve também estar a correr (icon na menubar).


## Estou em windows e o `bash setup.sh` não funciona

Correr numa bash (tanto a Git Bash, MSys2 bash ou WSL em linux) e não na Powershell, nem no CMD.exe.


## Estou em windows, tenho o python instalado mas ao correr o `bash setup.sh` ele não encontra o pip

Deve adicionar o pip ao PATH. Procure em `C:\Users\<vosso_user>\AppData\Local\Programs\Python\<versao_do_python>\Scripts`. 
Atenção pois essa pasta potencialmente estará oculta.


## `docker compose` não funciona

`docker compose` é o comando da última versão de docker. `docker-compose` é a versão antiga. Devem actualizar o docker.

## Ao executar o `bash run.sh` tenho um erro 401 e não consigo descarregar as imagens

Deve no terminal fazer logout do docker `docker logout` e refazer o login `docker login`.

