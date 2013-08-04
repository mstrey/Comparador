package br.nom.strey.maicon.comparador.cidade;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
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
import br.nom.strey.maicon.comparador.loja.ListarLojas;

public class ListarCidades extends Activity{
	ListView listViewCities;
	ImageButton btBuscar;
    EditText txtBuscar;
    String buscaCidade = "";
    CidadeVO cidade_menu;
    MenuItem item;
    private String current = "";
    Context ctx;
    private ProgressDialog dialogo;
    Boolean longClick = true;
    Toast mToastErrConnection;
    Toast mToastNeedConnection;
    View v_msg_conexao = null;
    Boolean menuContexto = true;
    @Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		criaLista();
	}
	
	private void criaLista(){
        setContentView(R.layout.lista_cidades);
        
        ctx = getApplicationContext();
		
        mToastErrConnection = Toast.makeText(ctx, "Não achei uma conexão ativa. Verifique se você está conectado." , 15);
        mToastNeedConnection = Toast.makeText(ctx, "Você precisa estar conectado.\n Escolha qual conexão ativar." , 15);
        
		listViewCities = (ListView) findViewById(R.id_cidade.lista);
		CidadeDAO cidade_dao = new CidadeDAO(getBaseContext());
		final List<CidadeVO> listaCidades;
		
		
		if (getIntent().getStringExtra("buscaCidade") != null) {
			buscaCidade = getIntent().getStringExtra("buscaCidade");
		}

		txtBuscar = (EditText) findViewById(R.id_cidade.txtBusca);
		
		txtBuscar.addTextChangedListener(new TextWatcher() {

	        public void afterTextChanged(Editable s) {
	            // TODO Auto-generated method stub

	        }

	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	            // TODO Auto-generated method stub

	        }

	        public void onTextChanged(CharSequence s, int start, int before, int count) {

	        	if(!s.toString().equals(current)){
	        		txtBuscar.removeTextChangedListener(this);
	     	    	if (start == 50){
	     	    		Toast.makeText(getBaseContext(), "Limite do campo atingido." , 5).show();
	 	     	    	txtBuscar.setText(s.toString().substring(0, s.toString().length()-1));
		     	    	txtBuscar.setSelection(s.toString().length()-1);
	     	    	}

	     	    	txtBuscar.addTextChangedListener(this);
	     	    }

	        } 

	    });
		
		btBuscar = (ImageButton) findViewById(R.id_cidade.btBuscar);
		btBuscar.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				buscaCidade = txtBuscar.getText().toString().trim();
				TextView txtSubtitulo = (TextView) findViewById(R.id_cidade.subTitulo);
				
				if (buscaCidade.length() > 3 || buscaCidade.length() == 0) {
					Intent it = new Intent(getBaseContext(), ListarCidades.class);
					if (buscaCidade.length() > 3){
						it.putExtra("buscaCidade", buscaCidade);	
					}
					startActivity(it);
					finish();
					txtBuscar.setText("");
				} else {
					txtSubtitulo.setText("Informe mais de 3 letras.");
					Toast.makeText(getBaseContext(), "Texto muito curto", 5).show();
				}
				
			}
		});
	
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		TextView txtSubtitulo = (TextView) findViewById(R.id_cidade.subTitulo);
		
		if (buscaCidade.length() > 0) {
			txtSubtitulo.setText("Cidades com '"+buscaCidade+"'.");

			listaCidades = cidade_dao.getAll(buscaCidade);
			if (listaCidades.isEmpty()) {
				Toast.makeText(getBaseContext(), "Tente novamente." , 5).show();
			}
		} else{
			listaCidades = cidade_dao.getAll();
		}
		
		CidadeVO cidade_vo = new CidadeVO();
		
		if (listaCidades.isEmpty()) {
			cidade_vo = cidade_dao.get(0);
			listaCidades.add(cidade_vo);
		}

		CidadeAdapter adapter = new CidadeAdapter(this, listaCidades);
		listViewCities.setAdapter(adapter);

		dialogo = new ProgressDialog(this);
		dialogo.setMessage("Sincronizando informações de lojas.");
		dialogo.setTitle("Sincronizando");
		dialogo.setCancelable(true);
		dialogo.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		
		listViewCities.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				longClick = false;
				menuContexto = true;
				registerForContextMenu(listViewCities);
		    	cidade_menu = listaCidades.get(position);
				if (cidade_menu.getIbge() != 0){
					if (WebService.Conectado(ctx)){
						openContextMenu(view);
					} else {
						menuContexto = false;
						mToastNeedConnection.show();
						openContextMenu(view);
					}
				} else {
					Toast.makeText(ctx, "Busque por uma cidade.", 5).show();
				}
				longClick = true;
			}
	    });
		
		listViewCities.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
				if (longClick){
					registerForContextMenu(listViewCities);
					cidade_menu = listaCidades.get(position);
					if (cidade_menu.getIbge() != 0){
						if (WebService.Conectado(getBaseContext())){
							new ProgressTask().execute();
						} else {
							menuContexto = false;
							mToastErrConnection.show();
							//openContextMenu(view);
						}
					} else {
						Toast.makeText(getBaseContext(), "Busque por uma cidade.", 5).show();
					}
				}
				return longClick;
			}
		});
	}
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo)
    {
		if (menuContexto){
			if (cidade_menu.getFavorita()){
				menu.add(Menu.NONE, 0, 0, "Desmarcar Favorita");
			} else {
				menu.add(Menu.NONE, 0, 0, "Marcar Favorita");
			}
			menu.add(Menu.NONE, 1, 1, "Selecionar");
		}else{
			menu.add(Menu.NONE, 2, 2, "Habilitar Wifi");
			menu.add(Menu.NONE, 3, 3, "Habilitar 3G");
		}
		super.onCreateContextMenu(menu, view, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem itemSelected)
    {
    	closeContextMenu();
    	Log.d("ListarCidades (226)", "item selected="+itemSelected.getItemId());
        item = itemSelected;
		Intent itCidades = new Intent(ctx, ListarCidades.class);
		
		switch (item.getItemId()) {
		case 0: // favoritar
			CidadeDAO dao = new CidadeDAO(getBaseContext());
			dao.setFavorita(cidade_menu);
			startActivity(itCidades);
			finish();
			break;

		case 1: // selecionar
			// TODO Auto-generated method stub
			closeContextMenu();
			new ProgressTask().execute();
			break;

		case 2: // Habilitar Wifi
			// TODO Auto-generated method stub
	    	Log.d("ListarCidades (246)", "habilitando Wifi...");
	    	Toast.makeText(ctx, "Conectando..." , 5).show();
	    	WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
	    	if(!wifiManager.isWifiEnabled()){  
	    	    if (wifiManager.setWifiEnabled(true)){
	    	    	try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    	    	if (wifiManager.isWifiEnabled()){
	    	    		Toast.makeText(ctx, "Conexão Wifi habilitada." , 5).show();
	    	    	} else{
	    	    		Toast.makeText(ctx, "Ops, parece que não consegui habilitar sua Wifi." , 5).show();
	    	    	}
	    	    } else {
    	    		Toast.makeText(ctx, "Ops, parece que não consegui habilitar sua Wifi.." , 5).show();
    	    	}
	    	}else{  
	    		Toast.makeText(ctx, "Estranho, parece que sua Wifi já estava habilitada." , 10).show();  
	    		Toast.makeText(ctx, "Talvez você não esteja conectado em alguma rede." , 10).show();
	    	}
			break;

		case 3: // Habilitar 3G
			// TODO Auto-generated method stub
			Toast.makeText(ctx, "Conectando..." , 10).show();
			try {
				setMobileDataEnabled(ctx, true);
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	if (WebService.Conectado(ctx)){
				Toast.makeText(ctx, "Conexão 3G habilitada." , 5).show();
			} else{
				Toast.makeText(ctx, "Ops, parece que não consegui habilitar sua 3G.." , 5).show();
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
            	if (WebService.Conectado(ctx)){
					WebService wb = new WebService();
					if (wb.getLojas(ctx, cidade_menu.getIbge())){
						Intent itLojas = new Intent(ctx, ListarLojas.class);
						itLojas.putExtra("ibge", cidade_menu.getIbge());
						startActivity(itLojas);
					} else {
						mToastErrConnection.show();
					}
				} else {
					mToastErrConnection.show();
				}
                return true;
             } catch (Exception e){
                Log.e("tag", "error", e);
                return false;
             }
          }
    }
	private Boolean setMobileDataEnabled(Context context, boolean enabled) {
	    final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    Class<?> conmanClass;
		try {
			conmanClass = Class.forName(conman.getClass().getName());
			final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
		    iConnectivityManagerField.setAccessible(true);
		    final Object iConnectivityManager = iConnectivityManagerField.get(conman);
		    final Class<?> iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
		    final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
		    setMobileDataEnabledMethod.setAccessible(true);

		    if (setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled) != null){
		    	return true;
		    } else {
		    	return false;
		    }
		    
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	    
	}

}
