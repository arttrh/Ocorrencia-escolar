#!/usr/bin/env python3
"""
Scanner de arquitetura hexagonal.

Varre todo o codigo-fonte Java do projeto e verifica, arquivo por arquivo, se as
dependencias respeitam as fronteiras da arquitetura hexagonal descritas em
`tools/arch_rules.json`.

Diferente do teste ArchUnit (que roda no build e olha bytecode), este scanner:

  * le os fontes, entao aponta a linha exata do import que viola a regra;
  * mostra um mapa das camadas e uma matriz de quem depende de quem;
  * nao precisa que o projeto compile - util justamente quando ele nao compila;
  * gera relatorio em texto, JSON ou HTML.

Uso:
    python3 tools/arch_scan.py                     # relatorio no terminal
    python3 tools/arch_scan.py --json saida.json   # para consumo por CI
    python3 tools/arch_scan.py --html relatorio.html
    python3 tools/arch_scan.py --strict            # avisos tambem falham o build

Saida: 0 quando nao ha violacao bloqueante, 1 quando ha.
"""

from __future__ import annotations

import argparse
import html
import json
import os
import re
import sys
from collections import defaultdict
from dataclasses import dataclass, field, asdict
from pathlib import Path

# --------------------------------------------------------------------------- #
# Modelo
# --------------------------------------------------------------------------- #


@dataclass
class Violacao:
    regra: str
    titulo: str
    severidade: str
    arquivo: str
    linha: int
    detalhe: str
    explicacao: str = ""

    @property
    def bloqueante(self) -> bool:
        return self.severidade == "erro"


@dataclass
class ArquivoJava:
    caminho: Path
    caminho_relativo: str
    pacote: str
    classe: str
    imports: list[tuple[int, str]]
    anotacoes: set[str]
    e_interface: bool
    linhas: list[str]
    camada: str | None = None


@dataclass
class Relatorio:
    arquivos: int = 0
    por_camada: dict[str, int] = field(default_factory=dict)
    matriz: dict[str, dict[str, int]] = field(default_factory=dict)
    violacoes: list[Violacao] = field(default_factory=list)
    sem_camada: list[str] = field(default_factory=list)


# --------------------------------------------------------------------------- #
# Leitura dos fontes
# --------------------------------------------------------------------------- #

RE_PACOTE = re.compile(r"^\s*package\s+([\w.]+)\s*;")
RE_IMPORT = re.compile(r"^\s*import\s+(?:static\s+)?([\w.*]+)\s*;")
RE_ANOTACAO = re.compile(r"^\s*@(\w+)")
RE_TIPO = re.compile(
    r"^\s*(?:public|protected|private|abstract|final|sealed|non-sealed|static|\s)*"
    r"\b(class|interface|enum|record)\s+(\w+)"
)
RE_COMENTARIO_LINHA = re.compile(r"//.*$")


def remover_comentarios(linhas: list[str]) -> list[str]:
    """Remove comentarios para que regras textuais nao acusem exemplos em javadoc."""
    limpas: list[str] = []
    dentro_de_bloco = False
    for linha in linhas:
        resultado = ""
        i = 0
        while i < len(linha):
            if dentro_de_bloco:
                fim = linha.find("*/", i)
                if fim == -1:
                    i = len(linha)
                else:
                    dentro_de_bloco = False
                    i = fim + 2
            else:
                inicio = linha.find("/*", i)
                if inicio == -1:
                    resultado += linha[i:]
                    i = len(linha)
                else:
                    resultado += linha[i:inicio]
                    dentro_de_bloco = True
                    i = inicio + 2
        limpas.append(RE_COMENTARIO_LINHA.sub("", resultado))
    return limpas


def ler_arquivo(caminho: Path, raiz: Path) -> ArquivoJava | None:
    try:
        texto = caminho.read_text(encoding="utf-8")
    except (OSError, UnicodeDecodeError):
        return None

    linhas = texto.splitlines()
    limpas = remover_comentarios(linhas)

    pacote = ""
    imports: list[tuple[int, str]] = []
    anotacoes: set[str] = set()
    classe = caminho.stem
    e_interface = False
    tipo_encontrado = False

    for numero, linha in enumerate(limpas, start=1):
        if not pacote:
            m = RE_PACOTE.match(linha)
            if m:
                pacote = m.group(1)
                continue
        m = RE_IMPORT.match(linha)
        if m:
            imports.append((numero, m.group(1)))
            continue
        m = RE_ANOTACAO.match(linha)
        if m and not tipo_encontrado:
            anotacoes.add("@" + m.group(1))
            continue
        if not tipo_encontrado:
            m = RE_TIPO.match(linha)
            if m:
                tipo_encontrado = True
                e_interface = m.group(1) == "interface"
                classe = m.group(2)

    return ArquivoJava(
        caminho=caminho,
        caminho_relativo=str(caminho.relative_to(raiz)),
        pacote=pacote,
        classe=classe,
        imports=imports,
        anotacoes=anotacoes,
        e_interface=e_interface,
        linhas=limpas,
    )


