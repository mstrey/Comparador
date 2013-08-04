package br.nom.strey.maicon.comparador.loja;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import br.nom.strey.maicon.comparador.R;
import br.nom.strey.maicon.comparador.WebService;
import br.nom.strey.maicon.comparador.cidade.CidadeDAO;

public class EditarLoja extends Activity{

	protected static final String CATEGORIA = "EditarLojas";
	private static final int TIRAR_FOTO = 1020394857;
	Button btSalvar;
    Integer loja_id = 0;
    EditText txtNome;
    EditText txtLocal;
    ImageView foto;
    Integer favorita = 0;
    Integer comparar = 0;
    ImageView iconFavorita;
    ImageView iconComparar;
    ImageView iconPhoto;
    LojaDAO loja_dao;
    LojaVO loja_vo = new LojaVO();
    Integer ibge;
    Boolean incluir = false;
    File tempFile;
    Uri outputFileUri;
    String local_foto;
	private String current = "";
	private ProgressDialog dialogo;
	
	@SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editar_loja);
		
		txtNome = (EditText) findViewById(R.id_editar_loja.txtNome);
		txtLocal = (EditText) findViewById(R.id_editar_loja.txtLocal);
		loja_dao = new LojaDAO(getBaseContext());

		CidadeDAO cidade_dao = new CidadeDAO(getBaseContext());
		
		if (getIntent().getIntExtra("ibge",0) != 0){
			incluir = true; // incluir
		}
			
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		if (incluir){
			ibge = getIntent().getIntExtra("ibge",0);
		}else{

	        loja_id = getIntent().getIntExtra("loja",0);
			loja_vo = loja_dao.get(loja_id);

			ibge = loja_vo.getCidade();
			txtNome = (EditText) findViewById(R.id_editar_loja.txtNome);
			txtNome.setText(loja_vo.getNome());
		    
			txtLocal = (EditText) findViewById(R.id_editar_loja.txtLocal);
			txtLocal.setText(loja_vo.getLocal());
			
			iconFavorita = (ImageView) findViewById(R.id_editar_loja.iconFavorite);
			
			if (loja_vo.getFavorita()){
				iconFavorita.setImageResource(R.drawable.favorito_on);
				favorita = 1;
			}else{
				iconFavorita.setImageResource(R.drawable.favorito_off);
				favorita = 0;
			}
			
		    iconComparar = (ImageView) findViewById(R.id_editar_loja.iconComparar);
		    if (loja_vo.getComparar()){
				iconComparar.setImageResource(R.drawable.comparar_on);
				comparar = 1;
			}else{
				iconComparar.setImageResource(R.drawable.comparar_off);
				comparar = 0;
			}
		    
		    foto = (ImageView) findViewById(R.id_editar_loja.foto);
		    if (loja_vo.getFoto() != null){
		    	foto.setImageBitmap(loja_vo.getFoto());
		    } else {
		    	foto.setImageResource(R.drawable.no_foto);
		    }
    		
		}

		txtNome.addTextChangedListener(new TextWatcher() {

			@SuppressLint("ShowToast")
			Toast mToast = Toast.makeText(getBaseContext(), "Limite do campo atingido." , Toast.LENGTH_SHORT);
		    
	        public void afterTextChanged(Editable s) {
	            // TODO Auto-generated method stub
	        }

	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	            // TODO Auto-generated method stub
	        }

	        public void onTextChanged(CharSequence s, int start, int before, int count) {

	        	if(!s.toString().equals(current)){
	        		txtNome.removeTextChangedListener(this);
	     	    	if (start == 45){
	     	    		mToast.cancel();
	     	    		mToast.show();
	     	    		txtNome.setText(s.toString().substring(0, s.toString().length()-1));
	     	    		txtNome.setSelection(s.toString().length()-1);
	     	    	}

	     	    	txtNome.addTextChangedListener(this);
	     	    }

	        } 

	    });
		
		txtLocal.addTextChangedListener(new TextWatcher() {

			Toast mToast = Toast.makeText(getBaseContext(), "Limite do campo atingido." , Toast.LENGTH_SHORT);
		    
	        public void afterTextChanged(Editable s) {
	            // TODO Auto-generated method stub
	        }

	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	            // TODO Auto-generated method stub
	        }

	        public void onTextChanged(CharSequence s, int start, int before, int count) {

	        	if(!s.toString().equals(current)){
	        		txtLocal.removeTextChangedListener(this);
	     	    	if (start == 45){
	     	    		mToast.cancel();
	     	    		mToast.show();
	     	    		txtLocal.setText(s.toString().substring(0, s.toString().length()-1));
	     	    		txtLocal.setSelection(s.toString().length()-1);
	     	    	}
	     	    	txtLocal.addTextChangedListener(this);
	     	    }
	        } 
	    });
		
		TextView txtSubtitulo = (TextView) findViewById(R.id_editar_loja.subTitulo);
		txtSubtitulo.setText(cidade_dao.get(ibge).getNome());

		dialogo = new ProgressDialog(this);
		dialogo.setMessage("Sincronizando informações de lojas.");
		dialogo.setTitle("Sincronizando");
		dialogo.setCancelable(true);
		dialogo.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		
		btSalvar = (Button) findViewById(R.id_editar_loja.btSalvar);
		btSalvar.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				dialogo.show();
				iconFavorita.setEnabled(false);
				iconComparar.setEnabled(false);
				txtNome.setEnabled(false);
				txtLocal.setEnabled(false);
				btSalvar.setEnabled(false);
				
				new ProgressTask().execute();
				btSalvar.setEnabled(true);
			}
		});

		iconFavorita = (ImageView) findViewById(R.id_editar_loja.iconFavorite);
		iconFavorita.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				if (favorita == 0){
					iconFavorita.setImageResource(R.drawable.favorito_on);
					favorita = 1;
				}else{
					iconFavorita.setImageResource(R.drawable.favorito_off);
					favorita = 0;
				}
			}
		});

		iconComparar = (ImageView) findViewById(R.id_editar_loja.iconComparar);
		iconComparar.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				if (comparar == 0){
					iconComparar.setImageResource(R.drawable.comparar_on);
					comparar = 1;
				}else{
					iconComparar.setImageResource(R.drawable.comparar_off);
					comparar = 0;
				}
			}
		});

		iconPhoto = (ImageView) findViewById(R.id_editar_loja.iconPhoto);
		iconPhoto.setOnClickListener(new View.OnClickListener() {
			
	        public void onClick(View arg0) {
				// TODO Auto-generated method stub
	        	local_foto = Environment.getExternalStorageDirectory() + "/Comparador/loja/"+loja_id+".png";
		    	tempFile = new File(local_foto); 
		        outputFileUri = Uri.fromFile(tempFile);
				
	        	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	        	intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
	        	startActivityForResult(intent, TIRAR_FOTO);
		        if (tempFile.exists()){ 
                    tempFile.delete(); 
                }
                
			}

		});
	}

    @SuppressLint("ShowToast")
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TIRAR_FOTO) {
            if (resultCode == RESULT_OK) {
            	LojaDAO dao = new LojaDAO(getBaseContext());
            	foto = (ImageView) findViewById(R.id_editar_loja.foto);
            	ajustaFoto();
            	foto.setImageBitmap(dao.get(loja_id).getFoto());
                Toast.makeText(getBaseContext(), "Parabéns! Agora ficou mais fácil reconhecer esta loja.", Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getBaseContext(), "Cancelou", Toast.LENGTH_SHORT);
            } else {
                Toast.makeText(getBaseContext(), "Saiu", Toast.LENGTH_SHORT);
            }

        }
    }
 
    protected void ajustaFoto(){
    	getContentResolver().notifyChange(outputFileUri, null);
		ContentResolver cr = getContentResolver();
		Bitmap bitmap = null;
		int w = 0;
		int h = 0;
		Matrix mtx = new Matrix();
		// Ajusta orienta��o da imagem
		try {
			bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, outputFileUri);
			w = bitmap.getWidth();
			h = bitmap.getHeight();
			mtx = new Matrix();
			ExifInterface exif = new ExifInterface(tempFile.getAbsolutePath());
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch(orientation) {
				case 3: // ORIENTATION_ROTATE_180
					mtx.postRotate(180);
					break;
				case 6: //ORIENTATION_ROTATE_90
					mtx.postRotate(90);
					break;
				case 8: //ORIENTATION_ROTATE_270
					mtx.postRotate(270);
					break;
				default:
					mtx.postRotate(0);
					break;
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Bitmap rotatedBmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
		BitmapDrawable bmpd = new BitmapDrawable(rotatedBmp);
		
		// redimensiona a imagem
		Integer lateral = 256;
		try {
		       FileOutputStream out = new FileOutputStream(local_foto);
		       Bitmap bmp = bmpd.getBitmap();
		       Integer idx = 1;
		       
		       w = bmp.getWidth();
		       h = bmp.getHeight();
		       if ( w >= h){
		    	   idx = w / lateral;
		       } else {
		    	   idx = h / lateral;
		       }
		       w = w / idx;
		       h = h / idx;
		       
		       Bitmap bmpReduzido = Bitmap.createScaledBitmap(bmp, w, h, true);
		       bmpReduzido.compress(Bitmap.CompressFormat.PNG, 90, out);
		} catch (Exception e) {
		       e.printStackTrace();
		}
		
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
					Boolean wbSave = false;
					
					loja_vo.setNome(txtNome.getText().toString());
					loja_vo.setLocal(txtLocal.getText().toString());
					loja_vo.setCidade(ibge);
					loja_vo.setFavorita(favorita);
					loja_vo.setComparar(comparar);

					if (incluir){
						loja_id = wb.setLoja(txtNome.getText().toString(), txtLocal.getText().toString(), ibge); 
						if (loja_id > 0){
							loja_vo.setId(loja_id);
							wbSave = true;
						}
					}else{
						wbSave = wb.setLoja(loja_vo);
					}

					if (wbSave){
						
						if (incluir){
							loja_dao.insert(loja_vo);	
						}else{
							loja_dao.update(loja_vo);
						}
						
						Intent it = new Intent(getBaseContext(), ListarLojas.class);
						it.putExtra("ibge", ibge);
						startActivity(it);

						finish();
					} else {
						Toast.makeText(getBaseContext(), "Problema na conexão. O registro não foi salvo." , Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(getBaseContext(), "Problema na conexão. O registro não foi salvo." , Toast.LENGTH_SHORT).show();
				}
                return true;
             } catch (Exception e){
                Log.e("tag", "error", e);
                return false;
             }
          }
    }
}
			