package br.ucdb.pos.engenhariasoftware.testesoftware.automacao.selenium.webdriver.pageobject;

public enum TipoLancamento {
    ENTRADA("Entrada"),
    SAIDA("Saída");

    private TipoLancamento(final String descricao){
        this.descricao = descricao;
    }

    private String descricao;

    public String getDescricao() {
        return descricao;
    }
}