# --------------------------------------------------------------------------- #
# Motor de regras
# --------------------------------------------------------------------------- #


class Scanner:
    def __init__(self, config: dict, raiz: Path):
        self.config = config
        self.raiz = raiz
        self.base = config["base_package"]
        self.camadas = config["layers"]
        self.por_id = {c["id"]: c for c in self.camadas}

    # --- classificacao ---------------------------------------------------- #

    def prefixos(self, camada: dict) -> list[str]:
        return [
            f"{self.base}.{p}" if p else self.base for p in camada["packages"]
        ]

    def camada_do_pacote(self, pacote: str) -> str | None:
        """Primeira camada cujo prefixo casa. A ordem no JSON define a precedencia."""
        for camada in self.camadas:
            for prefixo in self.prefixos(camada):
                if pacote == prefixo or pacote.startswith(prefixo + "."):
                    return camada["id"]
        return None

    def camada_do_import(self, alvo: str) -> str | None:
        if not alvo.startswith(self.base):
            return None
        pacote = alvo.rsplit(".", 1)[0]
        return self.camada_do_pacote(pacote)

    def em_algum_pacote(self, pacote: str, sufixos: list[str]) -> bool:
        for sufixo in sufixos:
            prefixo = f"{self.base}.{sufixo}" if sufixo else self.base
            if pacote == prefixo or pacote.startswith(prefixo + "."):
                return True
        return False

    # --- execucao --------------------------------------------------------- #

    def escanear(self) -> Relatorio:
        relatorio = Relatorio()
        arquivos: list[ArquivoJava] = []

        for source_root in self.config["source_roots"]:
            base_dir = self.raiz / source_root
            if not base_dir.is_dir():
                print(f"aviso: source root inexistente: {source_root}", file=sys.stderr)
                continue
            for caminho in sorted(base_dir.rglob("*.java")):
                arquivo = ler_arquivo(caminho, self.raiz)
                if arquivo is None:
                    continue
                arquivo.camada = self.camada_do_pacote(arquivo.pacote)
                arquivos.append(arquivo)

        relatorio.arquivos = len(arquivos)
        contagem: dict[str, int] = defaultdict(int)
        matriz: dict[str, dict[str, int]] = defaultdict(lambda: defaultdict(int))

        for arquivo in arquivos:
            if arquivo.camada is None:
                relatorio.sem_camada.append(arquivo.caminho_relativo)
                continue
            contagem[arquivo.camada] += 1
            for _, alvo in arquivo.imports:
                destino = self.camada_do_import(alvo)
                if destino and destino != arquivo.camada:
                    matriz[arquivo.camada][destino] += 1

        relatorio.por_camada = dict(contagem)
        relatorio.matriz = {k: dict(v) for k, v in matriz.items()}

        for arquivo in arquivos:
            relatorio.violacoes.extend(self.verificar(arquivo))

        relatorio.violacoes.sort(
            key=lambda v: (v.severidade != "erro", v.regra, v.arquivo, v.linha)
        )
        return relatorio

    def verificar(self, arquivo: ArquivoJava) -> list[Violacao]:
        return [
            *self.checar_dependencias_internas(arquivo),
            *self.checar_dependencias_externas(arquivo),
            *self.checar_nomenclatura(arquivo),
            *self.checar_higiene(arquivo),
        ]

    def checar_dependencias_internas(self, arquivo: ArquivoJava) -> list[Violacao]:
        achados = []
        for regra in self.config.get("regras_internas", []):
            if arquivo.camada not in regra["de"]:
                continue
            for linha, alvo in arquivo.imports:
                destino = self.camada_do_import(alvo)
                if destino in regra["para_proibido"]:
                    achados.append(Violacao(
                        regra=regra["id"],
                        titulo=regra["titulo"],
                        severidade=regra.get("severidade", "erro"),
                        arquivo=arquivo.caminho_relativo,
                        linha=linha,
                        detalhe=(
                            f"{self.nome_camada(arquivo.camada)} -> "
                            f"{self.nome_camada(destino)}: import {alvo}"
                        ),
                        explicacao=regra.get("explicacao", ""),
                    ))
        return achados

    def checar_dependencias_externas(self, arquivo: ArquivoJava) -> list[Violacao]:
        achados = []
        for regra in self.config.get("regras_externas", []):
            if arquivo.camada not in regra["de"]:
                continue
            for linha, alvo in arquivo.imports:
                for proibido in regra["imports_proibidos"]:
                    if alvo == proibido or alvo.startswith(proibido + "."):
                        achados.append(Violacao(
                            regra=regra["id"],
                            titulo=regra["titulo"],
                            severidade=regra.get("severidade", "erro"),
                            arquivo=arquivo.caminho_relativo,
                            linha=linha,
                            detalhe=f"import proibido em {self.nome_camada(arquivo.camada)}: {alvo}",
                            explicacao=regra.get("explicacao", ""),
                        ))
                        break
        return achados

    def checar_nomenclatura(self, arquivo: ArquivoJava) -> list[Violacao]:
        achados = []
        for regra in self.config.get("regras_de_nomenclatura", []):
            severidade = regra.get("severidade", "erro")

            anotacao = regra.get("anotacao")
            if anotacao:
                if anotacao not in arquivo.anotacoes:
                    continue
                permitidos = regra.get("packages_permitidos")
                proibidos = regra.get("packages_proibidos")
                if permitidos and not self.em_algum_pacote(arquivo.pacote, permitidos):
                    achados.append(Violacao(
                        regra["id"], regra["titulo"], severidade,
                        arquivo.caminho_relativo, 1,
                        f"{arquivo.classe} usa {anotacao} fora de {', '.join(permitidos)}",
                        regra.get("explicacao", ""),
                    ))
                if proibidos and self.em_algum_pacote(arquivo.pacote, proibidos):
                    achados.append(Violacao(
                        regra["id"], regra["titulo"], severidade,
                        arquivo.caminho_relativo, 1,
                        f"{arquivo.classe} usa {anotacao} dentro de {', '.join(proibidos)}",
                        regra.get("explicacao", ""),
                    ))
                continue

            sufixo = regra.get("sufixo_classe")
            if sufixo and arquivo.classe.endswith(sufixo):
                permitidos = regra.get("packages_permitidos", [])
                if permitidos and not self.em_algum_pacote(arquivo.pacote, permitidos):
                    achados.append(Violacao(
                        regra["id"], regra["titulo"], severidade,
                        arquivo.caminho_relativo, 1,
                        f"{arquivo.classe} deveria estar em {', '.join(permitidos)}",
                        regra.get("explicacao", ""),
                    ))
                continue

            if regra.get("exige_interface"):
                alvos = regra.get("packages", [])
                if self.em_algum_pacote(arquivo.pacote, alvos) and not arquivo.e_interface:
                    achados.append(Violacao(
                        regra["id"], regra["titulo"], severidade,
                        arquivo.caminho_relativo, 1,
                        f"{arquivo.classe} deveria ser uma interface",
                        regra.get("explicacao", ""),
                    ))
        return achados

    def checar_higiene(self, arquivo: ArquivoJava) -> list[Violacao]:
        achados = []
        for regra in self.config.get("regras_de_higiene", []):
            padrao = re.compile(regra["padrao"])
            for numero, linha in enumerate(arquivo.linhas, start=1):
                if padrao.search(linha):
                    achados.append(Violacao(
                        regra["id"], regra["titulo"],
                        regra.get("severidade", "aviso"),
                        arquivo.caminho_relativo, numero,
                        linha.strip()[:120],
                        regra.get("explicacao", ""),
                    ))
        return achados

    def nome_camada(self, camada_id: str | None) -> str:
        if camada_id is None:
            return "(sem camada)"
        return self.por_id.get(camada_id, {}).get("nome", camada_id)


