package br.nom.strey.maicon.comparador.produto;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;
import br.nom.strey.maicon.comparador.DBHelper;

public class AlertDAO {
	
	private Context ctx;
	private static final String TABLE_NAME_PRODUTOS = " produtos ";
	private static final String TABLE_NAME_PRECOS = " precos ";
	private static final String TABLE_NAME_COMPARACAO = " precos pr, produtos pd, lojas lj ";
	private static final String TABLE_NAME_JOIN = " precos pr, produtos pd ";
	private static final String WHERE_COMPARACAO = 	" pr.produto_id = pd.produto_id "+
													" AND lj.loja_id = pr.loja_id ";
	private static final String WHERE_JOIN = " pr.produto_id = pd.produto_id ";
	private static final String COLUNAS_JOIN = " pd.produto_id produto_id, "+ 
										" pd.descricao descricao, "+
										" pr.loja_id loja_id, "+ 
										" pr.preco preco, "+ 
										" pr.dt_confirmacao dt_confirmacao, "+ 
										" pd.favorito favorito ";
	
	private static final String[] COLUNAS_PRODUTOS = {	
										"produto_id", 
										"descricao", 
										"favorito"};
	private static final String COLUNAS_PRECOS = 	"produto_id, "+ 
													"loja_id, "+ 
													"preco, "+
													"dt_confirmacao";
	public AlertDAO(Context ctx) {
		this.ctx = ctx;
	}

	public boolean insert(ProdutoVO vo){
        SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();
		ContentValues ctv_produto = new ContentValues();
		ctv_produto.put("produto_id", vo.getId().toString());
		ctv_produto.put("descricao", vo.getDescricao());
		ctv_produto.put("favorito", vo.getFavorito());
		
		boolean result = db.insert(TABLE_NAME_PRODUTOS,
									null,
									ctv_produto) > 0;

		ContentValues ctv_preco = new ContentValues();
		ctv_preco.put("produto_id", vo.getId().toString());
		ctv_preco.put("loja_id", vo.getLoja());
		ctv_preco.put("preco", vo.getPreco());
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String data_confirmacao = dateFormat.format(new Date());
		ctv_preco.put("dt_confirmacao", data_confirmacao);
		
		result = db.insert(TABLE_NAME_PRECOS,
							null,
							ctv_preco) > 0;

		db.close();
		
		if (result) {
			return true;
		} else {
			return false;
		}
		
	}

	public boolean update(ProdutoVO vo){
        SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();
		
		int favorito = vo.getFavorito() ? 1 : 0;
		String queryProduto =	" update produtos "+
								" set 	descricao = '"+vo.getDescricao()+"', "+
								" 		favorito = "+favorito+
								" WHERE produto_id = "+vo.getId();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String data_cfm = dateFormat.format(new Date()); 
		if (vo.getDataConfirmacao() != null){
			data_cfm = dateFormat.format(vo.getDataConfirmacao());
		}
		
		String queryExiste =	" select * "+
								" from precos "+
								" WHERE produto_id = "+vo.getId()+
								" AND loja_id = "+vo.getLoja();
		Cursor cPreco = db.rawQuery(queryExiste, null);
		String queryPreco;
		if (cPreco.moveToNext()){
			queryPreco =	" update precos "+
							" set 	preco = "+vo.getPreco()+", "+
							" 		dt_confirmacao = '"+data_cfm+"' "+
							" WHERE produto_id = "+vo.getId()+
							" AND loja_id = "+vo.getLoja();
		}else{
			queryPreco =	" INSERT INTO precos (produto_id, loja_id, preco, dt_confirmacao) "+
							" SELECT "+vo.getId()+", "+
							vo.getLoja()+", "+
							vo.getPreco()+", "+
							"'"+data_cfm+"'";
		}
		
		db.execSQL(queryProduto);
		db.execSQL(queryPreco);
	
		db.close();
		return true;
	}
	
