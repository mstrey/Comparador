package br.nom.strey.maicon.comparador.loja;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import br.nom.strey.maicon.comparador.DBHelper;

public class LojaDAO {
	
	private Context ctx;
	private Boolean result;
	private static final String TABLE_NAME = "lojas";
	private static final String[] COLUNAS = {	"loja_id", 
												"cidade_id", 
												"nome", 
												"local", 
												"favorita",  
												"comparar"};

	public LojaDAO(Context ctx) {
		// TODO Auto-generated constructor stub
		this.ctx = ctx;
	}
	
	public boolean insert(LojaVO vo){
        
		SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();
		ContentValues ctv = new ContentValues();
		ctv.put("loja_id", vo.getId());
		ctv.put("cidade_id", vo.getCidade());
		ctv.put("nome", vo.getNome());
		ctv.put("local", vo.getLocal());
		
		ctv.put("favorita", vo.getFavorita());
		ctv.put("comparar", vo.getComparar());

		result = db.insert(TABLE_NAME, null, ctv) > 0;
		db.close();
		
		return (result);
	}
	
	public boolean delete(LojaVO vo){
		SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();
		result = db.delete(TABLE_NAME, "loja_id=?", new String[]{vo.getId()+""}) > 0;
		db.close();
		
		return (result);
	}

	public boolean update(LojaVO vo){
        SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();
		ContentValues ctv = new ContentValues();
		ctv.put("loja_id", vo.getId());
		ctv.put("cidade_id", vo.getCidade());
		ctv.put("nome", vo.getNome());
		ctv.put("local", vo.getLocal());
		ctv.put("favorita", vo.getFavorita());
		ctv.put("comparar", vo.getComparar());
		
		result = db.update(TABLE_NAME, ctv, "loja_id=?", new String[]{vo.getId() + ""}) > 0;
		db.close();
		
		return (result);

	}
	
	public boolean setFavorita(LojaVO vo){
		SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();
		ContentValues ctv = new ContentValues();
		if (vo.getFavorita()){
			ctv.put("favorita", 0);
		}else{
			ctv.put("favorita", 1);
		}		
		result = db.update(TABLE_NAME, ctv, "loja_id=?", new String[]{vo.getId() + ""}) > 0;
		db.close();
		return (result);
	}

	public boolean setComparar(LojaVO vo){
		SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();
		ContentValues ctv = new ContentValues();
		if (vo.getComparar()){
			ctv.put("comparar", 0);
		}else{
			ctv.put("comparar", 1);
		}		
		result = db.update(TABLE_NAME, ctv, "loja_id=?", new String[]{vo.getId() + ""}) > 0;
		db.close();
		return (result);
	}

	public List<LojaVO> getAll(Integer ibge){
		SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();
		List<LojaVO> lista_lojas = new ArrayList<LojaVO>();
		Cursor c = db.query(TABLE_NAME, 
							COLUNAS, 
							"cidade_id = '"+ibge+"' ", 
							null, 
							null, 
							null, 
							"favorita desc, nome asc",
							null);
		while(c.moveToNext()){
			LojaVO loja_vo = new LojaVO();
			
			loja_vo = get(c.getInt(c.getColumnIndex("loja_id")));
			lista_lojas.add(loja_vo);
		}
	
		if (lista_lojas.isEmpty()) {
			lista_lojas = getVazio();
		}

		c.close();
		db.close();
		
		return lista_lojas;
	}

	public List<LojaVO> getVazio(){
		List<LojaVO> lista_lojas = new ArrayList<LojaVO>();

		LojaVO loja_vo = new LojaVO();
		loja_vo.setNome("CADASTRE UMA");
		loja_vo.setLocal("LOJA");
		loja_vo.setFavorita(0);
		loja_vo.setComparar(0);
		lista_lojas.add(loja_vo);
	
		return lista_lojas;
	}

	public List<LojaVO> getAll(Integer ibge, String busca){
		SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();
		List<LojaVO> lista_lojas = new ArrayList<LojaVO>();

		Cursor c = db.query(TABLE_NAME, 
							COLUNAS, 
							"cidade_id = "+ibge+" AND nome like '%"+busca+"%' ", 
							null, 
							null, 
							null, 
							"favorita desc, nome asc"); 
		// verificar necessidade de implementar limit com 20 lojas nos GetAll
		
		while(c.moveToNext()){
			LojaVO loja_vo = new LojaVO();
			
			loja_vo = get(c.getInt(c.getColumnIndex("loja_id")));
			lista_lojas.add(loja_vo);
		}
	
		if (lista_lojas.isEmpty()) {
			lista_lojas = getVazio();
		}

		c.close();
		db.close();
		
		return lista_lojas;
	}
	
	public LojaVO get(Integer loja){
		SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();		
		Cursor c = db.query(TABLE_NAME, 
							COLUNAS, 
							"loja_id = "+loja, 
							null, 
							null, 
							null, 
							null);
		
		c.moveToFirst();
		LojaVO loja_vo = new LojaVO();
		
		if (c.getCount() > 0){
			loja_vo.setId(c.getInt(c.getColumnIndex("loja_id")));
			loja_vo.setCidade(c.getInt(c.getColumnIndex("cidade_id")));
			loja_vo.setNome(c.getString(c.getColumnIndex("nome")));
			loja_vo.setLocal(c.getString(c.getColumnIndex("local")));
			loja_vo.setFavorita(c.getInt(c.getColumnIndex("favorita")));
			loja_vo.setComparar(c.getInt(c.getColumnIndex("comparar")));
		} else {
			loja_vo.setNome("Nenhuma loja localizado.");
			loja_vo.setFavorita(0);
		}
		c.close();
		db.close();
		
		return loja_vo;
	}

	public Boolean existe(Integer loja){
		Boolean result = false;
		SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();		
		Cursor c = db.query(TABLE_NAME, 
							COLUNAS, 
							"loja_id = "+loja, 
							null, 
							null, 
							null, 
							null);
		
		if (c.moveToNext()){
			result = true;
		}
		
		c.close();
		db.close();

		return result;
	}

	public String getLojasComparar(){
		SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();		
		Cursor c = db.rawQuery( " SELECT * "+
								" FROM "+TABLE_NAME+ 
								" WHERE comparar = 1 ",
								null);
		
		c.moveToFirst();
		
		String lojas = "";
		while(!c.isAfterLast()){
			lojas += c.getString(c.getColumnIndex("loja_id"))+",";
			c.moveToNext();
		}
		if (lojas.length() > 1){
			lojas = lojas.substring(0, lojas.length()-1);	
		} else {
			lojas = "";
		}
		
		c.close();
		db.close();
		
		return lojas;
	}

}
