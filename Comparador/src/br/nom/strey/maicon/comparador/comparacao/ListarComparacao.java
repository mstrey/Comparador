package br.nom.strey.maicon.comparador.comparacao;

import java.io.File;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import br.nom.strey.maicon.comparador.R;
import br.nom.strey.maicon.comparador.WebService;
import br.nom.strey.maicon.comparador.produto.AlertDAO;
import br.nom.strey.maicon.comparador.produto.ProdutoVO;

public class ListarComparacao extends Activity{
    Button btPreco;
    Button btData;
	ListView listViewPrecos;
    Integer loja;
    Boolean ordenacao = true;
    String cod_barras;
    BigInteger produto_id;
    ProdutoVO produto_selecionado;
    File tempFile;
    Uri outputFileUri;
    String local_foto;
    ProdutoVO produto_vo;
    Boolean executou;
    Boolean buscou;
    
    ImageView fotoAtual;
	ImageView iconFavoritoAtual;
	TextView txtDescricaoAtual;
	TextView txtPrecoAtual;
	
    Context ctx;
    private ProgressDialog dialogo;
    AlertDAO produto_dao;
    @Override
    protected void onStart(){
    	super.onStart();
		try {
			criaLista();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private void criaLista() throws ParseException{
		setContentView(R.layout.lista_comparacao);
		
		loja = getIntent().getIntExtra("loja", 0);
		ordenacao = getIntent().getBooleanExtra("ordem", true);
		cod_barras = getIntent().getStringExtra("cod_barras");
		produto_id = new BigInteger(cod_barras);
		produto_dao = new AlertDAO(getBaseContext());
		produto_vo = produto_dao.get(loja, produto_id);
		produto_selecionado = produto_vo;


		fotoAtual = (ImageView) findViewById(R.id_comparacao.foto_produto);
		iconFavoritoAtual = (ImageView) findViewById(R.id_comparacao.iconFavoritoProduto);
		txtDescricaoAtual = (TextView) findViewById(R.id_comparacao.txtDescProduto);
		txtPrecoAtual = (TextView) findViewById(R.id_comparacao.txtPrecoProduto);
			
		Log.i("ListarComparacao (80): ", "codigo de barras: "+cod_barras);
		
		ctx = getApplicationContext();
		
		dialogo = new ProgressDialog(this);
		dialogo.setMessage("Sincronizando informações de produtos.");
		dialogo.setTitle("Sincronizando");
		dialogo.setCancelable(true);
		dialogo.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		
		executou = false;
		new ProgressTask().execute();
		while (!executou){}
		
		if (buscou){
		    if (produto_vo.getFoto() != null){
		    	fotoAtual.setImageBitmap(produto_vo.getFoto());
		    } else {
		    	fotoAtual.setImageResource(R.drawable.no_foto);
		    }
		    
			if (!produto_vo.getFavorito()){
				iconFavoritoAtual.setImageResource(R.drawable.favorito_off);
			}else{
				iconFavoritoAtual.setImageResource(R.drawable.favorito_on);
			}
			txtDescricaoAtual.setText(produto_vo.getDescricao());
			NumberFormat formatoReal = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
			String preco = formatoReal.format(produto_vo.getPreco());
			txtPrecoAtual.setText(preco);
		} else {
			fotoAtual.setImageResource(R.drawable.no_foto);
			iconFavoritoAtual.setImageResource(R.drawable.favorito_off);
			txtDescricaoAtual.setText("Erro ao recuperar produto.");
			txtPrecoAtual.setText("R$0,00");
			
			Toast.makeText(getBaseContext(), "Problema na conexão. O registro não foi recuperado." , Toast.LENGTH_SHORT).show();
		}
		// configura texto de ordenação e define ações para os botões
		
		TextView txtOrdenacao = (TextView) findViewById(R.id_comparacao.txtOrdenacao);
		if (ordenacao){
			txtOrdenacao.setText("Orenado pelo menor preço");
		} else {
			txtOrdenacao.setText("Orenado pelo mais atual");
		}
		// confirgura botão de ordenacao por preco
		btPreco = (Button) findViewById(R.id_comparacao.btPreco);
		btPreco.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent it = new Intent(getBaseContext(), ListarComparacao.class);
				it.putExtra("cod_barras", cod_barras);
				it.putExtra("ordem", true);
				it.putExtra("loja", loja);
				startActivity(it);
				finish();
			}
		});
		
		// confirgura botão de ordenacao por data
		btData = (Button) findViewById(R.id_comparacao.btData);
		btData.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent it = new Intent(getBaseContext(), ListarComparacao.class);
				it.putExtra("cod_barras", cod_barras);
				it.putExtra("ordem", false);
				it.putExtra("loja", loja);
				startActivity(it);
				finish();
			}
		});
		
		//alimenta lista de precos em outras lojas:
		
		listViewPrecos = (ListView) findViewById(R.id_comparacao.lista);
		List<ProdutoVO> listaPrecos;
		listaPrecos = produto_dao.getAll(produto_id, loja, ordenacao);

		if (listaPrecos.isEmpty()) {
			listaPrecos = produto_dao.getVazio();
			Toast.makeText(getBaseContext(), "Tente novamente." , 15).show();
		}

		ComparacaoAdapter adapter = new ComparacaoAdapter(this, listaPrecos, produto_selecionado); 
		
		listViewPrecos.setAdapter(adapter);

	}


    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}
	
	private class ProgressTask extends AsyncTask<Void, Void, Boolean> {
		
		public ProgressTask() {
        }
        
        @Override
        protected void onPreExecute() {
            dialogo.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            executou = true;
        	if (dialogo.isShowing()){
            	dialogo.dismiss();
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try{    
            	if (WebService.Conectado(getBaseContext())){
        			WebService wb = new WebService();
        			buscou = wb .getPreco(getBaseContext(), produto_id,loja.toString());
        		} else {
        			Toast.makeText(getBaseContext(), "Problema na conexão. O registro não foi recuperado." , Toast.LENGTH_SHORT).show();
        		}
            	executou = true;
                return true;
             } catch (Exception e){
                Log.e("tag", "error", e);
                return false;
             }
          }
    }
}
