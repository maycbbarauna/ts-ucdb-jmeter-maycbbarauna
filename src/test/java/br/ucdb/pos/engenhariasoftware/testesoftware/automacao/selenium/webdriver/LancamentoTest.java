package br.ucdb.pos.engenhariasoftware.testesoftware.automacao.selenium.webdriver;

import br.ucdb.pos.engenhariasoftware.testesoftware.automacao.selenium.webdriver.pageobject.LancamentoPage;
import br.ucdb.pos.engenhariasoftware.testesoftware.automacao.selenium.webdriver.pageobject.ListaLancamentosPage;
import br.ucdb.pos.engenhariasoftware.testesoftware.automacao.selenium.webdriver.pageobject.TipoLancamento;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.testng.Assert.assertTrue;

public class LancamentoTest {

    private WebDriver driver;
    private ListaLancamentosPage listaLancamentosPage;
    private LancamentoPage lancamentoPage;

    @BeforeClass
    private void inicialliza() {
        boolean windows = System.getProperty("os.name").toUpperCase().contains("WIN");
        System.setProperty("webdriver.gecko.driver",
                System.getProperty("user.dir") + "/src/test/resources/drivers/" +
                        "/geckodriver" + (windows ? ".exe" : ""));
        driver = new FirefoxDriver();
        listaLancamentosPage = new ListaLancamentosPage(driver);
        lancamentoPage = new LancamentoPage(driver);
    }

    @Test
    public void criaLancamento(){
        listaLancamentosPage.acessa();
        listaLancamentosPage.novoLancamento();

        LocalDateTime dataHora = LocalDateTime.now();
        DateTimeFormatter formatoLancamento = DateTimeFormatter.ofPattern("dd.MM.yy");
        final String descricaoLancamento = "Lançando saída automatizada " + dataHora.format(formatoLancamento);
        final BigDecimal valor = getValorLancamento();
        lancamentoPage.cria(descricaoLancamento, valor, dataHora, TipoLancamento.SAIDA);

        assertTrue(listaLancamentosPage.existeLancamento(descricaoLancamento, valor, dataHora, TipoLancamento.SAIDA));
    }

    @AfterClass
    private void finaliza(){
        driver.quit();
    }

    private BigDecimal getValorLancamento() {

        boolean  aplicaVariante = (System.currentTimeMillis() % 3) == 0;
        int fator = 10;
        long mim = 30;
        long max = 900;
        if(aplicaVariante){
            mim /= fator;
            max /= fator;
        }
        return new BigDecimal(( 1 + (Math.random() * (max - mim)))).setScale(2, RoundingMode.HALF_DOWN);
    }

    /*
    @Test
    public void ciraLancamento(){

        WebDriver driver = inicialliza();
        //a inicialização é a mesma do exemplo anteriror
        driver.get("http://localhost:8080/lancamentos");

        driver.findElement(By.id("novoLancamento")).click();

        driver.findElement(By.id("tipoLancamento2")).click(); // informa lançamento: SAÍDA
        LocalDateTime dataHora = LocalDateTime.now();
        DateTimeFormatter formatoLancamento = DateTimeFormatter.ofPattern("dd.MM.yy");
        WebElement descricao = driver.findElement(By.id("descricao"));
        descricao.click();
        final String descricaoLancamento = "Lançando saída automatizada " + dataHora.format(formatoLancamento);
        descricao.sendKeys(descricaoLancamento);

        DateTimeFormatter formatoDataLancamento = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        WebElement dataLancamento = driver.findElement(By.name("dataLancamento"));
        dataLancamento.sendKeys(dataHora.format(formatoDataLancamento));

        BigDecimal valorLancamento = getValorLancamento();
        WebElement valor = driver.findElement(By.id("valor"));
        driver.findElement(By.id("tipoLancamento2")).click();
        valor.sendKeys(String.valueOf(valorLancamento));
        driver.findElement(By.id("btnSalvar")).click();

        //verifica se o lancamento foi cadastrado
        //String lancamentos = driver.findElement(By.id("tabelaLancamentos")).getAttribute("innerHTML");
        String lancamentos = driver.getPageSource();
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
        assertTrue(lancamentos.contains(descricaoLancamento) &&
                lancamentos.contains(df.format(valorLancamento)) &&
                lancamentos.contains(dataHora.format(formatoDataLancamento)) &&
                lancamentos.contains("Saída"));

        driver.quit();
    }
     */
}