# --------------------------------------------------------------------------- #
# Saidas
# --------------------------------------------------------------------------- #

CORES = {
    "reset": "\033[0m", "negrito": "\033[1m", "cinza": "\033[90m",
    "vermelho": "\033[31m", "amarelo": "\033[33m", "verde": "\033[32m",
    "azul": "\033[36m",
}


def pinta(texto: str, cor: str, usar_cor: bool) -> str:
    return f"{CORES[cor]}{texto}{CORES['reset']}" if usar_cor else texto


def imprimir_texto(relatorio: Relatorio, scanner: Scanner, usar_cor: bool) -> None:
    p = lambda t, c="reset": print(pinta(t, c, usar_cor))

    p("")
    p("=" * 78)
    p("  SCANNER DE ARQUITETURA HEXAGONAL", "negrito")
    p("=" * 78)
    p("")
    p(f"Arquivos analisados: {relatorio.arquivos}")
    p("")

    p("CAMADAS", "negrito")
    p("-" * 78)
    for camada in scanner.camadas:
        total = relatorio.por_camada.get(camada["id"], 0)
        marcador = "  " * camada["nivel"]
        p(f"{marcador}{camada['nome']:<26} {total:>4} arquivo(s)   "
          f"{pinta(camada['descricao'], 'cinza', usar_cor)}")
    if relatorio.sem_camada:
        p("")
        p(f"  {len(relatorio.sem_camada)} arquivo(s) fora de qualquer camada declarada:", "amarelo")
        for caminho in relatorio.sem_camada[:10]:
            p(f"    - {caminho}", "amarelo")
    p("")

    p("DEPENDENCIAS ENTRE CAMADAS", "negrito")
    p("-" * 78)
    if not relatorio.matriz:
        p("  (nenhuma)")
    for origem in [c["id"] for c in scanner.camadas]:
        destinos = relatorio.matriz.get(origem)
        if not destinos:
            continue
        for destino, quantidade in sorted(destinos.items(), key=lambda kv: -kv[1]):
            nivel_origem = scanner.por_id[origem]["nivel"]
            nivel_destino = scanner.por_id[destino]["nivel"]
            seta = "-->" if nivel_destino <= nivel_origem else "==>"
            cor = "cinza" if nivel_destino <= nivel_origem else "vermelho"
            p(f"  {scanner.nome_camada(origem):<24} {seta} "
              f"{scanner.nome_camada(destino):<24} ({quantidade})", cor)
    p("")
    p(f"  {pinta('-->', 'cinza', usar_cor)} para dentro ou lateral (ok)   "
      f"{pinta('==>', 'vermelho', usar_cor)} para fora (violacao)")
    p("")

    erros = [v for v in relatorio.violacoes if v.bloqueante]
    avisos = [v for v in relatorio.violacoes if not v.bloqueante]

    p("VIOLACOES", "negrito")
    p("-" * 78)
    if not relatorio.violacoes:
        p("  Nenhuma violacao encontrada.", "verde")
    else:
        atual = None
        for v in relatorio.violacoes:
            if v.regra != atual:
                atual = v.regra
                cor = "vermelho" if v.bloqueante else "amarelo"
                rotulo = "ERRO " if v.bloqueante else "AVISO"
                p("")
                p(f"  [{rotulo}] {v.regra} - {v.titulo}", cor)
                if v.explicacao:
                    p(f"          {v.explicacao}", "cinza")
            p(f"    {v.arquivo}:{v.linha}")
            p(f"      {v.detalhe}", "cinza")
    p("")
    p("=" * 78)
    resumo = f"  {len(erros)} erro(s), {len(avisos)} aviso(s)"
    p(resumo, "vermelho" if erros else ("amarelo" if avisos else "verde"))
    p("=" * 78)
    p("")


