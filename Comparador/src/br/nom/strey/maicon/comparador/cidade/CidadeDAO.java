package br.nom.strey.maicon.comparador.cidade;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;
import br.nom.strey.maicon.comparador.DBHelper;

public class CidadeDAO {
	public CidadeDAO(Context ctx) {
		// TODO Auto-generated constructor stub
		this.ctx = ctx;
	}
	
	public boolean insert(CidadeVO vo){
		SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();
		ContentValues ctvo = new ContentValues();
		ctvo.put("ibge", vo.getIbge());
		ctvo.put("nome", vo.getNome());
		ctvo.put("uf", vo.getUF());
		ctvo.put("favorita", vo.getFavorita());
		
		return (db.insert(TABLE_NAME, null, ctvo) > 0);
	}
	
	public boolean delete(CidadeVO vo){
		SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();
		return (db.delete(TABLE_NAME, "ibge=?", new String[]{vo.getIbge()+""}) > 0);
	}

	public boolean deleteAll(){
		SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();
		return (db.delete(TABLE_NAME, null, null) > 0);
	}
	
	public boolean update(CidadeVO vo){
		SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();
		ContentValues ctv = new ContentValues();
		ctv.put("ibge", vo.getIbge());
		ctv.put("nome", vo.getNome());
		ctv.put("uf", vo.getUF());
		ctv.put("favorita", vo.getFavorita());
		
		return (db.update(TABLE_NAME, ctv, "ibge=?", new String[]{vo.getIbge() + ""}) > 0);
	}
	
	public boolean setFavorita(CidadeVO vo){
		SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();
		ContentValues ctv = new ContentValues();
		Boolean ret;
		if (vo.getIbge() != 0){
			if (vo.getFavorita()){
				ctv.put("favorita", 0);
			}else{
				ctv.put("favorita", 1);
			}		
			ret = db.update(TABLE_NAME, ctv, "ibge=?", new String[]{vo.getIbge() + ""}) > 0;
			db.close();
		} else {
			Toast.makeText(ctx, "Busque por uma cidade.", 5).show();
			ret = true;
		}
		
		return (ret);
	}

	public List<CidadeVO> getAll(String filtroCidade){
		SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();
		List<CidadeVO> lista_cidades = new ArrayList<CidadeVO>();
		Cursor c = db.query(TABLE_NAME, 						// table
							COLUNAS, 							// columns
							"nome like '%"+filtroCidade+"%'",	// selection (where)
							null, 								// selection args
							null, 								// group by 
							null, 								// having
							"favorita desc, nome",				// sort order
							null); 								// limit
		
		while(c.moveToNext()){
			CidadeVO cidade_vo = new CidadeVO();
			/*
			cidade_vo.setIbge(c.getInt(c.getColumnIndex("ibge")));
			cidade_vo.setNome(c.getString(c.getColumnIndex("nome")));
			cidade_vo.setUF(c.getString(c.getColumnIndex("uf")));
			cidade_vo.setFavorita(c.getInt(c.getColumnIndex("favorita")));
			*/
			cidade_vo = get(c.getInt(c.getColumnIndex("ibge")));
			lista_cidades.add(cidade_vo);
		}
		c.close();
		db.close();
		
		return lista_cidades;
	}

	public List<CidadeVO> getAll(){
		SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();
		List<CidadeVO> lista_cidades = new ArrayList<CidadeVO>();
		Cursor c = db.query(TABLE_NAME, 
							COLUNAS, 
							"favorita = 1",
							null, 
							null, 
							null, 
							"favorita desc");
		
		while(c.moveToNext()){
			CidadeVO cidade_vo = new CidadeVO();
			cidade_vo = get(c.getInt(c.getColumnIndex("ibge")));
			/*
			cidade_vo.setIbge(c.getInt(c.getColumnIndex("ibge")));
			cidade_vo.setNome(c.getString(c.getColumnIndex("nome")));
			cidade_vo.setUF(c.getString(c.getColumnIndex("uf")));
			cidade_vo.setFavorita(c.getInt(c.getColumnIndex("favorita")));
			*/
			lista_cidades.add(cidade_vo);
		}
	
		c.close();
		db.close();
		
		return lista_cidades;
	}

	public CidadeVO get(Integer ibge){
		SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();		
		Cursor c = db.query(TABLE_NAME, 
							COLUNAS, 
							"ibge = "+ibge, 
							null, 
							null, 
							null, 
							null);
		
		c.moveToFirst();
		
		CidadeVO city = new CidadeVO();
		city.setIbge(c.getInt(c.getColumnIndex("ibge")));
		city.setNome(c.getString(c.getColumnIndex("nome")));
		city.setUF(c.getString(c.getColumnIndex("uf")));
		city.setFavorita(c.getInt(c.getColumnIndex("favorita")));

		c.close();
		db.close();
		
		return city;
	}
	
	private Context ctx;
	private static final String TABLE_NAME = "cidades";
	private static final String[] COLUNAS = {"ibge","nome","uf", "Favorita"};
}
