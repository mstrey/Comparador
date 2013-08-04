package br.nom.strey.maicon.comparador.cidade;

public class CidadeVO {
	
	public int getIbge() {
		return cidade_id;
	}
	public void setIbge(int id) {
		this.cidade_id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getUF() {
		return uf;
	}
	public void setUF(String uf) {
		this.uf = uf;
	}
	public Boolean getFavorita() {
		Boolean fav;
		if (this.favorita == 1){
			fav = true;
		} else {
			fav = false;
		}
		
		return fav;
		
	}
	
	public void setFavorita(int fav) {
		this.favorita = fav;
	}
	
	private int cidade_id;
	private String nome;
	private String uf;
	private int favorita;
}