def gerar_html(relatorio: Relatorio, scanner: Scanner) -> str:
    erros = [v for v in relatorio.violacoes if v.bloqueante]
    avisos = [v for v in relatorio.violacoes if not v.bloqueante]

    def linhas_violacoes(itens: list[Violacao]) -> str:
        if not itens:
            return "<p class='ok'>Nenhuma.</p>"
        partes = ["<table><tr><th>Regra</th><th>Arquivo</th><th>Detalhe</th></tr>"]
        for v in itens:
            partes.append(
                f"<tr><td><code>{html.escape(v.regra)}</code><br>"
                f"<small>{html.escape(v.titulo)}</small></td>"
                f"<td><code>{html.escape(v.arquivo)}:{v.linha}</code></td>"
                f"<td>{html.escape(v.detalhe)}"
                + (f"<br><small>{html.escape(v.explicacao)}</small>" if v.explicacao else "")
                + "</td></tr>"
            )
        partes.append("</table>")
        return "".join(partes)

    camadas_html = "".join(
        f"<tr><td>{html.escape(c['nome'])}</td>"
        f"<td>{relatorio.por_camada.get(c['id'], 0)}</td>"
        f"<td><small>{html.escape(c['descricao'])}</small></td></tr>"
        for c in scanner.camadas
    )

    return f"""<!doctype html>
<html lang="pt-BR"><head><meta charset="utf-8">
<title>Relatorio de arquitetura hexagonal</title>
<style>
 body{{font-family:system-ui,sans-serif;margin:2rem auto;max-width:60rem;line-height:1.5;color:#1a1a1a}}
 h1{{border-bottom:2px solid #333;padding-bottom:.3rem}}
 table{{border-collapse:collapse;width:100%;margin:1rem 0}}
 th,td{{border:1px solid #ddd;padding:.5rem;text-align:left;vertical-align:top}}
 th{{background:#f2f2f2}}
 code{{background:#f6f6f6;padding:.1rem .3rem;border-radius:3px;font-size:.9em}}
 .ok{{color:#1a7f37;font-weight:600}}
 .erro{{color:#b3261e}} .aviso{{color:#8a6d00}}
 .cartao{{display:inline-block;border:1px solid #ddd;border-radius:6px;padding:1rem 1.5rem;margin-right:1rem}}
 .numero{{font-size:2rem;font-weight:700;display:block}}
</style></head><body>
<h1>Relatorio de arquitetura hexagonal</h1>
<div>
 <span class="cartao"><span class="numero">{relatorio.arquivos}</span>arquivos</span>
 <span class="cartao erro"><span class="numero">{len(erros)}</span>erros</span>
 <span class="cartao aviso"><span class="numero">{len(avisos)}</span>avisos</span>
</div>
<h2>Camadas</h2>
<table><tr><th>Camada</th><th>Arquivos</th><th>Responsabilidade</th></tr>{camadas_html}</table>
<h2 class="erro">Erros</h2>{linhas_violacoes(erros)}
<h2 class="aviso">Avisos</h2>{linhas_violacoes(avisos)}
</body></html>"""


