package org.acelera.brasil;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.acelera.brasil.futebol.FutebolService;
import org.acelera.brasil.futebol.Jogador;
import org.acelera.brasil.futebol.Posicao;
import org.acelera.brasil.futebol.Time;

import org.acelera.brasil.futebol.exception.JogadorNaoEncontradoException;
import org.acelera.brasil.futebol.exception.TimeNaoEncontradoException;
import org.acelera.brasil.futebol.exception.TimeNaoPossuiJogadoresException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

/**
 * Classe responsável pelo teste de {@link FutebolService}.
 * 
 * @author edmilson.santana
 *
 */
@RunWith(JUnitPlatform.class)
public class FutebolTest {

	private FutebolService service;

	@BeforeEach
	public void setUp() {
		this.service = new FutebolService();
		this.adicionarTime("A", 0, Posicao.ATAQUE);
		this.adicionarTime("B", 10, Posicao.DEFESA);
		this.adicionarTime("C", 5, Posicao.GOLEIRO);
		this.adicionarTime("D", 3, Posicao.ATAQUE);
		this.adicionarTime("E", 20, Posicao.DEFESA);
	}

	@Test
	public void deveBuscarJogadoresComDireitoAoGolNoFantastico() {
		List<Jogador> jogadores = this.service.buscarJogadoresComDireitoAoGolNoFantastico();

		assertEquals(4, jogadores.size());
	}

	@Test
	public void deveBuscarArtilheiroDoTime() {
		Time time = new Time("Time F");

		time.adicionar(new Jogador.Builder().withNome("Jogador FA").withCidade("Cidade A").withPais("Brasil")
				.withGols(10).withPosicao(Posicao.ATAQUE).build());
		time.adicionar(new Jogador.Builder().withNome("Jogador FB").withCidade("Cidade A").withPais("Brasil")
				.withGols(5).withPosicao(Posicao.DEFESA).build());
		time.adicionar(new Jogador.Builder().withNome("Jogador FC").withCidade("Cidade A").withPais("Brasil")
				.withGols(2).withPosicao(Posicao.GOLEIRO).build());
		this.service.adicionarTime(time);

		Jogador artilheiro = this.service.buscarArtilheiroTime("Time F");

		assertEquals("Jogador FA", artilheiro.getNome());
		assertEquals(10, artilheiro.getGols());
	}

	@Test
	public void deveOcorrerErroAoBuscarArtilheiroDoTimeSemJogadores() {
		String nomeTime = "Time F";
		Time time = new Time(nomeTime);

		this.service.adicionarTime(time);

		assertThrows(TimeNaoPossuiJogadoresException.class, () -> this.service.buscarArtilheiroTime(nomeTime));
	}

	@Test
	public void deveBuscaJogadoresOrdenadosPeloNumeroDeGols() {
		List<Jogador> jogadores = this.service.buscarJogadoresOrdenadosPorNumeroDeGols();

		assertEquals(Arrays.asList("Jogador AA", "Jogador DA", "Jogador CA", "Jogador BA", "Jogador EA"),
				this.service.getNomesDeJogadores(jogadores));
	}

	@Test
	public void deveAgruparJogadoresPorPosicao() {
		Map<Posicao, List<Jogador>> jogadoresPorPosicao = this.service.agruparJogadoresPorPosicao();

		List<Jogador> jogadoresNoAtaque = jogadoresPorPosicao.get(Posicao.ATAQUE);
		List<Jogador> jogadoresNaDefesa = jogadoresPorPosicao.get(Posicao.DEFESA);
		List<Jogador> goleiros = jogadoresPorPosicao.get(Posicao.GOLEIRO);

		assertTrue(Arrays.asList("Jogador AA", "Jogador DA")
				.containsAll(this.service.getNomesDeJogadores(jogadoresNoAtaque)));
		assertTrue(Arrays.asList("Jogador EA", "Jogador BA")
				.containsAll(this.service.getNomesDeJogadores(jogadoresNaDefesa)));
		assertTrue(Arrays.asList("Jogador CA").containsAll(this.service.getNomesDeJogadores(goleiros)));
	}

	@Test
	public void deveOcorrerErroAoCriarJogadorComNumeroDeGolsNegativo() {

		assertThrows(IllegalArgumentException.class, () -> new Jogador.Builder().withNome("Jogador FA")
				.withCidade("Cidade A").withPais("Brasil").withGols(-2).withPosicao(Posicao.ATAQUE).build());
	}

	@Test
	private void adicionarTime(String nome, Integer gols, Posicao posicao) {
		Time time = new Time(String.format("Time %s", nome));

		time.adicionar(
				new Jogador.Builder().withNome(String.format("Jogador %sA", nome)).withCidade("Cidade A")
				.withPais("Brasil").withGols(gols).withPosicao(posicao).build()
		);

		this.service.adicionarTime(time);
	}

	@Test
	public void deveOcorrerErroAoAdicionarUmTimeNullo() {  
		assertThrows(TimeNaoEncontradoException.class, () -> this.service.adicionarTime(null));
	}

	@Test
	public void deveOcorrerErroAoAdicionarJogadorNullo() {
    Time time = new Time("Time J");
		assertThrows(JogadorNaoEncontradoException.class, () -> time.adicionar(null));
	}

	@Test
	public void deveOcorrerErroAoCriarJogadorSemNome() {

		assertThrows(NullPointerException.class, () -> {
			 new Jogador.Builder()
					.withCidade("Cidade A")
					.withPais("Brasil")
					.withGols(10)
					.withPosicao(Posicao.ATAQUE)
					.build();

		});
	}

	@Test
	public void deveOcorrerErroAoCriarJogadorSemCidade() {
		assertThrows(NullPointerException.class, () -> {
			new Jogador.Builder()
					.withNome("Jogador FA")
					.withPais("Brasil")
					.withGols(10)
					.withPosicao(Posicao.ATAQUE)
					.build();


		});
	}

	@Test
	public void deveOcorrerErroAoCriarJogadorSemPais() {
		assertThrows(NullPointerException.class, () -> {
			new Jogador.Builder()
					.withNome("Jogador FA")
					.withPais("Brasil")
					.withGols(10)
					.withPosicao(Posicao.ATAQUE)
					.build();


		});
	}
	@Test
	public void deveOcorrerErroAoCriarJogadorSemGols() {
		assertThrows(NullPointerException.class, () -> {
			new Jogador.Builder()
					.withNome("Jogador FA")
					.withCidade("Cidade A")
					.withPais("Brasil")
					.withPosicao(Posicao.ATAQUE)
					.build();


		});
	}
	@Test
	public void deveOcorrerErroAoCriarJogadorSemPosicao() {
		assertThrows(NullPointerException.class, () -> {
			new Jogador.Builder()
					.withNome("Jogador FA")
					.withCidade("Cidade A")
					.withPais("Brasil")
					.withGols(10)
					.build();

		});
	}
}
