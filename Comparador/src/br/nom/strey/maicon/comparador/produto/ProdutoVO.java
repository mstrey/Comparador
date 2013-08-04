package br.nom.strey.maicon.comparador.produto;

import java.io.File;
import java.math.BigInteger;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;


public class ProdutoVO {
	
	private BigInteger produto_id;
	private Integer loja_id;
	private String descricao;
	private Double preco = 0.00;
	private Date data;
	private Integer favorito;

	public BigInteger getId() {
		return produto_id;
	}
	public void setId(BigInteger id) {
		this.produto_id = id;
	}

	public Integer getLoja() {
		return loja_id;
	}
	public void setLoja(int loja_id) {
		this.loja_id = loja_id;
	}

	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public Double getPreco() {
		
		return preco;
	}
	public void setPreco(Double preco) {
		this.preco = preco;
	}
	
	public Date getDataConfirmacao() {
		return data;
	}
	public void setDataConfirmacao(Date data) {
		this.data = data;
	}

	public Bitmap getFoto(){
		String local_foto = Environment.getExternalStorageDirectory() + "/Comparador/produto/"+produto_id+".png";
		File fileFoto = new File(local_foto); 
		if (fileFoto.exists()){
			Bitmap bitmap = BitmapFactory.decodeFile(local_foto);
			return (bitmap);
        } else {
			return null;
        }
	}
	
	public Boolean getFavorito() {
		Boolean fav;
		if (this.favorito == 1){
			fav = true;
		} else {
			fav = false;
		}
		
		return fav;
		
	}
	
	public void setFavorito(Integer fav) {
		this.favorito = fav;
	}
		
}