	public boolean setFavorito(ProdutoVO vo){
		SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();
		ContentValues ctv = new ContentValues();
		if (vo.getFavorito()){
			ctv.put("favorito", 0);
		}else{
			ctv.put("favorito", 1);
		}		
		Boolean result = db.update(TABLE_NAME_PRODUTOS, ctv, "produto_id=?", new String[]{vo.getId() + ""}) > 0;
		db.close();
		return (result);
	}

	public String listaFavoritos(){
		String lista_produtos = "";
		SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();
		String queryProdutos =  " SELECT 	produto_id "+
								" FROM "+TABLE_NAME_PRODUTOS+ 
								" WHERE favorito = 1 ";

		Cursor cProd = db.rawQuery(queryProdutos, null);
		cProd.moveToFirst();
		
		while(!cProd.isAfterLast()){
			lista_produtos += cProd.getString(cProd.getColumnIndex("produto_id"))+",";
			
			cProd.moveToNext();
		}
		if (lista_produtos.length() > 1){
			lista_produtos = lista_produtos.substring(0, lista_produtos.length()-1);	
		} else{
			Log.d("ProdutoDAO (153)","lista de produtos está vazia");
		}
		cProd.close();
		db.close();
		
		return lista_produtos;
	}

	public List<ProdutoVO> getAll(Integer loja) throws ParseException{
		SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();
		List<ProdutoVO> lista_produtos = new ArrayList<ProdutoVO>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		String queryProdutos =  " SELECT 	produto_id, " +
								"			descricao, "+ 
								"			favorito "+ 
								" FROM "+TABLE_NAME_PRODUTOS+ 
								" WHERE favorito = 1 "+ 
								" ORDER BY descricao asc";

		Cursor cProd = db.rawQuery(queryProdutos, null);
		cProd.moveToFirst();
		
		while(!cProd.isAfterLast()){
			ProdutoVO produto_vo = new ProdutoVO();

			produto_vo.setId(new BigInteger(cProd.getString(cProd.getColumnIndex("produto_id"))));
			produto_vo.setLoja(loja);
			produto_vo.setDescricao(cProd.getString(cProd.getColumnIndex("descricao")));
			String queryPreco =  	" SELECT " +COLUNAS_PRECOS+
									" FROM "+TABLE_NAME_PRECOS+ 
									" WHERE loja_id = "+loja+
									" AND produto_id = "+ produto_vo.getId();
				
			Cursor cPreco = db.rawQuery(queryPreco, null);
			cPreco.moveToFirst();
			if (cPreco.getCount() > 0){
				Double preco = cPreco.getDouble(cPreco.getColumnIndex("preco"));
				produto_vo.setPreco(preco);
				
				Date data_confirmacao = new Date();
				data_confirmacao = dateFormat.parse(cPreco.getString(cPreco.getColumnIndex("dt_confirmacao")));
				produto_vo.setDataConfirmacao(data_confirmacao);
				
			} else{
				produto_vo.setPreco(0.0);
				produto_vo.setDataConfirmacao(new Date());
			}
			cPreco.close();

			produto_vo.setFavorito(cProd.getInt(cProd.getColumnIndex("favorito")));
			lista_produtos.add(produto_vo);
			cProd.moveToNext();
		}
	
		cProd.close();
		db.close();

		if (lista_produtos.isEmpty()) {
			lista_produtos = getVazio();
			Log.d("ProdutoDAO (213)","lista de produtos está vazia");
		}

		return lista_produtos;
	}

