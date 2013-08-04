package br.nom.strey.maicon.comparador.produto;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.List;

import jim.h.common.android.zxinglib.integrator.IntentIntegrator;
import jim.h.common.android.zxinglib.integrator.IntentResult;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import br.nom.strey.maicon.comparador.R;
import br.nom.strey.maicon.comparador.WebService;
import br.nom.strey.maicon.comparador.cidade.CidadeDAO;
import br.nom.strey.maicon.comparador.cidade.CidadeVO;
import br.nom.strey.maicon.comparador.loja.LojaDAO;
import br.nom.strey.maicon.comparador.loja.LojaVO;

public class ListarProdutos extends Activity{
    ListView listViewProdutos;
	ImageButton btIncluir;
	ImageButton btBuscar;
    EditText txtBuscar;
    Integer loja;
    ProdutoVO produto_menu;
    private String current = "";
    private ProgressDialog dialogo;
    Integer option = 0; // 1=selecionou da lista / 2=leu codigo de barras
    Intent itEditar;
    Intent itListarProdutos;
	String cod_barras = null;
	AlertDAO produto_dao;
	ProdutoVO produto_vo;
	Toast mToast;
    Boolean camera = false;
    Boolean emulador = false;
    Boolean longClick = true;
    
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
		setContentView(R.layout.lista_produtos);
		itEditar = new Intent(getBaseContext(), EditarProduto.class);
		itListarProdutos = new Intent(getBaseContext(), ListarProdutos.class);
		produto_dao = new AlertDAO(getBaseContext());
		loja = getIntent().getIntExtra("loja", 0);
		
		LojaDAO loja_dao = new LojaDAO(getBaseContext());
		LojaVO loja_vo = loja_dao.get(loja);
		
		CidadeDAO cidade_dao = new CidadeDAO(getBaseContext());
		CidadeVO cidade_vo = cidade_dao.get(loja_vo.getCidade());
		
		TextView txtSubtitulo = (TextView) findViewById(R.id_produto.subTitulo);
		txtSubtitulo.setText(cidade_vo.getNome()+"\n"+loja_vo.getNome()+"\n"+loja_vo.getLocal());

