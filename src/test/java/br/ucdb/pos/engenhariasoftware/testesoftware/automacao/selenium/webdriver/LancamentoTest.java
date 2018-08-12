package br.ucdb.pos.engenhariasoftware.testesoftware.automacao.selenium.webdriver;

import br.ucdb.pos.engenhariasoftware.testesoftware.automacao.selenium.webdriver.pageobject.LancamentoPage;
import br.ucdb.pos.engenhariasoftware.testesoftware.automacao.selenium.webdriver.pageobject.ListaLancamentosPage;
import br.ucdb.pos.engenhariasoftware.testesoftware.automacao.selenium.webdriver.pageobject.TipoLancamento;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class LancamentoTest {

    private WebDriver driver;
    private ListaLancamentosPage listaLancamentosPage;
    private LancamentoPage lancamentoPage;
    private String descricaoLancada;
    private BigDecimal valorLancamento;
    private LocalDateTime data;
    private TipoLancamento tipo;
    private String categoriaBusca;
    private String categoriaInclusao;
    private String descricaoEditada;

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
    public void testeCriaLancamento(){
        criaLancamento();
    }

    @Test
    public void testeEditaLancamento(){
        criaLancamento();
        editaLancamento();
    }

    @Test
    public void testeExcluirLancamento(){
        criaLancamento();
        editaLancamento();
        excluiLancamento();
    }

    @Test
    public void testeValidaTotais(){
        criaLancamento();
        validarTotalDeSaidaEEntrada();
        acessaRelatorio();
    }

    @Test
    public void testeValidaMensagens(){
        validaMensagem();
    }

    public void criaLancamento() {
        listaLancamentosPage.acessa();
        listaLancamentosPage.novoLancamento();

        LocalDateTime dataHoraBase = LocalDateTime.now();
        Integer dia = getDiadaDataLancamneto();
        LocalDateTime dataHora = LocalDateTime.of(dataHoraBase.getYear(), dataHoraBase.getMonth().getValue(), dia, 0, 0);
        final String descricaoLancamento = "Incluindo lancamento automatizado " + new Random().nextInt(999999999);
        final BigDecimal valor = getValorLancamento();
        final TipoLancamento tipolancamento = getTipoLancamento();
        final List<String> categoria = getCategoria();

        lancamentoPage.cria(descricaoLancamento, valor, dataHora, tipolancamento, categoria.get(0));
        WebElement descricao = driver.findElement(By.id("itemBusca"));
        descricao.click();
        descricao.sendKeys(descricaoLancamento);

        driver.findElement(By.id("bth-search")).click();

        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@id='tabelaLancamentos']/tbody/tr")));

        assertTrue(listaLancamentosPage.existeLancamento(descricaoLancamento, valor, dataHora, tipolancamento, categoria.get(1)));

        // Polula os objetos da classe
        this.descricaoLancada = descricaoLancamento;
        this.valorLancamento = valor;
        this.data = dataHora;
        this.tipo = tipolancamento;
        this.categoriaInclusao = categoria.get(0);
        this.categoriaBusca = categoria.get(1);

    }

    public void editaLancamento() {

        WebElement descricao = driver.findElement(By.id("itemBusca"));
        descricao.click();
        descricao.clear();
        descricao.sendKeys(this.descricaoLancada);

        driver.findElement(By.id("bth-search")).click();

        driver.findElement(By.xpath("//*[@id=\"tabelaLancamentos\"]/tbody/tr/td[6]/div/a[1]")).click();

        WebElement descricaoEditar = driver.findElement(By.id("descricao"));
        descricaoEditar.clear();
        descricaoEditar.click();
        this.descricaoEditada = this.descricaoLancada + " - Editada";
        descricaoEditar.sendKeys(descricaoEditada);

        lancamentoPage.cria(this.descricaoEditada, this.valorLancamento, this.data, this.tipo, this.categoriaInclusao);
        WebElement descricaoEditadaVerif = driver.findElement(By.id("itemBusca"));
        descricaoEditadaVerif.click();
        descricaoEditadaVerif.sendKeys(descricaoEditada);

        driver.findElement(By.id("bth-search")).click();

        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@id='tabelaLancamentos']/tbody/tr")));

        assertTrue(listaLancamentosPage.existeLancamento(this.descricaoEditada, this.valorLancamento, this.data, this.tipo, this.categoriaBusca));
    }

    public void excluiLancamento(){

        WebElement descricao = driver.findElement(By.id("itemBusca"));
        descricao.click();
        descricao.clear();
        descricao.sendKeys(this.descricaoEditada);

        driver.findElement(By.id("bth-search")).click();

        driver.findElement(By.xpath("//*[@id=\"tabelaLancamentos\"]/tbody/tr/td[6]/div/a[2]")).click();

        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@id='tabelaLancamentos']/tbody/tr")));

        assertFalse(listaLancamentosPage.existeLancamento(this.descricaoEditada, this.valorLancamento, this.data, this.tipo, this.categoriaBusca));

    }

    public void validarTotalDeSaidaEEntrada(){

        BigDecimal totalSaida = BigDecimal.ZERO;
        BigDecimal totalEntrada = BigDecimal.ZERO;

        WebElement tipo = driver.findElement(By.id("itemBusca"));
        tipo.click();
        tipo.clear();
        tipo.sendKeys("Saida");
        driver.findElement(By.id("bth-search")).click();

        WebElement tableSaida = driver.findElement(By.tagName("table"));
        List<WebElement> tbodySaidaVOs = tableSaida.findElements(By.tagName("tbody"));

        if(!tbodySaidaVOs.isEmpty()) {
            WebElement tbodySaida = driver.findElement(By.tagName("tbody"));
            List<WebElement> trSaidaVOs = tbodySaida.findElements(By.tagName("tr"));

            for (int i = 1; i <= trSaidaVOs.size(); i++) {
                String valorTexto = driver.findElement(By.xpath("//*[@id=\'tabelaLancamentos\']/tbody/tr[" + i + "]/td[4]")).getText();
                valorTexto = valorTexto.replace(",", ".");
                totalSaida = totalSaida.add(new BigDecimal(valorTexto));
            }
        }

        tipo.click();
        tipo.clear();
        tipo.sendKeys("Entrada");
        driver.findElement(By.id("bth-search")).click();

        WebElement tableEntrada = driver.findElement(By.tagName("table"));
        List<WebElement> tbodyEntradaVOs = tableEntrada.findElements(By.tagName("tbody"));

        if(!tbodyEntradaVOs.isEmpty()) {
            WebElement tbodyEntrada = driver.findElement(By.tagName("tbody"));
            List<WebElement> trEntradaVOs = tbodyEntrada.findElements(By.tagName("tr"));

            for (int i = 1; i <= trEntradaVOs.size(); i++) {
                String valorTexto = driver.findElement(By.xpath("//*[@id=\'tabelaLancamentos\']/tbody/tr[" + i + "]/td[4]")).getText();
                valorTexto = valorTexto.replace(",", ".");
                totalEntrada = totalEntrada.add(new BigDecimal(valorTexto));
            }
        }

        driver.findElement(By.id("recarregar")).click();

        BigDecimal valorTotalEntrada = BigDecimal.ZERO;
        BigDecimal valorTotalSaida = BigDecimal.ZERO;

        String valorTotalEntradaTexto = driver.findElement(By.xpath("//*[@id=\'tabelaLancamentos\']/tfoot/tr[2]/th/span")).getText();
        String valorTotalSaidaTexto = driver.findElement(By.xpath("//*[@id=\'tabelaLancamentos\']/tfoot/tr[1]/th/span")).getText();

        valorTotalEntradaTexto = valorTotalEntradaTexto.replace(".","");
        valorTotalEntradaTexto = valorTotalEntradaTexto.replace(",",".");

        valorTotalSaidaTexto = valorTotalSaidaTexto.replace(".","");
        valorTotalSaidaTexto = valorTotalSaidaTexto.replace(",",".");

        valorTotalEntrada = new BigDecimal(valorTotalEntradaTexto);
        valorTotalSaida = new BigDecimal(valorTotalSaidaTexto);

        assertTrue(totalSaida.compareTo(valorTotalSaida) == 0);
        assertTrue(totalEntrada.compareTo(valorTotalEntrada) == 0);

        driver.findElement(By.id("recarregar")).click();

    }

    public void acessaRelatorio(){

        WebElement relatorio = driver.findElement(By.xpath("//*[@id=\'form-busca\']/div[1]/div[2]/div[4]/a/i"));

        relatorio.click();
    }

    public void validaMensagem(){
        listaLancamentosPage.acessa();
        listaLancamentosPage.novoLancamento();

        driver.findElement(By.id("btnSalvar")).click();

        String mensagem1 = driver.findElement(By.xpath("/html/body/div/div/div[2]/div/div/form/div[1]/div/div[1]")).getText();
        assertEquals(mensagem1,"A data deve ser informada");

        String mensagem2 = driver.findElement(By.xpath("/html/body/div/div/div[2]/div/div/form/div[1]/div/div[2]")).getText();
        assertEquals(mensagem2,"O valor deve ser informado");

        String mensagem3 = driver.findElement(By.xpath("/html/body/div/div/div[2]/div/div/form/div[1]/div/div[3]")).getText();
        assertEquals(mensagem3,"A descrição deve ser informada");

        String mensagem4 = driver.findElement(By.xpath("/html/body/div/div/div[2]/div/div/form/div[1]/div/div[4]")).getText();
        assertEquals(mensagem4,"A categoria deve ser informada");

        driver.findElement(By.id("cancelar")).click();

        driver.findElement(By.id("recarregar")).click();


    }

    @AfterClass
    private void finaliza() {
        driver.quit();
    }

    private BigDecimal getValorLancamento() {

        boolean aplicaVariante = (System.currentTimeMillis() % 3) == 0;
        int fator = 10;
        long mim = 30;
        long max = 900;
        if (aplicaVariante) {
            mim /= fator;
            max /= fator;
        }
        return new BigDecimal((1 + (Math.random() * (max - mim)))).setScale(2, RoundingMode.HALF_DOWN);
    }

    private Integer getDiadaDataLancamneto() {

        Random dia = new Random();

        return dia.nextInt(27) + 1;

    }

    private TipoLancamento getTipoLancamento() {
        boolean retorno;

        Random random = new Random();
        retorno = random.nextBoolean();

        if (retorno)
            return TipoLancamento.ENTRADA;

        return TipoLancamento.SAIDA;
    }

    private List<String> getCategoria() {
        Integer retorno;

        Random random = new Random();
        retorno = random.nextInt(7);
        List<String> categoriaRetorno = new ArrayList<>();

        if (retorno == 0) {
            categoriaRetorno.add("ALIMENTACAO");
            categoriaRetorno.add("Alimentação");
        } else if (retorno == 1) {
            categoriaRetorno.add("SALARIO");
            categoriaRetorno.add("Salário");
        } else if (retorno == 2){
            categoriaRetorno.add("LAZER");
            categoriaRetorno.add("Lazer");
        }else if(retorno ==3){
            categoriaRetorno.add("TELEFONE_INTERNET");
            categoriaRetorno.add("Telefone & Internet");
        }else if (retorno == 4){
            categoriaRetorno.add("CARRO");
            categoriaRetorno.add("Carro");
        }else if (retorno == 5){
            categoriaRetorno.add("EMPRESTIMO");
            categoriaRetorno.add("Empréstimo");
        }else if (retorno == 6){
            categoriaRetorno.add("INVESTIMENTOS");
            categoriaRetorno.add("Investimentos");
        }else{
            categoriaRetorno.add("OUTROS");
            categoriaRetorno.add("Outros");
        }

        return categoriaRetorno;
    }

}


