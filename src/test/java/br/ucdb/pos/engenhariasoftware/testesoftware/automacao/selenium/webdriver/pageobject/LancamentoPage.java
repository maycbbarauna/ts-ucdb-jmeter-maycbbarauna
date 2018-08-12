package br.ucdb.pos.engenhariasoftware.testesoftware.automacao.selenium.webdriver.pageobject;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LancamentoPage {

    private WebDriver driver;

    public LancamentoPage(final WebDriver driver){
        this.driver = driver;
    }

    public void cria(final String descricaoLancamento, final BigDecimal valorLancamento,
                     LocalDateTime dataHora, TipoLancamento tipo, String categoria){

        if(tipo == TipoLancamento.SAIDA) {
            driver.findElement(By.id("tipoLancamento2")).click(); // informa lançamento: SAÍDA
        }else{
            driver.findElement(By.id("tipoLancamento1")).click(); // informa lançamento: ENTRADA
        }

        WebElement descricao = driver.findElement(By.id("descricao"));
        descricao.click();
        descricao.clear();
        descricao.sendKeys(descricaoLancamento);

        DateTimeFormatter formatoDataLancamento = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        WebElement dataLancamento = driver.findElement(By.name("dataLancamento"));
        dataLancamento.clear();
        dataLancamento.sendKeys(dataHora.format(formatoDataLancamento));
        dataLancamento.sendKeys(Keys.TAB);

        WebElement valor = driver.findElement(By.id("valor"));
        valor.clear();
        valor.sendKeys(String.valueOf(valorLancamento));

        WebElement categoriaWeb = driver.findElement(By.id("categoria"));
        Select selectCategoria = new Select(categoriaWeb);
        selectCategoria.selectByValue(categoria);

        driver.findElement(By.id("btnSalvar")).click();
    }
}


