package br.nom.strey.maicon.comparador;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
import br.nom.strey.maicon.comparador.R;
import br.nom.strey.maicon.comparador.cidade.ListarCidades;

public class Comparador extends Activity {
    /** Called when the activity is first created. */
	protected static final String CATEGORIA = "principal";
	ListView listViewLojas;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_cidades);

        if (Environment.getExternalStorageState() != null){
            String local_foto_loja = Environment.getExternalStorageDirectory() + "/Comparador/loja/";
        	File folder = new File(local_foto_loja); 
            if (!folder.exists()){
            	if (folder.mkdirs()){
                	Log.d("Comparador (31): ", "Diretorio '"+local_foto_loja+"' criado");;
                }else {
                	Toast.makeText(getBaseContext(), "Erro ao criar diretório no cartão SD.", Toast.LENGTH_SHORT).show();	
				}
            }
        	String local_foto_produto = Environment.getExternalStorageDirectory() + "/Comparador/produto/";
            folder = new File(local_foto_produto); 
            if (!folder.exists()){
            	if (folder.mkdirs()){
                	Log.d("Comparador (40): ", "Diretorio '"+local_foto_produto+"' criado");;
                }else {
                	Toast.makeText(getBaseContext(), "Erro ao criar diretório no cartão SD.", Toast.LENGTH_SHORT).show();	
				}
            }
        }

        if (checkCameraHardware(getBaseContext())){
	        Intent it = new Intent(getBaseContext(), ListarCidades.class);
    		startActivity(it);
    		finish();	
        } else {
        	Toast.makeText(getBaseContext(), "Este aplicativo necessita de câmera!", Toast.LENGTH_LONG).show();
        }
		
    }
    
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            if (!getPackageManager().hasSystemFeature("android.hardware.camera.autofocus")){
            	Toast.makeText(getBaseContext(), "Sua câmera não possui autofocus, você pode ter problemas na leitura de códigos de barras!", 20).show();
            }
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
}