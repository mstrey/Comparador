package br.nom.strey.maicon.comparador.loja;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import br.nom.strey.maicon.comparador.R;

public class LojaAdapter extends BaseAdapter{
	private List<LojaVO> lista;
	private Activity ctx;
	protected static final String FILE = "lojaAdapter";
	File tempFile;
    Uri outputFileUri;
    String local_foto;
	
	public LojaAdapter(Activity ctx, List<LojaVO> lista) {
		// TODO Auto-generated constructor stub
		this.ctx = ctx;
		this.lista = lista;
	}
	
	public int getCount() {
		// TODO Auto-generated method stub
		return lista.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return lista.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, final ViewGroup parent) {
		final LojaVO loja_vo = lista.get(position);

		LayoutInflater layout = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = layout.inflate(R.layout.row_loja, null);
		
		TextView txtNome = (TextView) v.findViewById(R.id_loja.txtNome);
		txtNome.setText(loja_vo.getNome());
		
		TextView txtLocal = (TextView) v.findViewById(R.id_loja.txtLocal);
		txtLocal.setText(loja_vo.getLocal());
		
		ImageView iconFavorita = (ImageView) v.findViewById(R.id_loja.iconFavorite);
		
		if (!loja_vo.getFavorita()){
			iconFavorita.setImageResource(R.drawable.favorito_off);
		}else{
			iconFavorita.setImageResource(R.drawable.favorito_on);
		}

		ImageView iconComparar = (ImageView) v.findViewById(R.id_loja.iconComparar);

		if (!loja_vo.getComparar()){
			iconComparar.setImageResource(R.drawable.comparar_off);
		}else{
			iconComparar.setImageResource(R.drawable.comparar_on);
		}
		
		String local_foto = Environment.getExternalStorageDirectory() + "/Comparador/loja/"+loja_vo.getId()+".png";
    	tempFile = new File(local_foto); 
        outputFileUri = Uri.fromFile(tempFile);
		
		ImageView fotoLoja = (ImageView) v.findViewById(R.id_loja.foto);
		
	    if (loja_vo.getFoto() != null){
	    	fotoLoja.setImageBitmap(loja_vo.getFoto());
	    } else {
	    	fotoLoja.setImageResource(R.drawable.no_foto);
	    }
		
		return v;
	}
}
