package br.nom.strey.maicon.comparador.loja;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import br.nom.strey.maicon.comparador.produto.ListarProdutos;

public class ListarLojas extends Activity{
	protected static final String CATEGORIA = "ListarLojas";
	ListView listViewLojas;
	ImageButton btIncluir;
	ImageButton btBuscar;
    EditText txtBuscar;
    Integer ibge;
    String buscaLoja = "";
    LojaVO loja_menu;
    LojaDAO loja_dao;
    private String current = "";
    Context ctx;
    private ProgressDialog dialogo;
    Toast mToast;
    Boolean longClick = true;
    @Override
    
    protected void onStart(){
    	super.onStart();
    	criaLista();
    }

    private void criaLista(){
		setContentView(R.layout.lista_lojas);
		loja_dao = new LojaDAO(getBaseContext());
		ibge = getIntent().getIntExtra("ibge", 0);
		txtBuscar = (EditText) findViewById(R.id_loja.txtBusca);
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
		CidadeDAO cidade_dao = new CidadeDAO(getBaseContext());
		CidadeVO cidade_vo = cidade_dao.get(ibge);
		
		TextView txtSubtitulo = (TextView) findViewById(R.id_loja.subTitulo);
		txtSubtitulo.setText(cidade_vo.getNome());
		
		btBuscar = (ImageButton) findViewById(R.id_loja.btBuscar);
		btBuscar.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				txtBuscar = (EditText) findViewById(R.id_loja.txtBusca);

				Intent it = new Intent(getBaseContext(), ListarLojas.class);
				it.putExtra("buscaLoja", txtBuscar.getText().toString().trim());
				it.putExtra("ibge", ibge);
				txtBuscar.setText("");
				startActivity(it);
				finish();
			}
		});

		btIncluir = (ImageButton) findViewById(R.id_loja.btIncluir);
		btIncluir.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				txtBuscar.setText("");
				Intent it = new Intent(getBaseContext(), EditarLoja.class);
				it.putExtra("ibge", ibge);
				startActivity(it);
				finish();
			}
		});
		
		final List<LojaVO> listaLojas;

		if (getIntent().getStringExtra("buscaLoja") != null){
			buscaLoja = getIntent().getStringExtra("buscaLoja");
		}
		
		if (buscaLoja.length() > 0) {
			txtSubtitulo.setText(cidade_vo.getNome()+"\nLojas com '"+buscaLoja+"'.");
			listaLojas = loja_dao.getAll(ibge, buscaLoja);
		} else{
			listaLojas = loja_dao.getAll(ibge);
		}
		
		LojaVO loja_vo = new LojaVO();
		
		if (listaLojas.isEmpty()) {
			loja_vo = loja_dao.get(0);
			listaLojas.add(loja_vo);
			Toast.makeText(getBaseContext(), "Tente novamente." , 15).show();
		}

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		LojaAdapter adapter = new LojaAdapter(this, listaLojas); 
		
		ctx = getBaseContext();
		
		dialogo = new ProgressDialog(this);
		dialogo.setMessage("Sincronizando informações de produtos.");
		dialogo.setTitle("Sincronizando");
		dialogo.setCancelable(true);
		dialogo.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		
		listViewLojas = (ListView) findViewById(R.id_loja.lista);
		
		listViewLojas.setAdapter(adapter);

		listViewLojas.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            	longClick = false;
            	registerForContextMenu(listViewLojas);
            	loja_menu = listaLojas.get(position);
        		if (loja_menu.getId() != 0){
                	openContextMenu(view);
        		} else {
					Toast.makeText(getBaseContext(), "Cadastre uma loja.", 5).show();
        		}
        		longClick = true;
        	}
	    });
		
		listViewLojas.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
				if (longClick){
					loja_menu = listaLojas.get(position);
	        		closeContextMenu();
					mToast = Toast.makeText(ctx, "Problemas na conexão.\n Tente novamente." , 5);
					if (loja_dao.getLojasComparar().length() > 0){
						new ProgressTask().execute();	
					} else {
						Toast.makeText(getBaseContext(), "Você precisa ter pelo menos uma loja marcada como Comparar para prosseguir.", 20).show();
					}
				}
				return longClick;
			}
		});
	}
    
    
	@Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo)
    {
		if (loja_menu.getFavorita()){
			menu.add(Menu.NONE, 0, 0, "Desmarcar Favorita");
		}else{
			menu.add(Menu.NONE, 0, 0, "Marcar Favorita");
		}
		if (loja_menu.getComparar()){
			menu.add(Menu.NONE, 1, 1, "Desmarcar Comparar");
		}else{
			menu.add(Menu.NONE, 1, 1, "Marcar Comparar");
		}
		menu.add(Menu.NONE, 2, 2, "Editar");
		menu.add(Menu.NONE, 3, 3, "Selecionar");
		super.onCreateContextMenu(menu, view, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
    	Log.d("ListarLojas (200)", "item selected="+item.getItemId());
		LojaDAO dao = new LojaDAO(getBaseContext());
		Intent it = new Intent(this, ListarLojas.class);
		
		switch (item.getItemId()) {
		case 0: // favoritar

			dao.setFavorita(loja_menu);
			it.putExtra("ibge", loja_menu.getCidade());
			startActivity(it);
			finish();
			break;

		case 1: // comparar

			dao.setComparar(loja_menu);
			it.putExtra("ibge", loja_menu.getCidade());
			startActivity(it);
			finish();
			break;

		case 2: // editar

			Intent itEditar = new Intent(getBaseContext(), EditarLoja.class);
			itEditar.putExtra("loja", loja_menu.getId());
			startActivity(itEditar);
			finish();
			break;

		case 3: // selecionar

			closeContextMenu();
			mToast = Toast.makeText(ctx, "Problemas na conexão.\n Tente novamente." , 5);
			if (loja_dao.getLojasComparar().length() > 0){
				new ProgressTask().execute();	
			} else {
				Toast.makeText(getBaseContext(), "Você precisa ter pelo menos uma loja marcada como Comparar para prosseguir.", 20).show();
			}
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
            if (dialogo.isShowing()){
            	dialogo.dismiss();
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try{    
            	if (WebService.Conectado(getBaseContext())){
					WebService wb = new WebService();
					if (wb.getProdutos(getBaseContext(), loja_menu.getId())){
						Intent itProdutos = new Intent(ctx, ListarProdutos.class);
						itProdutos.putExtra("loja", loja_menu.getId());
						startActivity(itProdutos);
					} else {
						mToast.show();
					}
				} else {
					mToast.show();
				}
                return true;
             } catch (Exception e){
                Log.e("tag", "error", e);
                return false;
             }
          }
    }
}