	public List<ProdutoVO> getAll(BigInteger produto_id, Integer loja, Boolean order) throws ParseException{
		SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();
		List<ProdutoVO> lista_produtos = new ArrayList<ProdutoVO>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		String 	queryProdutos =  " SELECT "+COLUNAS_JOIN+
								 " FROM "+TABLE_NAME_COMPARACAO+
								 " WHERE "+WHERE_COMPARACAO+
								 " AND pr.produto_id = "+produto_id.toString()+
		 						 " AND pr.loja_id <> "+loja+
		 						 " AND lj.comparar = 1";
		
		if (order){ // ordena por preco
				queryProdutos += " ORDER BY pr.preco asc , pr.dt_confirmacao desc";
		} else { //ordena pela data mais atual
				queryProdutos += " ORDER BY pr.dt_confirmacao desc, pr.preco asc ";
		}

		Cursor cPrecos = db.rawQuery(queryProdutos, null);
		cPrecos.moveToFirst();
		
		while(!cPrecos.isAfterLast()){
			ProdutoVO produto_vo = new ProdutoVO();

			produto_vo.setId(new BigInteger(cPrecos.getString(cPrecos.getColumnIndex("produto_id"))));
			produto_vo.setLoja(cPrecos.getInt(cPrecos.getColumnIndex("loja_id")));
			produto_vo.setDescricao(cPrecos.getString(cPrecos.getColumnIndex("descricao")));

			Double preco = cPrecos.getDouble(cPrecos.getColumnIndex("preco"));
			produto_vo.setPreco(preco);
			
			Date data_confirmacao = new Date();
			data_confirmacao = dateFormat.parse(cPrecos.getString(cPrecos.getColumnIndex("dt_confirmacao")));
			produto_vo.setDataConfirmacao(data_confirmacao);
			
			produto_vo.setFavorito(cPrecos.getInt(cPrecos.getColumnIndex("favorito")));
			
			lista_produtos.add(produto_vo);
			cPrecos.moveToNext();
		}
	
		if (lista_produtos.isEmpty()) {
			Log.d("ProdutoDAO (261): ", "Nenhum preco localizado");
			lista_produtos = (this.getVazio());
			Toast.makeText(ctx, "Nenhum preco localizado em suas lojas." , Toast.LENGTH_LONG).show();
			Toast.makeText(ctx, "Tente selecionar mais lojas para comparação." , Toast.LENGTH_LONG).show();
		}
		cPrecos.close();
		db.close();

		if (lista_produtos.isEmpty()) {
			lista_produtos = getVazio();
			Log.d("ProdutoDAO (271)","lista de produtos está vazia");
		}

		return lista_produtos;
	}

	public List<ProdutoVO> getVazio(){
		List<ProdutoVO> lista_produtos = new ArrayList<ProdutoVO>();

		ProdutoVO produto_vo = new ProdutoVO();
		produto_vo.setDescricao("Consulte/inclua um produto");
		produto_vo.setFavorito(0);
		produto_vo.setPreco(0.0);
		lista_produtos.add(produto_vo);
	
		return lista_produtos;
	}

	public List<ProdutoVO> getAll(Integer loja, String busca) throws ParseException{
		SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();
		List<ProdutoVO> lista_produtos = new ArrayList<ProdutoVO>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		String queryProdutos =  " SELECT 	produto_id, " +
								"			descricao, "+ 
								"			favorito "+ 
								" FROM "+TABLE_NAME_PRODUTOS+ 
								" WHERE favorito = 1 "+
								" AND descricao like '%"+busca+"%' "+
								" ORDER BY descricao asc";

		Cursor cProd = db.rawQuery(queryProdutos, null);
		cProd.moveToFirst();
		
		while(!cProd.isAfterLast()){
			ProdutoVO produto_vo = new ProdutoVO();

			produto_vo.setId(new BigInteger(cProd.getString(cProd.getColumnIndex("produto_id"))));
			produto_vo.setLoja(loja);
			produto_vo.setDescricao(cProd.getString(cProd.getColumnIndex("descricao")));

			String queryPreco =  	" SELECT " +COLUNAS_PRECOS+
									" FROM "+TABLE_NAME_PRECOS+ 
									" WHERE loja_id = "+loja+
									" AND produto_id = "+ produto_vo.getId();
				
			Cursor cPreco = db.rawQuery(queryPreco, null);
			cPreco.moveToFirst();
			if (cPreco.getCount() > 0){
				Double preco = cPreco.getDouble(cPreco.getColumnIndex("preco"));
				produto_vo.setPreco(preco);
				
				Date data_confirmacao = new Date();
				data_confirmacao = dateFormat.parse(cPreco.getString(cPreco.getColumnIndex("dt_confirmacao")));
				produto_vo.setDataConfirmacao(data_confirmacao);
				
			} else{
				produto_vo.setPreco(0.0);
				produto_vo.setDataConfirmacao(new Date());
			}
			
			cPreco.close();

			produto_vo.setFavorito(cProd.getInt(cProd.getColumnIndex("favorito")));
			
			lista_produtos.add(produto_vo);
			cProd.moveToNext();
		}
	
		cProd.close();
		db.close();

		if (lista_produtos.isEmpty()) {
			lista_produtos = getVazio();
			Log.d("ProdutoDAO (345)","lista de produtos está vazia");
		}

		return lista_produtos;
	}
	