		txtBuscar = (EditText) findViewById(R.id_produto.txtBusca);
		txtBuscar.addTextChangedListener(new TextWatcher() {

			Toast mToast = Toast.makeText(getBaseContext(), "Limite do campo atingido." , Toast.LENGTH_SHORT);
		    
	        public void afterTextChanged(Editable s) {
	            // TODO Auto-generated method stub
	        }

	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	            // TODO Auto-generated method stub
	        }

	        public void onTextChanged(CharSequence s, int start, int before, int count) {
	        	if(!s.toString().equals(current)){
	        		txtBuscar.removeTextChangedListener(this);
	     	    	if (start == 45){
	     	    		mToast.cancel();
	     	    		mToast.show();
	     	    		txtBuscar.setText(s.toString().substring(0, s.toString().length()-1));
	     	    		txtBuscar.setSelection(s.toString().length()-1);
	     	    	}
	     	    	txtBuscar.addTextChangedListener(this);
	     	    }
	        } 
	    });

		btBuscar = (ImageButton) findViewById(R.id_produto.btBuscar);
		btBuscar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				itListarProdutos.putExtra("produto", txtBuscar.getText().toString().trim());
				itListarProdutos.putExtra("loja", loja);
				txtBuscar.setText("");
				startActivity(itListarProdutos);
				finish();
			}
		});

		btIncluir = (ImageButton) findViewById(R.id_produto.btIncluir);
		btIncluir.setOnClickListener(new View.OnClickListener(){
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// define se está rodando no emulador ou no device fisico
				
				if (emulador){
               		cod_barras = "7891000020791";	
               		new ProgressTask().execute();
				} else{
					camera = true;
	                IntentIntegrator.initiateScan(
	                					ListarProdutos.this, 
	                					R.layout.capture,
	                					R.id_capture.viewfinder_view, 
	                					R.id_capture.preview_view, 
	                					true);
				}
			}
		}); 
		
		AlertDAO produto_dao = new AlertDAO(getBaseContext());
		final List<ProdutoVO> listaProdutos;

		if (getIntent().getStringExtra("produto") != null) {
			String busca = getIntent().getStringExtra("produto");
			listaProdutos = produto_dao.getAll(loja, busca);
			txtSubtitulo.setText(cidade_vo.getNome()+
								"\n"+loja_vo.getNome()+
								"\n"+loja_vo.getLocal()+
								"\nProdutos com '"+busca+"'.");
		} else{
			listaProdutos = produto_dao.getAll(loja);
		}
		
		ProdutoVO produto_vo = new ProdutoVO();
		produto_vo = produto_dao.get(0, null);
		
		if (listaProdutos.isEmpty()) {
			listaProdutos.add(produto_vo);
			Toast.makeText(getBaseContext(), "Tente novamente." , 15).show();
		}

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		ProdutoAdapter adapter = new ProdutoAdapter(this, listaProdutos); 
		
		dialogo = new ProgressDialog(this);
		dialogo.setMessage("Sincronizando informações do produto.");
		dialogo.setTitle("Sincronizando");
		dialogo.setCancelable(true);
		dialogo.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialogo.show();
		dialogo.dismiss();
		dialogo.cancel();
		listViewProdutos = (ListView) findViewById(R.id_produto.lista);
		
		listViewProdutos.setAdapter(adapter);

		listViewProdutos.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            	longClick = false;
            	registerForContextMenu(listViewProdutos);
            	produto_menu = listaProdutos.get(position);
        		if (produto_menu.getId() != null){
                	openContextMenu(view);
        		} else {
					Toast.makeText(getBaseContext(), "Inclua um produto.", 5).show();
        		}
        		longClick = true;
        	}
	    });
		
		listViewProdutos.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
				if (longClick){
					produto_menu = listaProdutos.get(position);
	        		closeContextMenu();
					mToast = Toast.makeText(getBaseContext(), "Problemas na conexão.\n Tente novamente." , 5);
					new ProgressTask().execute();
				}
				return longClick;
			}
		});
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (camera){
        	switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE:
                IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                final String result = scanResult.getContents();
                if ((result != null) && (scanResult.getFormatName().toString().contentEquals("EAN_13"))) {
                	cod_barras = result;
    				new ProgressTask().execute();
                } else {
                	Toast.makeText(getBaseContext(), "Código inválido ou inexistente.", 5).show();
                }
                break;
            default:
            	Toast.makeText(getBaseContext(), "Código inválido ou inexistente.", 5).show();
        	}	
        }
        requestCode = 0;
        resultCode = 0;
        data = null;
    }
 
	@Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo)
    {
		if (produto_menu.getFavorito()) {
			menu.add(Menu.NONE, 0, 0, "Desmarcar Favorito");
		}else{
			menu.add(Menu.NONE, 0, 0, "Marcar Favorito");
		}
		menu.add(Menu.NONE, 1, 1, "Selecionar");
		
		super.onCreateContextMenu(menu, view, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
		AlertDAO dao = new AlertDAO(getBaseContext());
		Intent it = new Intent(this, ListarProdutos.class);

		switch (item.getItemId()) {
		case 0: // favoritar

			dao.setFavorito(produto_menu);
			it.putExtra("loja", loja);
			startActivity(it);
			finish();
			
			break;

		case 1: // Selecionar

			closeContextMenu();
			mToast = Toast.makeText(getBaseContext(), "Problemas na conexão.\n Tente novamente." , 5);
			new ProgressTask().execute();
			break;

		default:
			break;
		}
		
        return super.onContextItemSelected(item);
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
            dialogo.dismiss();
            dialogo.cancel();
    		finish();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
        	try{   
        		BigInteger produto_id;
            	if (cod_barras != null){
            		produto_id = new BigInteger(cod_barras);
            	} else{
            		produto_id = produto_menu.getId();
            	}
            	if (WebService.Conectado(getBaseContext())){
					WebService wb = new WebService();
					if (wb.getProduto(getBaseContext(), produto_id)){
						produto_vo = produto_dao.get(produto_id);
						if (produto_vo.getDescricao().equalsIgnoreCase("Nenhum produto localizado.")){
							produto_vo.setDescricao(null);
						} else {
							wb.getPreco(getBaseContext(), produto_id, loja.toString());
						}
						itEditar.putExtra("loja", loja);
                		itEditar.putExtra("cod_barras", produto_id.toString());
                		
                		itListarProdutos.putExtra("loja", loja);
        				
                		startActivity(itListarProdutos);
                		startActivity(itEditar);
					} else {
						mToast.show();
					}
				} else {
					mToast.show();
				}
            	return true;
             } catch (Exception e){
                e.printStackTrace();
                return false;
             }
          }
    }	
}
