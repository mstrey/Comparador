package br.nom.strey.maicon.comparador;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;
import br.nom.strey.maicon.comparador.loja.LojaDAO;
import br.nom.strey.maicon.comparador.loja.LojaVO;
import br.nom.strey.maicon.comparador.produto.AlertDAO;
import br.nom.strey.maicon.comparador.produto.ProdutoVO;

public class WebService extends AsyncTask<Void, Void, Void>{
	
	public static boolean Conectado(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()) {
                    //handler.sendEmptyMessage(0);
                    Log.d("WebService (29)","Status de conexão 3G: "+cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected());
                    return true;
            } else if(cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()){
                    //handler.sendEmptyMessage(0);
                    Log.d("WebService (33)","Status de conexão Wifi: "+cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected());
                    return true;
            } else {
                    //handler.sendEmptyMessage(0);
                    Log.e("WebService (37)","Status de conexão Wifi: "+cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected());
                    Log.e("WebService (38)","Status de conexão 3G: "+cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected());
                    return false;
            }
        } catch (Exception e) {
                e.printStackTrace();
                return false;
        }
    }
	
	public Boolean setLoja(LojaVO loja_vo){
    	Boolean result = false;
		StringBuffer strUrl = new StringBuffer("http://maicon.strey.nom.br/");
    	strUrl.append("comparador/");
    	strUrl.append("addLoja.php");
    	strUrl.append("?nome=");
    	strUrl.append(URLEncoder.encode(loja_vo.getNome()));
    	strUrl.append("&local=");
    	strUrl.append(URLEncoder.encode(loja_vo.getLocal()));
    	strUrl.append("&cidade=");
    	strUrl.append(URLEncoder.encode(loja_vo.getCidade().toString()));
    	strUrl.append("&loja_id=");
    	strUrl.append(URLEncoder.encode(loja_vo.getId().toString()));
    	
    	try {
    		URL url = new URL(strUrl.toString());			
			URLConnection con = url.openConnection();
			con.setConnectTimeout(15000);
			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
	    	result = true;
			
		} catch (Exception e) {
			e.printStackTrace();
	    	result = false;
		}
    	return result;
	}

	public Integer setLoja(String nome, String local, Integer ibge){
		Integer result = 0;
    	StringBuffer strUrl = new StringBuffer("http://maicon.strey.nom.br/");
    	strUrl.append("comparador/");
    	strUrl.append("addLoja.php");
    	strUrl.append("?nome=");
    	strUrl.append(URLEncoder.encode(nome));
    	strUrl.append("&local=");
    	strUrl.append(URLEncoder.encode(local));
    	strUrl.append("&cidade=");
    	strUrl.append(URLEncoder.encode(ibge.toString()));
    	
    	try {
	    	
    		URL url = new URL(strUrl.toString());			
			URLConnection con = url.openConnection();
			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String linha = in.readLine();
	    	result = Integer.valueOf(linha);
			in.close();
			
		} catch (Exception e) {
			e.printStackTrace();
	    	result = 0;
		}
    	return result;
	}
	
	public Boolean getLojas(Context ctx, Integer ibge) {
    	Boolean result;

		StringBuffer strUrl = new StringBuffer("http://maicon.strey.nom.br/");
    	strUrl.append("comparador/json/");
    	strUrl.append("listarLojas.php");
    	strUrl.append("?cidade=");
    	strUrl.append(URLEncoder.encode(ibge.toString()));
    	
    	try {
    		URL url = new URL(strUrl.toString());			
			URLConnection con = url.openConnection();
			con.setConnectTimeout(15000);
			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			
			String linha;
			while((linha = in.readLine()) != null){
				if (linha.length() < 3){
					Log.d("WebService (124) while", "Force break when line length < 3: "+linha.length());
					break;
				}
				String values[] = linha.split(">");
				
		    	LojaVO loja_vo = new LojaVO();
		    	LojaDAO loja_dao = new LojaDAO(ctx);
		    	
				loja_vo.setCidade(ibge); 
				loja_vo.setId(Integer.valueOf(values[0])); 
				loja_vo.setNome(values[1]); 
				loja_vo.setLocal(values[2]); 
				if (loja_dao.existe(Integer.valueOf(values[0]))){
					loja_vo = loja_dao.get(Integer.valueOf(values[0]));
					loja_vo.setId(Integer.valueOf(values[0])); 
					loja_vo.setNome(values[1]); 
					loja_vo.setLocal(values[2]); 

					loja_dao.update(loja_vo);
				} else {
					loja_vo.setFavorita(0);
					loja_vo.setComparar(0);
					loja_dao.insert(loja_vo);
				}
			}
			result = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = false;
		}
		return result;
    }
 	
	public Boolean setProduto(ProdutoVO produto_vo){
		Boolean result;
		
		StringBuffer strUrl = new StringBuffer("http://maicon.strey.nom.br/");
    	strUrl.append("comparador/");
    	strUrl.append("produto.php");
    	strUrl.append("?codbarras=");
    	strUrl.append(URLEncoder.encode(produto_vo.getId().toString()));
    	strUrl.append("&descricao=");
    	strUrl.append(URLEncoder.encode(produto_vo.getDescricao()));
    	
    	StringBuffer strUrl2 = new StringBuffer("http://maicon.strey.nom.br/");
    	strUrl2.append("comparador/");
    	strUrl2.append("precos.php");
    	strUrl2.append("?produto=");
    	strUrl2.append(URLEncoder.encode(produto_vo.getId().toString()));
    	strUrl2.append("&preco=");
    	strUrl2.append(URLEncoder.encode(produto_vo.getPreco().toString()));
    	strUrl2.append("&loja_atual=");
    	strUrl2.append(URLEncoder.encode(produto_vo.getLoja().toString()));
    	
    	try {
			
    		URL url = new URL(strUrl.toString());			
			URLConnection con = url.openConnection();
			
			URL url2 = new URL(strUrl2.toString());			
			con = url2.openConnection();
			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
	    	
			result = setPreco(produto_vo);
		} catch (Exception e) {
			e.printStackTrace();
	    	result = false;
		}
		return result;
	}

	public Boolean getProdutos(Context ctx, Integer loja){
    	Boolean result;
		
		AlertDAO produto_dao = new AlertDAO(ctx);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		StringBuffer strUrl = new StringBuffer("http://maicon.strey.nom.br/");
    	strUrl.append("comparador/");
    	strUrl.append("precos.php");
    	strUrl.append("?listaProdutos=");
    	strUrl.append(URLEncoder.encode(produto_dao.listaFavoritos()));
    	strUrl.append("&loja_atual=");
    	strUrl.append(URLEncoder.encode(loja.toString()));
    	
    	try {
			
    		URL url = new URL(strUrl.toString());			
			URLConnection con = url.openConnection();
			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			
			String linha;
			while((linha = in.readLine()) != null){
				if (linha.length() < 3){
					Log.d("WebService (223) while","Force break when line length < 3: "+linha.length());
					break;
				}
				String values[] = linha.split(">");
				
		    	ProdutoVO produto_vo = new ProdutoVO();
		    	
		    	produto_vo.setId(new BigInteger(values[0])); 
		    	produto_vo.setDescricao(values[1]); 
		    	produto_vo.setLoja(Integer.valueOf(values[2])); 
		    	produto_vo.setPreco(Double.valueOf(values[3])); 

		    	Date data_confirmacao = new Date();
				data_confirmacao = dateFormat.parse(values[4]);

		    	produto_vo.setDataConfirmacao(data_confirmacao); 
				if (produto_dao.existe(new BigInteger(values[0]))){
					produto_vo.setFavorito(produto_dao.get(new BigInteger(values[0])).getFavorito()?1:0);
					produto_dao.update(produto_vo);
				} else {
					produto_vo.setFavorito(0);
					produto_dao.insert(produto_vo);
				}
			}
			result = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	public Boolean setPreco(ProdutoVO produto_vo){
    	Boolean result = false;
		
		StringBuffer strUrl = new StringBuffer("http://maicon.strey.nom.br/");
    	strUrl.append("comparador/");
    	strUrl.append("precos.php");
    	strUrl.append("?produto=");
    	strUrl.append(URLEncoder.encode(produto_vo.getId().toString()));
    	strUrl.append("&preco=");
    	strUrl.append(URLEncoder.encode(produto_vo.getPreco().toString()));
    	strUrl.append("&loja_atual=");
    	strUrl.append(URLEncoder.encode(produto_vo.getLoja().toString()));
    	
    	try {
		
    		URL url = new URL(strUrl.toString());			
			URLConnection con = url.openConnection();
			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			
	    	result = true;
			
		} catch (Exception e) {
			e.printStackTrace();
	    	result = false;
		}
    	return result;
	}
	
	public Boolean getPreco(Context ctx, BigInteger codBarras, String lojas) {
		Boolean result  = false;
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		AlertDAO produto_dao = new AlertDAO(ctx);
		
		StringBuffer strUrl = new StringBuffer("http://maicon.strey.nom.br/");
    	strUrl.append("comparador/");
    	strUrl.append("precos.php");
    	strUrl.append("?produto=");
    	strUrl.append(URLEncoder.encode(codBarras.toString()));
    	strUrl.append("&lojas_comparar=");
    	strUrl.append(URLEncoder.encode(lojas));
    	
    	try {
			
    		URL url = new URL(strUrl.toString());			
			URLConnection con = url.openConnection();
			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			
			String linha;
			while((linha = in.readLine()) != null){
				if (linha.length() < 3){
					Log.d("WebService (309)", "Force break when line length < 3: "+linha.length());
					break;
				}
				String values[] = linha.split(">");
				
		    	ProdutoVO produto_vo = new ProdutoVO();
		    	
		    	produto_vo.setId(new BigInteger(values[0])); 
		    	produto_vo.setDescricao(values[1]); 
		    	produto_vo.setLoja(Integer.valueOf(values[2])); 
		    	produto_vo.setPreco(Double.valueOf(values[3])); 

		    	Date data_confirmacao = new Date();
				data_confirmacao = dateFormat.parse(values[4]);
		    	produto_vo.setDataConfirmacao(data_confirmacao); 

		    	if (produto_dao.existe(new BigInteger(values[0]))){
					produto_vo.setFavorito(produto_dao.get(new BigInteger(values[0])).getFavorito()?1:0);
					produto_dao.update(produto_vo);
				} else {
					produto_vo.setFavorito(0);
					produto_dao.insert(produto_vo);
				}
			}
			result = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = false;
		}
		return result;
		
	}

	public Boolean getProduto(Context ctx, BigInteger produto_id){
		Boolean result = false;
		
		StringBuffer strUrl = new StringBuffer("http://maicon.strey.nom.br/");
    	strUrl.append("comparador/");
    	strUrl.append("produto.php");
    	strUrl.append("?codbarras=");
    	strUrl.append(URLEncoder.encode(produto_id.toString()));
    	
    	try {
			
    		URL url = new URL(strUrl.toString());			
			URLConnection con = url.openConnection();
			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			
			String linha;
			while((linha = in.readLine()) != null){
				if (linha.length() < 3){
					Log.d("WebService (362) while","Force break when line length < 3: "+linha.length());
					break;
				}
				String values[] = linha.split(">");
		    	AlertDAO produto_dao = new AlertDAO(ctx);
		    	ProdutoVO produto_vo = new ProdutoVO();
		    	
				if (produto_dao.existe(new BigInteger(values[0]))){
					produto_vo = produto_dao.get(new BigInteger(values[0]));
					produto_vo.setDescricao(values[1]); 
					produto_dao.update(produto_vo);
				} else {
					produto_vo.setId(new BigInteger(values[0])); 
			    	produto_vo.setDescricao(values[1]); 
					produto_vo.setFavorito(0);
					produto_dao.insert(produto_vo);
				}
			}
			result = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		return null;
	}

}