	public ProdutoVO get(Integer loja, BigInteger produto){
		
		ProdutoVO produto_vo = new ProdutoVO();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		if (produto != null){
			SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();		
			
			String query =  " SELECT " + COLUNAS_JOIN +
							" FROM "+TABLE_NAME_JOIN+ 
							" WHERE "+WHERE_JOIN+
							" AND pr.loja_id = "+loja+
							" AND pr.produto_id = "+produto+
							" ORDER BY pd.descricao asc";
		
			Cursor c = db.rawQuery(query, null);

			c.moveToFirst();

			if (c.getCount() > 0){
				produto_vo.setId(new BigInteger(c.getString(c.getColumnIndex("produto_id"))));
				produto_vo.setLoja(c.getInt(c.getColumnIndex("loja_id")));
				produto_vo.setDescricao(c.getString(c.getColumnIndex("descricao")));
				produto_vo.setPreco(c.getDouble(c.getColumnIndex("preco")));
				
				Date data_confirmacao = new Date();
				try {
					data_confirmacao = dateFormat.parse(c.getString(c.getColumnIndex("dt_confirmacao")));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				produto_vo.setDataConfirmacao(data_confirmacao);
				
				produto_vo.setFavorito(c.getInt(c.getColumnIndex("favorito")));
				
			} 
	
			c.close();
			db.close();

		} else {
			produto_vo.setDescricao("Nenhum produto localizado.");
			produto_vo.setPreco(Double.valueOf("0.0"));
			produto_vo.setFavorito(0);
		}
		return produto_vo;
	}

	public ProdutoVO get(BigInteger produto){
		
		ProdutoVO produto_vo = new ProdutoVO();
		
		if (produto != null){
			SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();		
			
			Cursor c = db.query(TABLE_NAME_PRODUTOS, 
								COLUNAS_PRODUTOS, 
								" produto_id = "+produto, 
								null, 
								null, 
								null, 
								null,
								null);

			
			c.moveToFirst();

			if (c.getCount() > 0){
				produto_vo.setId(new BigInteger(c.getString(c.getColumnIndex("produto_id"))));
				produto_vo.setDescricao(c.getString(c.getColumnIndex("descricao")));

				produto_vo.setFavorito(c.getInt(c.getColumnIndex("favorito")));
				
			} else {
				produto_vo.setDescricao("Nenhum produto localizado.");
				produto_vo.setPreco(Double.valueOf("0.0"));
				produto_vo.setFavorito(0);
			} 
	
			c.close();
			db.close();

		} else {
			produto_vo.setDescricao("Nenhum produto localizado.");
			produto_vo.setPreco(Double.valueOf("0.0"));
			produto_vo.setFavorito(0);
		}
		return produto_vo;
	}

	public Boolean existe(BigInteger produto){
		Boolean result = false;
		SQLiteDatabase db = new DBHelper(ctx).getWritableDatabase();		
		Cursor c = db.query(TABLE_NAME_PRODUTOS, 
							COLUNAS_PRODUTOS, 
							"produto_id = "+produto, 
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
}

