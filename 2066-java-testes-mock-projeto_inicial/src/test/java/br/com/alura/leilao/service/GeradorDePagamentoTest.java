package br.com.alura.leilao.service;

import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Pagamento;
import br.com.alura.leilao.model.Usuario;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.*;

class GeradorDePagamentoTest {

    private GeradorDePagamento gerador;

    @Mock
    private PagamentoDao pagamentoDao;

    //sempre que quiser capturar um objeto que foi passado por um método de um mock, é só usar o captor
    @Captor
    private ArgumentCaptor<Pagamento> captor;

    @Mock
    private Clock clock;

    @BeforeEach
    public void beforeEach(){
        MockitoAnnotations.initMocks(this);
        this.gerador = new GeradorDePagamento(pagamentoDao, clock);
    }


    @Test
    void deveriaCriarPagamentoParaVencedorDoLeilao() {
        Leilao leilao = leilao();
        Lance vencedor = leilao.getLanceVencedor();


        LocalDate data = LocalDate.of(2022,10,3);
        Instant instant = data.atStartOfDay(ZoneId.systemDefault()).toInstant();

        Mockito.when(clock.instant()).thenReturn(instant);
        Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        gerador.gerarPagamento(vencedor);
        Mockito.verify(pagamentoDao).salvar(captor.capture());
        Pagamento pagamento = captor.getValue();

        Assert.assertEquals(data.plusDays(1), pagamento.getVencimento());
        Assert.assertEquals(vencedor.getValor(), pagamento.getValor());
        Assert.assertFalse(pagamento.getPago());
        Assert.assertEquals(vencedor.getUsuario(),pagamento.getUsuario());
        Assert.assertEquals(leilao,pagamento.getLeilao());
    }

    private Leilao leilao() {
        Leilao leilao = new Leilao("Celular",
                new BigDecimal("500"),
                new Usuario("Fulano"));

        Lance lance = new Lance(new Usuario("Ciclano"),
                new BigDecimal("900"));

        leilao.propoe(lance);
        leilao.setLanceVencedor(lance);
        return leilao;

    }
}