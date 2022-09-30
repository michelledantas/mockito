# Mocks em Java com Mockito
Aprendendo a usar Mockito com o curso do Alura


Em alguns casos, as classes de um projeto dependem de outras classes que executam algum serviço externo, como, por exemplo, acessar um banco de dados. Às vezes, queremos testar apenas a lógica daquela classe de maneira isolada, mas pelo fato de ela depender de outra classe, não é possível isolar essas dependências e testá-la individualmente. Podemos solucionar esse problema fazendo a integração ou usando um Mock, que é o objetivo deste estudo. Foi utilizado o Mockito, a biblioteca mais conhecida para fazer Mocks em Java.

## Mas o que são Mocks?
De maneira resumida, o Mock é uma classe que simula os comportamentos de outra classe. Ele serve para cenários em que queremos testar as lógicas e os algoritmos de uma classe que tem dependência de outra classe, mas isolando essas dependências.

Com os Mocks, conseguimos escrever um teste de unidade em vez de ter que usar um teste de integração, ou seja, que vai se integrar às dependências.

Como exemplo, considere a classe LeilaoDao, que estará no nosso projeto. Ela segue o padrão Objeto de Acesso a Dados (ou Data Access Object, em inglês), ou seja, é um objeto que isola o acesso a um banco de dados. Ela utiliza JPA, tem um EntityManager e uma série de métodos para salvar e buscar por ID. Confira o código dessa classe abaixo:

@Repository
public class LeilaoDao {

    private EntityManager em;

    @Autowired
    public LeilaoDao(EntityManager em) {
            this.em = em;
    }

    public Leilao salvar(Leilao leilao) {
        return em.merge(leilao);
    }

    public Leilao buscarPorId(Long id) {
            return em.find(Leilao.class, id);
    }

Porém, se chamarmos esses métodos, eles vão precisar do Entity Manager e terão que acessar o banco de dados. Então, se quisermos testar uma classe que depende da LeilaoDao, teremos que passar esta classe como parâmetro. Com isso, sempre que chamarmos qualquer método da classe, ele utilizará o LeilaoDao que, por sua vez, vai chamar o banco de dados.

Isso resultaria em um teste de integração, uma vez que estamos testando algo que se integra a um banco de dados. Para não ser necessário chamar o banco de dados, precisamos simular essa classe DAO.

Pense nessa simulação como um dublê de cinema: uma pessoa que faz uma cena perigosa no lugar do ator ou atriz do filme. A ideia é a mesma: não queremos testar a classe DAO verdadeira que acessa o banco de dados real, mas criar um LeilaoDaoFake, como no exemplo a seguir:

public class LeilaoDaoFake {

    private Long id = 1l;
    private List<Leilao> leiloes = new ArrayList<>();

    public Leilao salvar(Leilao leilao) {
            leilao.setId(id++);
            leiloes.add(leilao);
            return leilao;
    }

    public Leilao buscarPorId(Long id) {
            return leiloes.stream()
                            .filter(l -> l.getId().equals(id))
                            .findFirst().orElse(null);
    }

Essa classe seria um Mock, um dublê, que simula os comportamentos da classe LeilaoDao original. Ela não tem as anotações do Spring nem a dependência do EntityManager. Os métodos da LeilaoDaoFake não vão acessar o banco de dados, mas simplesmente fazer toda a simulação em memória. Assim, temos uma lista com um atributo e os métodos salvar e buscarPorId sempre vão manipular essa lista.

Para melhorar esse código, poderíamos fazer a classe LeilaoDaoFake herdar da classe LeilaoDao original e simplesmente sobrescrever os métodos sem alterar nada ou guardar o parâmetro de algum método chamado num determinado momento, por exemplo. Esse é só um exemplo do que seria uma classe Mock.

Porém, perceba que temos um problema: se tivermos que criar uma classe "dublê" para cada classe a ser testada, daria muito trabalho. Num contexto em que existam 30 classes DAO, teríamos que criar outras 30 classes Mock para simular o comportamento de cada uma delas. Isso significaria escrever e fazer a manutenção de mais códigos.

Em vez disso, queremos escrever o nosso teste de unidade independentemente das outras classes Mock. É para isso que existem as bibliotecas de Mock: para não termos que dedicar tanto trabalho escrevendo as classes Mock uma a uma. Em vez disso, a biblioteca usa algum recurso próprio para criar essa classe dinamicamente e simula as dependências da classe que queremos testar.

## Qual a vantagem de se utilizar mocks ao escrever testes de unidade?
Mocks possuem o objetivo de simular comportamentos das dependências de uma classe, para que os testes de unidade não se tornem testes de integração.
