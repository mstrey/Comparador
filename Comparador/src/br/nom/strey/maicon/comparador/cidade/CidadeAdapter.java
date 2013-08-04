package br.nom.strey.maicon.comparador.cidade;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import br.nom.strey.maicon.comparador.R;

public class CidadeAdapter extends BaseAdapter{
	private List<CidadeVO> lista;
	private Activity ctx;
	protected static final String CATEGORIA = "cidades";
	
	
	public CidadeAdapter(Activity ctx, List<CidadeVO> lista) {
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
		final CidadeVO cidade = lista.get(position);
		
		LayoutInflater layout = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = layout.inflate(R.layout.row_cidade, null);
		
		ImageView iconFavorite = (ImageView) v.findViewById(R.id_cidade.iconFavorite);
		
		if (!cidade.getFavorita()){
			iconFavorite.setImageResource(R.drawable.favorito_off);
		}else{
			iconFavorite.setImageResource(R.drawable.favorito_on);
		}
		
		TextView txtUF = (TextView) v.findViewById(R.id_cidade.txtUF);
		txtUF.setText(cidade.getUF());
		
		TextView txtNome = (TextView) v.findViewById(R.id_cidade.txtNome);
		txtNome.setText(cidade.getNome());
		
		return v;
	}


}
