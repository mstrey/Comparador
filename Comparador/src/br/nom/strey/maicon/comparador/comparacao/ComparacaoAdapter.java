package br.nom.strey.maicon.comparador.comparacao;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import br.nom.strey.maicon.comparador.DiferencaEm;
import br.nom.strey.maicon.comparador.R;
import br.nom.strey.maicon.comparador.cidade.CidadeDAO;
import br.nom.strey.maicon.comparador.cidade.CidadeVO;
import br.nom.strey.maicon.comparador.loja.LojaDAO;
import br.nom.strey.maicon.comparador.loja.LojaVO;
import br.nom.strey.maicon.comparador.produto.ProdutoVO;

public class ComparacaoAdapter extends BaseAdapter{
	private List<ProdutoVO> lista;
	private Activity ctx;
	private ProdutoVO selecionado;
	
	public ComparacaoAdapter(Activity ctx, List<ProdutoVO> lista, ProdutoVO produto_selecionado) {
		// TODO Auto-generated constructor stub
		this.ctx = ctx;
		this.lista = lista;
		this.selecionado = produto_selecionado;
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
		LojaDAO loja_dao = new LojaDAO(ctx);
		LojaVO loja_vo = loja_dao.get(produto_vo.getLoja());

		LayoutInflater layout = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = layout.inflate(R.layout.row_comparacao, null);

		ImageView foto = (ImageView) v.findViewById(R.id_preco.foto);
		
		if (loja_vo.getFoto() == null){
			foto.setImageResource(R.drawable.no_foto);
		} else {
			foto.setImageBitmap(loja_vo.getFoto());
		}
		
		TextView txtLoja = (TextView) v.findViewById(R.id_preco.txtLoja);
		TextView txtLocal = (TextView) v.findViewById(R.id_preco.txtLocal);
		TextView txtCidade = (TextView) v.findViewById(R.id_preco.txtCidade);
		TextView txtPreco = (TextView) v.findViewById(R.id_preco.txtPreco);
		TextView txtPercentual = (TextView) v.findViewById(R.id_preco.txtPercentual);
		TextView tempo = (TextView) v.findViewById(R.id_preco.txtTempo);
		
		CidadeDAO cidade_dao = new CidadeDAO(ctx);
		NumberFormat formatoReal = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
		String preco = formatoReal.format(produto_vo.getPreco());
		
		String stringPercentual;
		Double percentual = ((produto_vo.getPreco()/selecionado.getPreco())-1.0)*100;
		
		if (selecionado.getPreco().toString().equals(produto_vo.getPreco().toString())){
			percentual = 0.0;
		}

		DecimalFormat formatoPercento = (DecimalFormat) DecimalFormat.getNumberInstance(new Locale("pt", "BR"));
		
		Date dataConfirm = produto_vo.getDataConfirmacao();
		Date agora = new Date();
		
		if (produto_vo.getId() != null){
			CidadeVO cidade_vo = cidade_dao.get(loja_vo.getCidade());

			if (selecionado.getPreco() >= produto_vo.getPreco()){
				txtPercentual.setTextColor(Color.GREEN);
				stringPercentual = "-";
			} else {
				txtPercentual.setTextColor(Color.RED);
				stringPercentual = "+";
			}
			stringPercentual += formatoPercento.format(Math.abs(percentual))+"%";
			
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
			txtLoja.setText(loja_vo.getNome());
			txtLocal.setText(loja_vo.getLocal());
			txtCidade.setText(cidade_vo.getNome());
			txtPreco.setText(preco);
			txtPercentual.setText(stringPercentual);
		} else {
			txtLoja.setText("Loja");
			txtLocal.setText("Local");
			txtCidade.setText("Cidade");
			txtPreco.setText("R$0,00");
			txtPercentual.setTextColor(Color.RED);
			txtPercentual.setText("0%");
		}
		return v;
	}

}
