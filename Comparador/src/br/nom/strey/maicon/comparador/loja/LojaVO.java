package br.nom.strey.maicon.comparador.loja;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;



public class LojaVO {
	
	private Integer loja_id;
	private Integer cidade_id;
	private String nome;
	private String local;
	private Integer favorita;
	private Integer comparar;

	public Integer getId() {
		return loja_id;
	}
	public void setId(Integer id) {
		this.loja_id = id;
	}

	public Integer getCidade() {
		return cidade_id;
	}
	public void setCidade(Integer id) {
		this.cidade_id = id;
	}

	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	
	public Bitmap getFoto(){
		String local_foto = Environment.getExternalStorageDirectory() + "/Comparador/loja/"+loja_id+".png";
		File fileFoto = new File(local_foto); 
		if (fileFoto.exists()){
			Bitmap bitmap = BitmapFactory.decodeFile(local_foto);
			return (bitmap);
        } else {
			return null;
        }
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
	
	public void setFavorita(Integer fav) {
		this.favorita = fav;
	}
	
	public Boolean getComparar() {
		Boolean comp;
		if (this.comparar == 1){
			comp = true;
		} else {
			comp = false;
		}
		
		return comp;
		
	}
	
	public void setComparar(Integer comp) {
		this.comparar = comp;
	}
	
}
