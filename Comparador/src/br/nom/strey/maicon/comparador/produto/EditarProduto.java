package br.nom.strey.maicon.comparador.produto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import br.nom.strey.maicon.comparador.R;
import br.nom.strey.maicon.comparador.WebService;
import br.nom.strey.maicon.comparador.cidade.CidadeDAO;
import br.nom.strey.maicon.comparador.comparacao.ListarComparacao;
import br.nom.strey.maicon.comparador.loja.LojaDAO;

public class EditarProduto extends Activity{

    public static final int HIDE_IMPLICIT_ONLY = 0x0001;
    private static final int TIRAR_FOTO = 1020394857;
	Button btComparar;
    BigInteger produto_id;
    EditText txtDescricao;
    EditText txtPreco;
    ImageView foto;
    Integer favorito = 0;
    ImageView iconFavorito;
    ImageView iconPhoto;
    AlertDAO produto_dao;
    ProdutoVO produto_vo = new ProdutoVO();
    ProdutoVO produto_vo_preco = new ProdutoVO();
    CidadeDAO cidade_dao = new CidadeDAO(getBaseContext());
    Integer ibge;
    LojaDAO loja_dao = new LojaDAO(getBaseContext());
	private Integer loja;
    String cod_barras;
    Boolean incluir = false;
    File tempFile;
    Uri outputFileUri;
    String local_foto;
	private String current = "";
	private ProgressDialog dialogo;
    Integer option; // 1= iniciar / 2 = comparar
    Integer buscou = 0; // 1=achou produto / 2 = achou preco
    String prodDescricao;
    String prodPreco;
    Integer prodFavorito;
    Boolean executou;
    Toast mToast;
    Toast mToastComparar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editar_produto);
		
		produto_dao = new AlertDAO(getBaseContext());

		cod_barras = getIntent().getStringExtra("cod_barras");
		produto_id =  new BigInteger(cod_barras);
		loja = getIntent().getIntExtra("loja",0);
		produto_vo = produto_dao.get(produto_id);
		
		dialogo = new ProgressDialog(this);
		dialogo.setMessage("Sincronizando informações de produtos.");
		dialogo.setTitle("Sincronizando");
		dialogo.setCancelable(true);
		dialogo.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mToast = Toast.makeText(getBaseContext(), "Problema na conexão. O registro não foi salvo." , Toast.LENGTH_SHORT);
		mToastComparar = Toast.makeText(getBaseContext(), "Você não tem lojas marcadas para comparar. Marque alguma loja e tente novamente." , Toast.LENGTH_LONG);
				
		// se foi feita leitura de código de barras precisa verificar se já existe informação.
		if (WebService.Conectado(getBaseContext())){
			loja_dao = new LojaDAO(getBaseContext());
			cidade_dao = new CidadeDAO(getBaseContext());
			ibge = loja_dao.get(loja).getCidade();
			
			TextView txtSubtitulo = (TextView) findViewById(R.id_editar_produto.subTitulo);
			txtSubtitulo.setText(cidade_dao.get(ibge).getNome()+"\n"+loja_dao.get(loja).getNome()+"\n"+loja_dao.get(loja).getLocal());

			txtDescricao = (EditText) findViewById(R.id_editar_produto.txtDescricao);
			txtDescricao.addTextChangedListener(new TextWatcher() {

				Toast mToast = Toast.makeText(getBaseContext(), "Limite do campo atingido." , Toast.LENGTH_SHORT);
			    
		        public void afterTextChanged(Editable s) {
		            // TODO Auto-generated method stub
		        }

		        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		            // TODO Auto-generated method stub
		        }

		        public void onTextChanged(CharSequence s, int start, int before, int count) {

		        	if(!s.toString().equals(current)){
		        		txtDescricao.removeTextChangedListener(this);
		     	    	if (start == 45){
		     	    		mToast.cancel();
		     	    		mToast.show();
		     	    		txtDescricao.setText(s.toString().substring(0, s.toString().length()-1));
		     	    		txtDescricao.setSelection(s.toString().length()-1);
		     	    	}
		     	    	txtDescricao.addTextChangedListener(this);
		     	    }
		        } 
		    });
			
			txtPreco = (EditText) findViewById(R.id_editar_produto.txtPreco);
			txtPreco.requestFocus();
			
			// campo preco:
			txtPreco.setOnClickListener(new View.OnClickListener() {
				
				Toast mToast = Toast.makeText(getBaseContext(), "Digite o valor." , 5);
				public void onClick(View v) {
					mToast.cancel();
     	    		mToast.show();
					txtPreco.setSelection(txtPreco.getText().toString().length());
				}
			});

			txtPreco.addTextChangedListener(new TextWatcher() {

				Toast mToast = Toast.makeText(getBaseContext(), "Valor muito alto." , 5);
				
				public void afterTextChanged(Editable s) {
		            // TODO Auto-generated method stub
		        }

		        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		            // TODO Auto-generated method stub
		        }

		        public void onTextChanged(CharSequence s, int start, int before, int count) {

		        	if(!s.toString().equals(current)){
		        		txtPreco.removeTextChangedListener(this);
		        		String precoString = s.toString().replaceAll("[R$.]", "");
		     	    	precoString = precoString.replaceAll(",", ".");
		     	    	
		     	    	if (start == 10){
		     	    		mToast.cancel();
		     	    		mToast.show();
		     	    		precoString = precoString.substring(0, precoString.length()-1);
		     	    	} else {
		        		
			     	    	Double precoDouble = 0.0;
			     	    	
			     	    	if (s.length() > current.length()){
			     	    		precoDouble = Double.valueOf(precoString);
			     	    		precoDouble = precoDouble * 10;
			     	    	}
		
			     	    	if (s.length() < current.length()){
			     	    		precoDouble = Double.valueOf(precoString);
			     	    		precoDouble = precoDouble / 10;
			     	    	}
			     	    	
			     	    	precoString = precoDouble.toString();
		     	    	}
		     	    	NumberFormat formatoReal = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
		     			String formatado = formatoReal.format(Double.valueOf(precoString));
		     			txtPreco.setText(formatado);

		     	    	current = formatado;
		     	    	txtPreco.setText(formatado);
		     	    	txtPreco.setSelection(formatado.length());

		     	    	txtPreco.addTextChangedListener(this);
		     	    }

		        } 

		    });
			
			// botão comparar:
			btComparar = (Button) findViewById(R.id_editar_produto.btSalvar);
			btComparar.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					iconFavorito.setEnabled(false);
					btComparar.setEnabled(false);
					txtDescricao.setEnabled(false);
					txtPreco.setEnabled(false);
					
					option = 2;
					if (!txtPreco.getText().toString().contentEquals("R$0,00")){
						String precoDouble = txtPreco.getText().toString().replaceAll("[R$.]", "");
						precoDouble = precoDouble.replaceAll(",", ".");
						NumberFormat formatoDouble = NumberFormat.getNumberInstance(new Locale("en", "US"));
			 			String formatado = formatoDouble.format(Double.valueOf(precoDouble)).replaceAll(",", "");
	
			 			produto_vo.setId(new BigInteger(cod_barras));
						produto_vo.setDescricao(txtDescricao.getText().toString());
						produto_vo.setPreco(Double.valueOf(formatado));
						produto_vo.setLoja(loja);
						produto_vo.setFavorito(favorito);
						
						new ProgressTask().execute();
						
					} else {
						Toast.makeText(getBaseContext(), "Valor deve ser maior que zero." , Toast.LENGTH_SHORT).show();
					}
					iconFavorito.setEnabled(true);
					btComparar.setEnabled(true);
					txtDescricao.setEnabled(true);
					txtPreco.setEnabled(true);
					
				}
			});

			// icone favorito:
			
			iconFavorito = (ImageView) findViewById(R.id_editar_produto.iconFavorite);
			iconFavorito.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					if (favorito == 0){
						iconFavorito.setImageResource(R.drawable.favorito_on);
						favorito = 1;
					}else{
						iconFavorito.setImageResource(R.drawable.favorito_off);
						favorito = 0;
					}
				}
			});
			
			iconPhoto = (ImageView) findViewById(R.id_editar_produto.iconPhoto);
			iconPhoto.setOnClickListener(new View.OnClickListener() {
				
		        public void onClick(View arg0) {
					// TODO Auto-generated method stub
		        	local_foto = Environment.getExternalStorageDirectory() + "/Comparador/produto/"+produto_id+".png";
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
		
			produto_vo = produto_dao.get(produto_id);

			if (produto_vo.getDescricao().equalsIgnoreCase("Nenhum produto localizado.")){
				produto_vo.setDescricao(null);
				txtDescricao.requestFocus();
			}
			txtDescricao.setText(produto_vo.getDescricao());
			
			produto_vo_preco = produto_dao.get(loja, produto_id);
			if(produto_vo_preco.getPreco() > 0){
				NumberFormat formatoReal = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
 	 			String precoString = formatoReal.format(produto_vo_preco.getPreco());
 	 			current = precoString;
			} else{
				produto_vo.setId(produto_id);
				txtDescricao.requestFocus();
				current = "R$0,00";	
			}
			txtPreco.setText(current);
	 		txtPreco.setSelection(txtPreco.getText().toString().length());
			
			iconFavorito = (ImageView) findViewById(R.id_editar_produto.iconFavorite);
			if (produto_vo.getFavorito()){
		    	iconFavorito.setImageResource(R.drawable.favorito_on);
		    	favorito = 1;
			}else{
				iconFavorito.setImageResource(R.drawable.favorito_off);
				favorito = 0;
			}
			
			foto = (ImageView) findViewById(R.id_editar_produto.foto);
			if (produto_vo.getFoto() != null){
		    	foto.setImageBitmap(produto_vo.getFoto());
		    } else {
		    	foto.setImageResource(R.drawable.no_foto);
		    }

		} else {
			Toast.makeText(getBaseContext(), "Problemas na conexão. Tente novamente.", 5).show();
		}

		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(txtDescricao.getWindowToken(), 0);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	}
   
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TIRAR_FOTO) {
            if (resultCode == RESULT_OK) {
            	AlertDAO dao = new AlertDAO(getBaseContext());
            	foto = (ImageView) findViewById(R.id_editar_produto.foto);
            	ajustaFoto();
            	foto.setImageBitmap(dao.get(produto_id).getFoto());
                Toast.makeText(getBaseContext(), "Demais essa foto ein. Ficou muito legal.", Toast.LENGTH_LONG).show();
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
		
		try {
		       FileOutputStream out = new FileOutputStream(local_foto);
		       Bitmap bmp = bmpd.getBitmap();
		       Integer idx = 1;
		       
		       w = bmp.getWidth();
		       h = bmp.getHeight();
		       if ( w >= h){
		    	   idx = w / 256;
		       } else {
		    	   idx = h / 256;
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
					if (wb.setProduto(produto_vo)){
			 			buscou = 1;
						LojaDAO loja_dao = new LojaDAO(getBaseContext());
						String lojas_comparar = loja_dao.getLojasComparar();
					
						if (lojas_comparar.length() > 0){
							if (wb.getPreco(getBaseContext(), new BigInteger(cod_barras),lojas_comparar)){
								AlertDAO produto_dao = new AlertDAO(getBaseContext());
								Boolean result;
								if (produto_dao.existe(produto_vo.getId())){
					 				result = produto_dao.update(produto_vo);
					 			} else {
						 			result = produto_dao.insert(produto_vo);
					 			}
								if (result){
									Intent it = new Intent(getBaseContext(), ListarComparacao.class);
									it.putExtra("cod_barras", cod_barras);
									it.putExtra("ordem", true);
									it.putExtra("loja", loja);
									dialogo.dismiss();
					            	dialogo.cancel();
					            	startActivity(it);
					            	finish();	
								} else {
									Log.d("EditarProduto (445)","Problema desconhecido.");
								}
							} else {
								mToast.show();
							}	
						} else {
							mToastComparar.show();
						}
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
