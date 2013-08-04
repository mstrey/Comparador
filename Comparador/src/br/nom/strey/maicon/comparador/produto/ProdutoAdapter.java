package br.nom.strey.maicon.comparador.produto;

import java.io.File;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import br.nom.strey.maicon.comparador.DiferencaEm;
import br.nom.strey.maicon.comparador.R;

public class ProdutoAdapter extends BaseAdapter{
	private List<ProdutoVO> lista;
	private Activity ctx;
	File tempFile;
    Uri outputFileUri;
    String local_foto;
	
	public ProdutoAdapter(Activity ctx, List<ProdutoVO> lista) {
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
		final ProdutoVO produto_vo = lista.get(position);

		LayoutInflater layout = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = layout.inflate(R.layout.row_produto, null);
		
		TextView txtDescricao = (TextView) v.findViewById(R.id_produto.txtDescricao);
		txtDescricao.setText(produto_vo.getDescricao());
		
		TextView txtPreco = (TextView) v.findViewById(R.id_produto.txtPreco);
		NumberFormat formatoReal = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
		String preco = formatoReal.format(produto_vo.getPreco());
		txtPreco.setText(preco);
		
		TextView tempo = (TextView) v.findViewById(R.id_produto.txtTempo);
		
		Date dataConfirm = produto_vo.getDataConfirmacao();
		Date agora = new Date();
		
		String local_foto = Environment.getExternalStorageDirectory() + "/Comparador/produto/"+produto_vo.getId()+".png";
    	tempFile = new File(local_foto); 
        outputFileUri = Uri.fromFile(tempFile);
		ImageView fotoProduto = (ImageView) v.findViewById(R.id_produto.foto);
		
	    if (produto_vo.getFoto() != null){
	    	fotoProduto.setImageBitmap(produto_vo.getFoto());
	    } else {
	    	fotoProduto.setImageResource(R.drawable.no_foto);
	    }
		
		ImageView iconFavorito = (ImageView) v.findViewById(R.id_produto.iconFavorito);
		
		if (!produto_vo.getFavorito()){
			iconFavorito.setImageResource(R.drawable.favorito_off);
		}else{
			iconFavorito.setImageResource(R.drawable.favorito_on);
		}


		if (produto_vo.getId() != null){
			int meses = DiferencaEm.meses(dataConfirm, agora);
			int dias = DiferencaEm.dias(dataConfirm, agora);
			int horas = DiferencaEm.horas(dataConfirm, agora);
			int minutos = DiferencaEm.minutos(dataConfirm, agora);
			minutos = minutos - (dias * 3600);
			
			if (produto_vo.getPreco() == 0) {
				tempo.setText("não informado");
			} else {
				
				tempo.setText("1 minuto atrás");
				
				if (minutos > 1) {
					tempo.setText(minutos+" minutos atrás");
				}
				
				if (horas > 0) {
					tempo.setText(horas+" horas atrás");
					if (horas == 1){
						tempo.setText("1 hora atrás");
					}
				}
				
				if (dias > 0) {
					tempo.setText(dias+" dias atrás");
					if (dias == 1){
						tempo.setText("1 dia atrás");
					}
				}
				
				if (meses > 0) {
					tempo.setText(dias+" meses atrás");
					if (meses == 1){
						tempo.setText("1 mês atrás");
					}
				}
			}
			
		}
		return v;
	}

}
