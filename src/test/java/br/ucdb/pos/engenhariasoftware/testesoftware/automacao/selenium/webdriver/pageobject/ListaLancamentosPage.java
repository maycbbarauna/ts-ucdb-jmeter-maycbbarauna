package br.ucdb.pos.engenhariasoftware.testesoftware.automacao.selenium.webdriver.pageobject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ListaLancamentosPage {

    private WebDriver driver;

    public ListaLancamentosPage(final WebDriver driver){
        this.driver = driver;
    }

    public void acessa(){
        driver.get("http://localhost:8080/lancamentos");
    }

    public void novoLancamento(){
        driver.findElement(By.id("novoLancamento")).click();
    }

    public boolean existeLancamento(final String descricaoLancamento, final BigDecimal valorLancamento,
                                    LocalDateTime dataHora, TipoLancamento tipo, String categoria){

        DateTimeFormatter formatoDataLancamento = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String lancamentos = driver.getPageSource();
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
        return (lancamentos.contains(descricaoLancamento) &&
                lancamentos.contains(df.format(valorLancamento)) &&
                lancamentos.contains(dataHora.format(formatoDataLancamento)) &&
                lancamentos.contains(tipo.getDescricao()) &&
        lancamentos.contains(categoria));
    }
}