# --------------------------------------------------------------------------- #
# CLI
# --------------------------------------------------------------------------- #


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Verifica se o projeto respeita a arquitetura hexagonal.")
    parser.add_argument("--config", default=None,
                        help="arquivo de regras (padrao: tools/arch_rules.json)")
    parser.add_argument("--raiz", default=None,
                        help="raiz do projeto (padrao: diretorio acima de tools/)")
    parser.add_argument("--json", metavar="ARQUIVO", help="grava o relatorio em JSON")
    parser.add_argument("--html", metavar="ARQUIVO", help="grava o relatorio em HTML")
    parser.add_argument("--strict", action="store_true",
                        help="tratar avisos como erros")
    parser.add_argument("--quiet", action="store_true",
                        help="nao imprimir o relatorio em texto")
    parser.add_argument("--no-color", action="store_true", help="desliga as cores ANSI")
    args = parser.parse_args()

    aqui = Path(__file__).resolve().parent
    raiz = Path(args.raiz).resolve() if args.raiz else aqui.parent
    config_path = Path(args.config) if args.config else aqui / "arch_rules.json"

    if not config_path.is_file():
        print(f"erro: arquivo de regras nao encontrado: {config_path}", file=sys.stderr)
        return 2

    config = json.loads(config_path.read_text(encoding="utf-8"))
    scanner = Scanner(config, raiz)
    relatorio = scanner.escanear()

    usar_cor = not args.no_color and sys.stdout.isatty() and os.environ.get("TERM") != "dumb"
    if not args.quiet:
        imprimir_texto(relatorio, scanner, usar_cor)

    if args.json:
        Path(args.json).write_text(json.dumps({
            "arquivos": relatorio.arquivos,
            "por_camada": relatorio.por_camada,
            "matriz": relatorio.matriz,
            "sem_camada": relatorio.sem_camada,
            "violacoes": [asdict(v) for v in relatorio.violacoes],
        }, indent=2, ensure_ascii=False), encoding="utf-8")
        print(f"JSON gravado em {args.json}")

    if args.html:
        Path(args.html).write_text(gerar_html(relatorio, scanner), encoding="utf-8")
        print(f"HTML gravado em {args.html}")

    bloqueantes = [v for v in relatorio.violacoes
                   if v.bloqueante or (args.strict and not v.bloqueante)]
    return 1 if bloqueantes else 0


if __name__ == "__main__":
    sys.exit(main())
